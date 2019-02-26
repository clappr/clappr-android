package io.clappr.player.playback

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.drm.*
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.text.CaptionStyleCompat
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import io.clappr.player.base.*
import io.clappr.player.components.*
import io.clappr.player.log.Logger
import io.clappr.player.periodicTimer.PeriodicTimeElapsedHandler
import io.clappr.player.bitrate.BitrateHistory
import kotlin.math.min


open class ExoPlayerPlayback(source: String, mimeType: String? = null, options: Options = Options(), private val bitrateHistory: BitrateHistory = BitrateHistory()) : Playback(source, mimeType, options, name = entry.name, supportsSource = supportsSource) {
    companion object {
        private const val tag: String = "ExoPlayerPlayback"

        const val name = "exoplayerplayback"

        val supportsSource: PlaybackSupportCheck = { source, _ ->
            val uri = Uri.parse(source)
            val type = Util.inferContentType(uri.lastPathSegment)
            type == C.TYPE_SS || type == C.TYPE_HLS || type == C.TYPE_DASH || type == C.TYPE_OTHER
        }

        val entry = PlaybackEntry(
                name = name,
                supportsSource = supportsSource,
                factory = { source, mimeType, options -> ExoPlayerPlayback(source, mimeType, options) })
    }

    private val ONE_SECOND_IN_MILLIS: Int = 1000
    private val DEFAULT_MIN_DVR_SIZE = 60
    private val MIN_DVR_LIVE_DRIFT = 5
    private val DEFAULT_SYNC_BUFFER_IN_SECONDS = DefaultLoadControl.DEFAULT_MIN_BUFFER_MS / ONE_SECOND_IN_MILLIS

    open val minDvrSize by lazy { options[ClapprOption.MIN_DVR_SIZE.value] as? Int ?: DEFAULT_MIN_DVR_SIZE }

    protected var player: SimpleExoPlayer? = null
    protected val bandwidthMeter = DefaultBandwidthMeter()

    private val mainHandler = Handler()
    private val eventsListener = ExoplayerEventsListener()
    private val bitrateEventsListener = ExoplayerBitrateLogger()
    private val timeElapsedHandler = PeriodicTimeElapsedHandler(200L, { checkPeriodicUpdates() })
    private var lastBufferPercentageSent = 0.0
    private var currentState = State.NONE
    private var lastPositionSent = 0.0
    private var recoveredFromBehindLiveWindowException = false

    protected var trackSelector: DefaultTrackSelector? = null

    private val trackIndexKey = "trackIndexKey"
    private val trackGroupIndexKey = "trackGroupIndexKey"
    private val formatIndexKey = "formatIndexKey"
    private var needSetupMediaOptions = true

    private val dataSourceFactory = DefaultDataSourceFactory(applicationContext, "agent", bandwidthMeter)
    private var mediaSource: MediaSource? = null
    private val drmEventsListeners = ExoplayerDrmEventsListeners()
    private val drmScheme = C.WIDEVINE_UUID
    private val drmLicenseUrl: String?
        get() {
            return options.options[ClapprOption.DRM_LICENSE_URL.value] as? String
        }
    private val drmLicenses: ByteArray?
        get() {
            return options.options[ClapprOption.DRM_LICENSES.value] as? ByteArray
        }

    private val subtitlesFromOptions = options.options[ClapprOption.SUBTITLES.value] as? HashMap<String, String>

    private val useSubtitleFromOptions = subtitlesFromOptions?.isNotEmpty() ?: false

    private val bufferPercentage: Double
        get() = player?.bufferedPercentage?.toDouble() ?: 0.0

    private val playerView: PlayerView
        get() = view as PlayerView

    private var dvrStartTimeinSeconds: Long? = null

    override val viewClass: Class<*>
        get() = PlayerView::class.java

    override val mediaType: MediaType
        get() {
            player?.let {
                if (it.isCurrentWindowDynamic || it.duration == C.TIME_UNSET) return MediaType.LIVE else return MediaType.VOD
            }
            return MediaType.UNKNOWN
        }

    private val syncBufferInSeconds = if (mediaType == MediaType.LIVE) DEFAULT_SYNC_BUFFER_IN_SECONDS + MIN_DVR_LIVE_DRIFT else 0

    override val duration: Double
        get() = player?.duration?.let { (it.toDouble() / ONE_SECOND_IN_MILLIS) - syncBufferInSeconds } ?: Double.NaN

    override val position: Double
        get() = player?.currentPosition?.let { min(it.toDouble() / ONE_SECOND_IN_MILLIS, duration) } ?: Double.NaN

    override val state: State
        get() = currentState

    override val canPlay: Boolean
        get() = currentState == State.PAUSED ||
                currentState == State.IDLE ||
                (currentState == State.STALLING && player?.playWhenReady == false)

    override val canPause: Boolean
        get() = canPause(currentState) &&
                when (mediaType) {
                    MediaType.LIVE -> isDvrAvailable
                    else -> true
                }

    private fun canPause(state: State) =
            state == State.PLAYING || state == State.STALLING || state == State.IDLE


    override val canSeek: Boolean
        get() = currentState != State.ERROR &&
                when (mediaType) {
                    MediaType.LIVE -> isDvrAvailable
                    else -> duration != 0.0
                }

    override val isDvrAvailable: Boolean
        get() {
            val videoHasMinimumDurationForDvr = duration >= minDvrSize
            val isCurrentWindowSeekable = player?.isCurrentWindowSeekable ?: false
            return mediaType == MediaType.LIVE && videoHasMinimumDurationForDvr && isCurrentWindowSeekable
        }

    override val isDvrInUse: Boolean
        get() = exoplayerIsDvrInUse ?: false

    private var exoplayerIsDvrInUse: Boolean? = null
        set(value) {
            val oldValue = field

            field = value

            if (oldValue != field) {
                trigger(Event.DID_CHANGE_DVR_STATUS.value, Bundle().apply {
                    putBoolean("inUse", field ?: false)
                })
            }
        }

    private var lastDrvAvailableCheck: Boolean? = null

    private var lastBitrate: Int? = null
    set(value) {

        val oldValue = field

        field = value

        bitrateHistory.addBitrate(field)

        if(oldValue != field) {
            trigger(Event.DID_UPDATE_BITRATE.value, Bundle().apply { putInt(EventData.BITRATE.value, field ?: 0) })
        }
    }

    override val bitrate: Int
        get() = lastBitrate ?: 0

    override val avgBitrate: Long
        get() = bitrateHistory.averageBitrate()

    override val currentDate: Long?
        get() = dvrStartTimeinSeconds

    override val currentTime: Long?
        get() = currentDate?.plus(position.toLong())

    init {
        playerView.useController = false
        playerView.subtitleView?.setStyle(getSubtitleStyle())
    }


    open fun getSubtitleStyle() = CaptionStyleCompat(Color.WHITE, Color.TRANSPARENT, Color.TRANSPARENT, CaptionStyleCompat.EDGE_TYPE_NONE, Color.WHITE, null)

    override fun destroy() {
        release()
        super.destroy()
    }

    override fun play(): Boolean {
        if (player == null) setupPlayer()

        if (!canPlay && player != null) return false

        trigger(Event.WILL_PLAY)
        player?.playWhenReady = true
        return true
    }

    override fun pause(): Boolean {
        if (!canPause) return false

        trigger(Event.WILL_PAUSE)
        player?.playWhenReady = false
        return true
    }

    override fun stop(): Boolean {
        trigger(Event.WILL_STOP)
        player?.stop()
        release()
        trigger(Event.DID_STOP)
        return true
    }

    private fun release() {
        timeElapsedHandler.cancel()

        lastDrvAvailableCheck = null
        exoplayerIsDvrInUse = null

        needSetupMediaOptions = true
        mediaOptionList.clear()
        selectedMediaOptionList.clear()
        bitrateHistory.bitrateLogList.clear()

        removeListeners()

        player?.release()
        player = null
    }

    protected open fun removeListeners() {
        player?.removeListener(eventsListener)
        player?.removeAnalyticsListener(bitrateEventsListener)
    }

    override fun seek(seconds: Int): Boolean {
        if (!canSeek) return false

        trigger(Event.WILL_SEEK)
        player?.seekTo((seconds * ONE_SECOND_IN_MILLIS).toLong())
        trigger(Event.DID_SEEK)
        triggerPositionUpdateEvent()

        return true
    }

    override fun startAt(seconds: Int): Boolean {
        if (!canSeek) return false

        player?.seekTo((seconds * ONE_SECOND_IN_MILLIS).toLong())
        triggerPositionUpdateEvent()

        return true
    }

    override fun seekToLivePosition(): Boolean {
        if(!canSeek) return false

        player?.seekToDefaultPosition()
        return true
    }

    override fun load(source: String, mimeType: String?): Boolean {
        trigger(Event.WILL_LOAD_SOURCE)
        this.source = source
        this.mimeType = mimeType
        stop()
        setupPlayer()
        trigger(Event.DID_LOAD_SOURCE)
        return true
    }

    private fun mediaSource(uri: Uri): MediaSource {
        val mediaType = Util.inferContentType(uri.lastPathSegment)
        val dataSourceFactory = DefaultDataSourceFactory(applicationContext, "agent", bandwidthMeter)

        when (mediaType) {
            C.TYPE_DASH -> return DashMediaSource.Factory(DefaultDashChunkSource.Factory(dataSourceFactory), dataSourceFactory).createMediaSource(uri, mainHandler, null)
            C.TYPE_SS -> return  SsMediaSource.Factory(DefaultSsChunkSource.Factory(dataSourceFactory), dataSourceFactory).createMediaSource(uri, mainHandler, null)
            C.TYPE_HLS -> return HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri, mainHandler, null)
            C.TYPE_OTHER -> return ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri, mainHandler, null)
            else -> throw IllegalStateException("Unsupported type: " + mediaType)
        }
    }

    private fun setupPlayer() {
        val rendererFactory = setUpRendererFactory()

        configureTrackSelector()

        player = ExoPlayerFactory.newSimpleInstance(rendererFactory, trackSelector)
        player?.playWhenReady = false

        addListeners()

        playerView.player = player
        mediaSource = mediaSource(Uri.parse(source))
        player?.prepare(mediaSource)
    }

    protected open fun configureTrackSelector() {
        trackSelector = DefaultTrackSelector(AdaptiveTrackSelection.Factory(bandwidthMeter))
    }

    protected open fun addListeners() {
        player?.addListener(eventsListener)
        player?.addAnalyticsListener(bitrateEventsListener)
    }

    private fun setUpRendererFactory(): DefaultRenderersFactory {
        val rendererFactory = DefaultRenderersFactory(applicationContext,
                buildDrmSessionManager(), DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
        return rendererFactory
    }

    @SuppressLint("NewApi")
    private fun buildDrmSessionManager(): DrmSessionManager<FrameworkMediaCrypto>? {
        if (Util.SDK_INT < 18 || drmLicenseUrl == null) {
            return null
        }

        val defaultHttpDataSourceFactory = DefaultHttpDataSourceFactory(Util.getUserAgent(applicationContext, applicationContext.packageName), bandwidthMeter)
        val drmMediaCallback = HttpMediaDrmCallback(drmLicenseUrl, defaultHttpDataSourceFactory)
        var drmSessionManager : DrmSessionManager<FrameworkMediaCrypto>? = null

        try {
            drmSessionManager = DefaultDrmSessionManager(drmScheme, FrameworkMediaDrm.newInstance(drmScheme), drmMediaCallback, null)
                    .apply {
                        addListener(mainHandler, drmEventsListeners)
                        drmLicenses?.let { setMode(DefaultDrmSessionManager.MODE_QUERY, it) }
                    }
        }
        catch (drmException: UnsupportedDrmException) { handleError(drmException) }

        return drmSessionManager
    }

    private fun checkPeriodicUpdates() {
        updateDvrAvailableState()
        updateIsDvrInUse()

        if (bufferPercentage != lastBufferPercentageSent) triggerBufferUpdateEvent()
        if (position != lastPositionSent) triggerPositionUpdateEvent()
    }

    private fun triggerBufferUpdateEvent() {
        val bundle = Bundle()
        val currentBufferPercentage = bufferPercentage

        bundle.putDouble("percentage", currentBufferPercentage)
        trigger(Event.DID_UPDATE_BUFFER.value, bundle)
        lastBufferPercentageSent = currentBufferPercentage
    }

    private fun triggerPositionUpdateEvent() {
        val bundle = Bundle()
        val currentPosition = position
        val percentage = if (duration != 0.0) (currentPosition / duration) * 100 else 0.0

        bundle.putDouble("percentage", percentage)
        bundle.putDouble("time", currentPosition)
        trigger(Event.DID_UPDATE_POSITION.value, bundle)
        lastPositionSent = currentPosition
    }

    private fun updateDvrAvailableState() {
        if (isDvrAvailable == lastDrvAvailableCheck) return

        lastDrvAvailableCheck = isDvrAvailable
        trigger(Event.DID_CHANGE_DVR_AVAILABILITY.value, Bundle().apply {
            putBoolean("available", isDvrAvailable)
        })
        Logger.info(tag, "DVR Available: $isDvrAvailable")
    }

    private fun updateIsDvrInUse() {
        val forcedDvrValue = state != State.PLAYING
        val isInDvr = position < duration
        exoplayerIsDvrInUse = isDvrAvailable && (forcedDvrValue || isInDvr)
    }

    private fun updateState(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            Player.STATE_IDLE -> handleExoplayerIdleState()
            Player.STATE_ENDED -> handleExoplayerEndedState()
            Player.STATE_BUFFERING -> handleExoplayerBufferingState()
            Player.STATE_READY -> handleExoplayerReadyState(playWhenReady)
        }
    }

    private fun handleExoplayerReadyState(playWhenReady: Boolean) {
        if (currentState == State.NONE) {
            currentState = State.IDLE
            trigger(Event.READY)

            if (!playWhenReady) return
        }

        if (needSetupMediaOptions) {
            setUpMediaOptions()
            needSetupMediaOptions = false
        }

        if (playWhenReady) {
            currentState = State.PLAYING
            trigger(Event.PLAYING)
            timeElapsedHandler.start()
        } else {
            currentState = State.PAUSED
            trigger(Event.DID_PAUSE)
        }
    }

    private fun handleExoplayerBufferingState() {
        if (currentState != State.NONE) {
            currentState = State.STALLING
            trigger(Event.STALLING)
        }
    }

    private fun handleExoplayerEndedState() {
        currentState = State.IDLE
        trigger(Event.DID_COMPLETE)
        stop()
    }

    private fun handleExoplayerIdleState() {
        timeElapsedHandler.cancel()
        if(!recoveredFromBehindLiveWindowException) {
            currentState = State.NONE
        } else {
            recoveredFromBehindLiveWindowException = false
        }
    }

    private fun trigger(event: Event) {
        trigger(event.value)
    }

    protected fun handleError(error: Exception?) {
        if (error?.cause is BehindLiveWindowException) {
            Logger.info(tag, "BehindLiveWindowException")
            recoveredFromBehindLiveWindowException = true
            player?.prepare(mediaSource, false, false)
        } else if (currentState != State.ERROR) {
            timeElapsedHandler.cancel()
            currentState = State.ERROR
            triggerErrorEvent(error)
        }
    }

    private fun triggerErrorEvent(error: Exception?) {
        val bundle = Bundle()
        val message = error?.message ?: "Exoplayer Error"

        val errorExtra = Bundle()
        error?.let { errorExtra.putSerializable(ErrorInfoData.EXCEPTION.value, it) }

        bundle.putParcelable(Event.ERROR.value, ErrorInfo(message, ErrorCode.PLAYBACK_ERROR, errorExtra))
        trigger(Event.ERROR.value, bundle)
    }

    private fun setUpMediaOptions() {
        if (useSubtitleFromOptions) {
            setupAudioOptions()
            setupSubtitleOptionsFromClapprOptions()
        } else {
            setupAudioAndSubtitleOptions()
        }

        setDefaultMedias()
        checkInitialMedias()
        trigger(InternalEvent.MEDIA_OPTIONS_READY.value)
        Logger.info(tag, "MEDIA_OPTIONS_READY")
    }

    private fun setDefaultMedias() {
        if (availableMediaOptions(MediaOptionType.SUBTITLE).isNotEmpty()) {
            addAvailableMediaOption(SUBTITLE_OFF, 0)
            if (selectedMediaOption(MediaOptionType.SUBTITLE) == null)
                setSelectedMediaOption(SUBTITLE_OFF)
        }
        if (availableMediaOptions(MediaOptionType.AUDIO).isNotEmpty()) {
            if (selectedMediaOption(MediaOptionType.AUDIO) == null)
                setSelectedMediaOption(availableMediaOptions(MediaOptionType.AUDIO).first())
        }
    }

    private fun checkInitialMedias() {
        options.options[ClapprOption.SELECTED_MEDIA_OPTIONS.value]?.let {
            setupInitialMediasFromClapprOptions()
        }
    }

    private fun setupAudioOptions() {
        trackSelector?.currentMappedTrackInfo?.let {
            for (index in 0 until it.length) {
                when (player?.getRendererType(index)) {
                    C.TRACK_TYPE_AUDIO -> setUpOptions(index, it) { format, mediaInfo ->
                        createAudioMediaOption(format, mediaInfo)
                    }
                }
            }
        }
    }

    private fun setupAudioAndSubtitleOptions() {
        trackSelector?.currentMappedTrackInfo?.let {
            for (index in 0 until it.length) {
                when (player?.getRendererType(index)) {
                    C.TRACK_TYPE_AUDIO -> setUpOptions(index, it) { format, mediaInfo ->
                        createAudioMediaOption(format, mediaInfo)
                    }
                    C.TRACK_TYPE_TEXT -> setUpOptions(index, it) { format, mediaInfo ->
                        createSubtitleMediaOption(format, mediaInfo)
                    }
                }
            }
        }
    }

    private fun setupSubtitleOptionsFromClapprOptions() {
        subtitlesFromOptions?.forEach {
            val textFormat = Format.createTextSampleFormat(null, MimeTypes.APPLICATION_SUBRIP, Format.NO_VALUE, it.key, null)
            val subtitleSource = SingleSampleMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(it.value), textFormat, C.TIME_UNSET)

            createSubtitleMediaOption(textFormat, subtitleSource)?.let { addAvailableMediaOption(it) }
        }
    }

    private fun setUpOptions(renderedIndex: Int, trackGroups: MappingTrackSelector.MappedTrackInfo, createMediaOption: (format: Format, raw: Any?) -> MediaOption?) {
        trackGroups.forEachGroupIndexed(renderedIndex) { index, trackGroup ->
            addOptions(renderedIndex, index, trackGroup, createMediaOption)
        }
    }

    private fun addOptions(renderedIndex: Int, trackGroupIndex: Int, trackGroup: TrackGroup, createMediaOption: (format: Format, raw: Any?) -> MediaOption?) {
        trackGroup.forEachFormatIndexed { index, format ->
            val rawInfo = createMediaInfo(renderedIndex, trackGroupIndex, index)
            val mediaOption = createMediaOption(format, rawInfo)

            mediaOption?.let {
                addAvailableMediaOption(mediaOption)
                selectActualSelectedMediaOption(renderedIndex, format, mediaOption)
            }
        }
    }

    private fun selectActualSelectedMediaOption(renderedAudioIndex: Int, format: Format, mediaOption: MediaOption) {
        player?.let {
            if (it.currentTrackSelections.get(renderedAudioIndex)?.selectedFormat == format)
                setSelectedMediaOption(mediaOption)
        }
    }

    private fun createAudioMediaOption(format: Format, raw: Any?): MediaOption {
        return format.language?.let { createAudioMediaOptionFromLanguage(it, raw) } ?: createOriginalOption(raw)
    }

    private fun createSubtitleMediaOption(format: Format, raw: Any?): MediaOption {
        return format.language?.let { createSubtitleMediaOptionFromLanguage(it, raw) } ?: SUBTITLE_OFF
    }

    private fun createMediaInfo(renderedTextIndex: Int, trackGroupIndex: Int, formatIndex: Int) =
            Options().apply {
                put(trackIndexKey, renderedTextIndex)
                put(trackGroupIndexKey, trackGroupIndex)
                put(formatIndexKey, formatIndex)
            }

    override fun setSelectedMediaOption(mediaOption: MediaOption) {
        playerView.subtitleView.visibility = if (mediaOption == SUBTITLE_OFF) View.GONE else View.VISIBLE

        trackSelector?.currentMappedTrackInfo?.let {
            setMediaOptionOnPlayback(mediaOption, it)
            super.setSelectedMediaOption(mediaOption)
        }

        Logger.info(tag, "setSelectedMediaOption")
    }

    private fun setMediaOptionOnPlayback(mediaOption: MediaOption, mappedTrackInfo: MappingTrackSelector.MappedTrackInfo) {
        if (useSubtitleFromOptions && mediaOption.type == MediaOptionType.SUBTITLE)
            setSubtitleFromOptions(mediaOption)
        else
            setMediaOptionFromTracks(mediaOption, mappedTrackInfo)

    }

    private fun setSubtitleFromOptions(mediaOption: MediaOption) {
        var mergedSource = mediaSource
        if (mediaOption != SUBTITLE_OFF) {
            mergedSource = MergingMediaSource(mediaSource, mediaOption.raw as MediaSource)
        }
        player?.prepare(mergedSource, false, false)
    }

    private fun setMediaOptionFromTracks(mediaOption: MediaOption, mappedTrackInfo: MappingTrackSelector.MappedTrackInfo) {
        (mediaOption.raw as? Options)?.let {
            val trackIndex = it[trackIndexKey] as? Int
            val trackGroupIndexKey = it[trackGroupIndexKey] as? Int
            val formatIndexKey = it[formatIndexKey] as? Int

            if (trackIndex != null && trackGroupIndexKey != null && formatIndexKey != null) {
                trackSelector?.setParameters(DefaultTrackSelector.ParametersBuilder().setRendererDisabled(trackIndex, false))
                val selectionOverride = DefaultTrackSelector.SelectionOverride(trackGroupIndexKey, formatIndexKey)
                trackSelector?.setParameters(DefaultTrackSelector.ParametersBuilder().setSelectionOverride(trackIndex, mappedTrackInfo.getTrackGroups(trackIndex), selectionOverride))
            }
        }
    }

    private fun MappingTrackSelector.MappedTrackInfo.forEachGroupIndexed(renderedTextIndex: Int, function: (index: Int, trackGroup: TrackGroup) -> Unit) {
        val trackGroup = getTrackGroups(renderedTextIndex)
        for (index in 0 until trackGroup.length) {
            function(index, trackGroup.get(index))
        }
    }

    private fun TrackGroup.forEachFormatIndexed(function: (index: Int, format: Format) -> Unit) {
        for (index in 0 until length) {
            function(index, getFormat(index))
        }
    }

    inner class ExoplayerEventsListener: Player.EventListener{
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            updateState(playWhenReady, playbackState)
        }

        override fun onPlayerError(error: ExoPlaybackException?) {
            handleError(error)
        }

        override fun onLoadingChanged(isLoading: Boolean) {
            if (isLoading && currentState == State.NONE) {
                currentState = State.IDLE
                trigger(Event.READY.value)
            }
        }

        override fun onPositionDiscontinuity(reason: Int) {
        }

        override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
            timeline?.takeIf { isDvrAvailable && it.windowCount > 0 }?.let {
                var currentWindow = Timeline.Window()
                currentWindow = it.getWindow(0, currentWindow)
                dvrStartTimeinSeconds = currentWindow.windowStartTimeMs / ONE_SECOND_IN_MILLIS
            }
        }

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
        }

        override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
            Logger.info(tag, "onTracksChanged")
        }

        override fun onSeekProcessed() {
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        }

    }

    inner class ExoplayerBitrateLogger(trackSelector: MappingTrackSelector? = null) : EventLogger(trackSelector) {
        override fun onLoadCompleted(eventTime: AnalyticsListener.EventTime?, loadEventInfo: MediaSourceEventListener.LoadEventInfo?, mediaLoadData: MediaSourceEventListener.MediaLoadData?) {
            super.onLoadCompleted(eventTime, loadEventInfo, mediaLoadData)

            mediaLoadData?.let {
                if (it.trackType == C.TRACK_TYPE_DEFAULT || it.trackType == C.TRACK_TYPE_VIDEO){
                    it.trackFormat?.bitrate?.let { lastBitrate = it }
                }
            }
        }
    }

    inner class ExoplayerDrmEventsListeners : DefaultDrmSessionManager.EventListener {
        override fun onDrmKeysRestored() {
        }

        override fun onDrmKeysLoaded() {
        }

        override fun onDrmKeysRemoved() {
        }

        override fun onDrmSessionManagerError(error: java.lang.Exception?) {
            handleError(error)
        }
    }
}
