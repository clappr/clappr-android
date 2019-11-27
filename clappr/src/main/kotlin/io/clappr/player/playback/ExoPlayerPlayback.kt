package io.clappr.player.playback

import android.annotation.SuppressLint
import android.graphics.Color.TRANSPARENT
import android.graphics.Color.WHITE
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.C.*
import com.google.android.exoplayer2.DefaultLoadControl.DEFAULT_MIN_BUFFER_MS
import com.google.android.exoplayer2.Player.*
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.drm.*
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.text.CaptionStyleCompat
import com.google.android.exoplayer2.text.CaptionStyleCompat.EDGE_TYPE_NONE
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
import com.google.android.exoplayer2.util.Util.*
import io.clappr.player.base.*
import io.clappr.player.base.ClapprOption.*
import io.clappr.player.base.ErrorInfoData.EXCEPTION
import io.clappr.player.base.Event.*
import io.clappr.player.base.InternalEvent.DID_FIND_AUDIO
import io.clappr.player.base.InternalEvent.DID_FIND_SUBTITLE
import io.clappr.player.base.InternalEventData.*
import io.clappr.player.bitrate.BitrateHistory
import io.clappr.player.components.Playback
import io.clappr.player.components.Playback.MediaType.*
import io.clappr.player.components.PlaybackEntry
import io.clappr.player.components.PlaybackSupportCheck
import io.clappr.player.components.SubtitleLanguage
import io.clappr.player.log.Logger
import io.clappr.player.periodicTimer.PeriodicTimeElapsedHandler
import kotlin.math.min

open class ExoPlayerPlayback(
    source: String,
    mimeType: String? = null,
    options: Options = Options(),
    private val bitrateHistory: BitrateHistory = BitrateHistory { System.nanoTime() },
    protected val createDefaultTrackSelector: () -> DefaultTrackSelector = {
        DefaultTrackSelector(AdaptiveTrackSelection.Factory())
    }
) : Playback(source, mimeType, options, name = entry.name, supportsSource = supportsSource) {

    private var isVideoCompleted = false

    open val minDvrSize by lazy {
        options[MIN_DVR_SIZE.value] as? Int ?: DEFAULT_MIN_DVR_SIZE
    }

    protected var player: SimpleExoPlayer? = null
    protected val bandwidthMeter = DefaultBandwidthMeter()

    private val mainHandler = Handler()
    val eventsListener = ExoPlayerEventsListener()
    private val bitrateEventsListener = ExoPlayerBitrateLogger()
    private val timeElapsedHandler = PeriodicTimeElapsedHandler(200L) { checkPeriodicUpdates() }
    private var lastBufferPercentageSent = 0.0
    private var currentState = State.NONE
    private var lastPositionSent = 0.0
    private var recoveredFromBehindLiveWindowException = false

    protected var trackSelector: DefaultTrackSelector? = null

    private var shouldInitializeAudioAndSubtitles = true

    private val dataSourceFactory =
        DefaultDataSourceFactory(applicationContext, "agent", bandwidthMeter)
    private var mediaSource: MediaSource? = null
    private val drmEventsListeners = ExoPlayerDrmEventsListeners()
    private val drmScheme = WIDEVINE_UUID
    private val drmLicenseUrl: String?
        get() {
            return options.options[DRM_LICENSE_URL.value] as? String
        }
    private val drmLicenses: ByteArray?
        get() {
            return options.options[DRM_LICENSES.value] as? ByteArray
        }

    private val subtitlesFromOptions =
        (options.options[SUBTITLES.value] as? HashMap<String, String>).orEmpty()

    private val shouldUseExternalSubtitles = subtitlesFromOptions.isNotEmpty()

    private val bufferPercentage: Double
        get() = player?.bufferedPercentage?.toDouble() ?: 0.0

    private val playerView: PlayerView
        get() = view as PlayerView

    private var dvrStartTimeInSeconds: Long? = null

    override val viewClass: Class<*>
        get() = PlayerView::class.java

    override val mediaType: MediaType
        get() {
            player?.let {
                return if (it.isCurrentWindowDynamic || it.duration == TIME_UNSET) LIVE else VOD
            }
            return UNKNOWN
        }

    private val syncBufferInSeconds
        get() = if (mediaType == LIVE) DEFAULT_SYNC_BUFFER_IN_SECONDS + MIN_DVR_LIVE_DRIFT else 0

    override val duration: Double
        get() = player?.duration?.let { (it.toDouble() / ONE_SECOND_IN_MILLIS) - syncBufferInSeconds }
            ?: Double.NaN

    override val position: Double
        get() = player?.currentPosition?.let {
            min(it.toDouble() / ONE_SECOND_IN_MILLIS, duration)
        } ?: Double.NaN

    override val state: State
        get() = currentState

    override val canPlay: Boolean
        get() = currentState == State.PAUSED ||
                currentState == State.IDLE ||
                (currentState == State.STALLING && player?.playWhenReady == false)

    override val canPause: Boolean
        get() = canPause(currentState) &&
                when (mediaType) {
                    LIVE -> isDvrAvailable
                    else -> true
                }

    private fun canPause(state: State) =
        state == State.PLAYING || state == State.STALLING || state == State.IDLE


    override val canSeek: Boolean
        get() = currentState != State.ERROR &&
                !isVideoCompleted &&
                canSeekByMediaType

    private val canSeekByMediaType: Boolean
        get() = when (mediaType) {
            LIVE -> isDvrAvailable
            else -> duration != 0.0
        }

    override var volume: Float?
        get() = player?.volume
        set(volume) {
            volume?.let { player?.volume = it }
        }

    override val isDvrAvailable: Boolean
        get() {
            val videoHasMinimumDurationForDvr = duration >= minDvrSize
            val isCurrentWindowSeekable = player?.isCurrentWindowSeekable ?: false
            return mediaType == LIVE && videoHasMinimumDurationForDvr && isCurrentWindowSeekable
        }

    override val isDvrInUse: Boolean
        get() = exoplayerIsDvrInUse ?: false

    private var exoplayerIsDvrInUse: Boolean? = null
        set(value) {
            val oldValue = field

            field = value

            if (oldValue != field) {
                trigger(DID_CHANGE_DVR_STATUS.value, Bundle().apply {
                    putBoolean("inUse", field ?: false)
                })
            }
        }

    private var lastDrvAvailableCheck: Boolean? = null

    private var lastBitrate: Long? = null
        set(value) {

            val oldValue = field

            field = value

            try {
                bitrateHistory.addBitrate(field)
            } catch (e: BitrateHistory.BitrateLog.WrongTimeIntervalException) {
                Logger.error(name, e.message ?: "Can not add bitrate on history")
            }

            if (oldValue != field) {
                trigger(DID_UPDATE_BITRATE.value, Bundle().apply {
                    putLong(EventData.BITRATE.value, field ?: 0)
                })
            }
        }

    override val bitrate: Long
        get() = lastBitrate ?: 0L

    override val avgBitrate: Long
        get() = bitrateHistory.averageBitrate()

    override val currentDate: Long?
        get() = dvrStartTimeInSeconds

    override val currentTime: Long?
        get() = currentDate?.plus(position.toLong())

    private val isRepeatModeEnabled
        get() = player?.let {
            it.repeatMode == REPEAT_MODE_ONE &&
                    options.options.containsKey(LOOP.value) &&
                    mediaType == VOD
        } ?: false

    open val handleAudioFocus: Boolean
        get() = options.options[HANDLE_AUDIO_FOCUS.value] as? Boolean ?: false

    init {
        playerView.useController = false
        playerView.subtitleView?.setStyle(getSubtitleStyle())
    }

    open fun getSubtitleStyle() =
        CaptionStyleCompat(WHITE, TRANSPARENT, TRANSPARENT, EDGE_TYPE_NONE, WHITE, null)

    override fun destroy() {
        release()
        super.destroy()
    }

    override fun play(): Boolean {
        if (player == null) setupPlayer()

        if (!canPlay && player != null) return false

        trigger(WILL_PLAY)
        player?.playWhenReady = true
        isVideoCompleted = false
        return true
    }

    override fun pause(): Boolean {
        if (!canPause) return false

        trigger(WILL_PAUSE)
        player?.playWhenReady = false
        return true
    }

    override fun stop(): Boolean {
        trigger(WILL_STOP)
        player?.stop()
        release()
        currentState = State.IDLE
        trigger(DID_STOP)
        return true
    }

    private fun release() {
        timeElapsedHandler.cancel()

        lastDrvAvailableCheck = null
        exoplayerIsDvrInUse = null

        shouldInitializeAudioAndSubtitles = true
        availableAudios.clear()
        availableSubtitles.clear()
        internalSelectedAudio = null
        internalSelectedSubtitle = SubtitleLanguage.OFF.value
        bitrateHistory.clear()

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

        trigger(WILL_SEEK)
        player?.seekTo((seconds * ONE_SECOND_IN_MILLIS).toLong())
        trigger(DID_SEEK)
        triggerPositionUpdateEvent()

        return true
    }

    override fun startAt(seconds: Int): Boolean {
        val positionInMillis = seconds.toLong() * ONE_SECOND_IN_MILLIS

        if (!canSeek || positionInMillis < 0) return false
        player?.seekTo(positionInMillis)
        triggerPositionUpdateEvent()
        return true
    }

    override fun seekToLivePosition(): Boolean {
        if (!canSeek) return false

        trigger(WILL_SEEK)
        player?.seekToDefaultPosition()
        trigger(DID_SEEK)
        triggerPositionUpdateEvent()
        return true
    }

    override fun load(source: String, mimeType: String?): Boolean {
        trigger(WILL_LOAD_SOURCE)
        this.source = source
        this.mimeType = mimeType
        stop()
        setupPlayer()
        trigger(DID_LOAD_SOURCE)
        return true
    }

    private fun mediaSource(uri: Uri): MediaSource {
        val mediaType = inferContentType(uri.lastPathSegment)

        val dataSourceFactory =
            DefaultDataSourceFactory(applicationContext, "agent", bandwidthMeter)

        return when (mediaType) {
            TYPE_DASH -> DashMediaSource.Factory(
                DefaultDashChunkSource.Factory(dataSourceFactory),
                dataSourceFactory
            ).createMediaSource(uri)
            TYPE_SS -> SsMediaSource.Factory(
                DefaultSsChunkSource.Factory(dataSourceFactory),
                dataSourceFactory
            ).createMediaSource(uri)
            TYPE_HLS -> HlsMediaSource.Factory(
                dataSourceFactory
            ).createMediaSource(uri)
            TYPE_OTHER -> ExtractorMediaSource.Factory(
                dataSourceFactory
            ).createMediaSource(uri)
            else -> throw IllegalStateException("Unsupported type: $mediaType")
        }
    }

    private fun setupPlayer() {

        configureTrackSelector()

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(CONTENT_TYPE_MOVIE)
            .setUsage(USAGE_MEDIA)
            .build()

        player = ExoPlayerFactory.newSimpleInstance(
            applicationContext,
            buildRendererFactory(),
            trackSelector,
            DefaultLoadControl(),
            buildDrmSessionManager(),
            bandwidthMeter
        ).apply {
            setAudioAttributes(audioAttributes, handleAudioFocus)
            playWhenReady = false
            repeatMode = when (options.options[LOOP.value]) {
                true -> REPEAT_MODE_ONE
                else -> REPEAT_MODE_OFF
            }
        }

        addListeners()

        playerView.player = player
        mediaSource = mediaSource(Uri.parse(source))
        player?.prepare(mediaSource)
    }

    protected open fun configureTrackSelector() {
        trackSelector = createDefaultTrackSelector()
    }

    protected open fun addListeners() {
        player?.addListener(eventsListener)
        player?.addAnalyticsListener(bitrateEventsListener)
    }

    private fun buildRendererFactory() = DefaultRenderersFactory(
        applicationContext,
        DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
    )

    @SuppressLint("NewApi")
    private fun buildDrmSessionManager(): DrmSessionManager<FrameworkMediaCrypto>? {
        if (SDK_INT < 18 || drmLicenseUrl == null) {
            return null
        }

        val defaultHttpDataSourceFactory = DefaultHttpDataSourceFactory(
            getUserAgent(applicationContext, applicationContext.packageName),
            bandwidthMeter
        )
        val drmMediaCallback = HttpMediaDrmCallback(drmLicenseUrl, defaultHttpDataSourceFactory)
        var drmSessionManager: DrmSessionManager<FrameworkMediaCrypto>? = null

        try {
            drmSessionManager = DefaultDrmSessionManager(
                drmScheme,
                FrameworkMediaDrm.newInstance(drmScheme),
                drmMediaCallback,
                null
            ).apply {
                addListener(mainHandler, drmEventsListeners)
                drmLicenses?.let { setMode(DefaultDrmSessionManager.MODE_QUERY, it) }
            }
        } catch (drmException: UnsupportedDrmException) {
            handleError(drmException)
        }

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
        trigger(DID_UPDATE_BUFFER.value, bundle)
        lastBufferPercentageSent = currentBufferPercentage
    }

    private fun triggerPositionUpdateEvent() {
        val bundle = Bundle()
        val currentPosition = position
        val percentage = if (duration != 0.0) (currentPosition / duration) * 100 else 0.0

        bundle.putDouble("percentage", percentage)
        bundle.putDouble("time", currentPosition)
        trigger(DID_UPDATE_POSITION.value, bundle)
        lastPositionSent = currentPosition
    }

    private fun updateDvrAvailableState() {
        if (isDvrAvailable == lastDrvAvailableCheck) return

        lastDrvAvailableCheck = isDvrAvailable
        trigger(DID_CHANGE_DVR_AVAILABILITY.value, Bundle().apply {
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
            STATE_IDLE -> handleExoPlayerIdleState()
            STATE_ENDED -> handleExoPlayerEndedState()
            STATE_BUFFERING -> handleExoPlayerBufferingState()
            STATE_READY -> handleExoPlayerReadyState(playWhenReady)
        }
    }

    private fun handleExoPlayerReadyState(playWhenReady: Boolean) {
        if (currentState == State.NONE) {
            currentState = State.IDLE
            trigger(READY)

            if (!playWhenReady) return
        }

        if (shouldInitializeAudioAndSubtitles) {
            initializeAudioAndSubtitles()
            shouldInitializeAudioAndSubtitles = false
        }

        if (playWhenReady) {
            currentState = State.PLAYING
            trigger(PLAYING)
            timeElapsedHandler.start()
        } else {
            currentState = State.PAUSED
            trigger(DID_PAUSE)
        }
    }

    private fun handleExoPlayerBufferingState() {
        if (currentState != State.NONE) {
            currentState = State.STALLING
            trigger(STALLING)
        }
    }

    private fun handleExoPlayerEndedState() {
        currentState = State.IDLE
        trigger(DID_COMPLETE)
        isVideoCompleted = true
        stop()
    }

    private fun handleExoPlayerIdleState() {
        timeElapsedHandler.cancel()
        if (!recoveredFromBehindLiveWindowException) {
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
        error?.let { errorExtra.putSerializable(EXCEPTION.value, it) }

        bundle.putParcelable(ERROR.value, ErrorInfo(message, ErrorCode.PLAYBACK_ERROR, errorExtra))
        trigger(ERROR.value, bundle)
    }

    private fun initializeAudioAndSubtitles() {
        setupBuiltInAudios()

        if (shouldUseExternalSubtitles) {
            setupExternalSubtitles()
        } else {
            setupBuiltInSubtitles()
        }

        setupInitialMediasFromClapprOptions()

        selectFirstAudioIfAvailableAndUnset()
    }

    private fun selectFirstAudioIfAvailableAndUnset() {
        if (availableAudios.isNotEmpty() && selectedAudio == null)
            selectedAudio = availableAudios.first()
    }

    private fun setupBuiltInAudios() {
        val player = player ?: return
        val trackSelector = trackSelector ?: return
        val audioTracks = trackSelector.audioTracks()

        availableAudios += audioTracks.map { it.language }

        internalSelectedAudio = player.getSelectedAudio(trackSelector)

        if (audioTracks.any()) triggerDidFindAudio()
    }

    private fun setupBuiltInSubtitles() {
        val player = player ?: return
        val trackSelector = trackSelector ?: return
        val subtitleTracks = trackSelector.subtitleTracks()

        availableSubtitles += listOf(SubtitleLanguage.OFF.value) + subtitleTracks.map { it.language }

        internalSelectedSubtitle = player.getSelectedSubtitle(trackSelector)

        if (subtitleTracks.any()) triggerDidFindSubtitle()
    }

    private fun setupExternalSubtitles() {

        availableSubtitles += listOf(SubtitleLanguage.OFF.value) + subtitlesFromOptions.map { it.key }

        internalSelectedSubtitle = SubtitleLanguage.OFF.value

        if (subtitlesFromOptions.any()) triggerDidFindSubtitle()
    }

    private fun triggerDidFindAudio() {
        val data = Bundle().apply {
            putStringArrayList(FOUND_AUDIOS.value, ArrayList(availableAudios.toList()))
        }
        trigger(DID_FIND_AUDIO.value, data)
    }

    private fun triggerDidFindSubtitle() {
        val data = Bundle().apply {
            putStringArrayList(FOUND_SUBTITLES.value, ArrayList(availableSubtitles.toList()))
        }
        trigger(DID_FIND_SUBTITLE.value, data)
    }

    override var selectedAudio: String?
        get() = super.selectedAudio
        set(value) {
            if (value != null)
                setAudioOnPlayback(value)
            super.selectedAudio = value
            Logger.info(tag, "selectedAudio")
        }

    override var selectedSubtitle: String
        get() = super.selectedSubtitle
        set(value) {
            playerView.subtitleView.visibility =
                if (value == SubtitleLanguage.OFF.value) View.GONE else View.VISIBLE

            setSubtitleOnPlayback(value)
            super.selectedSubtitle = value
            Logger.info(tag, "selectedSubtitle")
        }

    private fun setAudioOnPlayback(language: String) = trackSelector?.setAudioFromTracks(language)

    private fun setSubtitleOnPlayback(language: String) = when {
        language == SubtitleLanguage.OFF.value -> turnOffSubtitle()
        shouldUseExternalSubtitles -> selectExternalSubtitle(language)
        else -> trackSelector?.selectSubtitleFromTracks(language)
    }

    private fun turnOffSubtitle() {
        player?.prepare(mediaSource, false, false)
    }

    private fun selectExternalSubtitle(language: String) {
        val player = player ?: return
        val uri = subtitlesFromOptions[language] ?: return

        val subtitleMediaSource = createExternalSubtitleMediaSource(language, uri)
        val mergingMediaSource = MergingMediaSource(mediaSource, subtitleMediaSource)

        player.prepare(mergingMediaSource, false, false)
    }

    private fun createExternalSubtitleMediaSource(language: String, uri: String): MediaSource {
        val textFormat = Format.createTextSampleFormat(
            null,
            MimeTypes.APPLICATION_SUBRIP,
            Format.NO_VALUE,
            language,
            null
        )
        return SingleSampleMediaSource.Factory(dataSourceFactory).createMediaSource(
            Uri.parse(uri),
            textFormat,
            TIME_UNSET
        )
    }

    inner class ExoPlayerEventsListener : EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            updateState(playWhenReady, playbackState)
        }

        override fun onPlayerError(error: ExoPlaybackException?) {
            handleError(error)
        }

        override fun onLoadingChanged(isLoading: Boolean) {
            if (isLoading && currentState == State.NONE) {
                currentState = State.IDLE
                trigger(READY.value)
            }
        }

        override fun onPositionDiscontinuity(reason: Int) {
            if (isRepeatModeEnabled && reason == DISCONTINUITY_REASON_PERIOD_TRANSITION) {
                trigger(DID_LOOP)
            }
        }

        override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
            timeline?.takeIf { isDvrAvailable && it.windowCount > 0 }?.let {
                var currentWindow = Timeline.Window()
                currentWindow = it.getWindow(0, currentWindow)
                dvrStartTimeInSeconds =
                    currentWindow.windowStartTimeMs / Companion.ONE_SECOND_IN_MILLIS
            }
        }

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {}

        override fun onTracksChanged(
            trackGroups: TrackGroupArray?,
            trackSelections: TrackSelectionArray?
        ) {
            Logger.info(tag, "onTracksChanged")
        }

        override fun onSeekProcessed() {}

        override fun onRepeatModeChanged(repeatMode: Int) {}

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}
    }

    inner class ExoPlayerBitrateLogger(trackSelector: MappingTrackSelector? = null) :
        EventLogger(trackSelector) {

        override fun onLoadCompleted(
            eventTime: AnalyticsListener.EventTime?,
            loadEventInfo: MediaSourceEventListener.LoadEventInfo?,
            mediaLoadData: MediaSourceEventListener.MediaLoadData?
        ) {
            super.onLoadCompleted(eventTime, loadEventInfo, mediaLoadData)

            mediaLoadData?.let { data ->
                if (data.trackType in listOf(TRACK_TYPE_DEFAULT, TRACK_TYPE_VIDEO)) {
                    data.trackFormat?.bitrate?.let { lastBitrate = it.toLong() }
                }
            }
        }
    }

    inner class ExoPlayerDrmEventsListeners : DefaultDrmSessionEventListener {
        override fun onDrmKeysRestored() {}

        override fun onDrmKeysLoaded() {}

        override fun onDrmKeysRemoved() {}

        override fun onDrmSessionManagerError(error: java.lang.Exception?) {
            handleError(error)
        }
    }

    companion object {
        private const val tag: String = "ExoPlayerPlayback"

        const val name = "exoplayerplayback"

        private const val ONE_SECOND_IN_MILLIS: Int = 1000
        private const val DEFAULT_MIN_DVR_SIZE = 60
        private const val MIN_DVR_LIVE_DRIFT = 5
        private const val DEFAULT_SYNC_BUFFER_IN_SECONDS =
            DEFAULT_MIN_BUFFER_MS / ONE_SECOND_IN_MILLIS

        val supportsSource: PlaybackSupportCheck = { source, _ ->
            val uri = Uri.parse(source)
            val type = inferContentType(uri.lastPathSegment)
            type == TYPE_SS || type == TYPE_HLS || type == TYPE_DASH || type == TYPE_OTHER
        }
        val entry = PlaybackEntry(
            name = name,
            supportsSource = supportsSource,
            factory = { source, mimeType, options -> ExoPlayerPlayback(source, mimeType, options) })
    }
}
