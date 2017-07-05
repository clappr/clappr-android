package io.clappr.player.playback

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.view.View
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.drm.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.text.CaptionStyleCompat
import com.google.android.exoplayer2.trackselection.*
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import io.clappr.player.base.*
import io.clappr.player.components.*
import io.clappr.player.log.Logger
import io.clappr.player.periodicTimer.PeriodicTimeElapsedHandler
import java.io.IOException
import java.util.HashMap

open class ExoPlayerPlayback(source: String, mimeType: String? = null, options: Options = Options()) : Playback(source, mimeType, options) {
    companion object : PlaybackSupportInterface {
        val tag: String = "ExoPlayerPlayback"

        override fun supportsSource(source: String, mimeType: String?): Boolean {
            val uri = Uri.parse(source)
            val type = Util.inferContentType(uri.lastPathSegment)
            return type == C.TYPE_SS || type == C.TYPE_HLS || type == C.TYPE_DASH || type == C.TYPE_OTHER
        }

        override val name: String = "exoplayerplayback"
    }

    private val ONE_SECOND_IN_MILLIS: Int = 1000

    private val mainHandler = Handler()
    private val eventsListener = ExoplayerEventsListener()
    private val timeElapsedHandler = PeriodicTimeElapsedHandler(200L, { checkPeriodicUpdates() })
    private var player: SimpleExoPlayer? = null
    private var lastBufferPercentageSent = 0.0
    private var trackSelector: DefaultTrackSelector? = null
    private var currentState = State.NONE
    private var lastPositionSent = 0.0

    private val trackIndexKey = "trackIndexKey"
    private val trackGroupIndexKey = "trackGroupIndexKey"
    private val formatIndexKey = "formatIndexKey"
    private var needSetupMediaOptions = true

    private val bandwidthMeter = DefaultBandwidthMeter()
    private val dataSourceFactory = DefaultDataSourceFactory(context, "agent", bandwidthMeter)
    private var mediaSource: MediaSource? = null
    private val drmEventsListeners = ExoplayerDrmEventsListeners()
    private val drmScheme = C.WIDEVINE_UUID
    private val drmLicenseUrl: String?
        get() {
            return options.options[ClapprOption.DRM_LICENSE_URL.value] as? String
        }
    private val subtitlesFromOptions = options.options[ClapprOption.SUBTITLES.value] as? HashMap<String, String>

    private val useSubtitleFromOptions = subtitlesFromOptions?.isNotEmpty() ?: false

    private val bufferPercentage: Double
        get() = player?.bufferedPercentage?.toDouble() ?: 0.0

    private val playerView: SimpleExoPlayerView
        get() = view as SimpleExoPlayerView

    override val viewClass: Class<*>
        get() = SimpleExoPlayerView::class.java

    override val duration: Double
        get() = player?.duration?.let { it.toDouble() / ONE_SECOND_IN_MILLIS } ?: Double.NaN

    override val position: Double
        get() = player?.currentPosition?.let { it.toDouble() / ONE_SECOND_IN_MILLIS } ?: Double.NaN

    override val state: State
        get() = currentState

    override val canPlay: Boolean
        get() = currentState == State.PAUSED ||
                currentState == State.IDLE ||
                (currentState == State.STALLED && player?.playWhenReady == false)

    override val canPause: Boolean
        get() = currentState == State.PLAYING ||
                currentState == State.STALLED ||
                currentState == State.IDLE


    override val canSeek: Boolean
        get() = duration != 0.0 && currentState != State.ERROR

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
        player?.removeListener(eventsListener)
        player?.release()
        player = null
    }

    override fun seek(seconds: Int): Boolean {
        if (!canSeek) return false

        trigger(Event.WILL_SEEK)
        player?.seekTo((seconds * 1000).toLong())
        trigger(Event.DID_SEEK)
        triggerPositionUpdateEvent()
        return true
    }

    override fun load(source: String, mimeType: String?): Boolean {
        trigger(Event.WILL_CHANGE_SOURCE)
        this.source = source
        this.mimeType = mimeType
        stop()
        setupPlayer()
        trigger(Event.DID_CHANGE_SOURCE)
        return true
    }

    private fun mediaSource(uri: Uri): MediaSource {
        val mediaType = Util.inferContentType(uri.lastPathSegment)
        val dataSourceFactory = DefaultDataSourceFactory(context, "agent", bandwidthMeter)

        when (mediaType) {
            C.TYPE_DASH -> return DashMediaSource(uri, dataSourceFactory, DefaultDashChunkSource.Factory(dataSourceFactory), mainHandler, eventsListener)
            C.TYPE_SS -> return SsMediaSource(uri, dataSourceFactory, DefaultSsChunkSource.Factory(dataSourceFactory), mainHandler, eventsListener)
            C.TYPE_HLS -> return HlsMediaSource(uri, dataSourceFactory, mainHandler, eventsListener)
            C.TYPE_OTHER -> return ExtractorMediaSource(uri, dataSourceFactory, DefaultExtractorsFactory(), mainHandler, eventsListener)
            else -> throw IllegalStateException("Unsupported type: " + mediaType)
        }
    }

    private fun setupPlayer() {
        val rendererFactory = setUpRendererFactory()
        trackSelector = DefaultTrackSelector(AdaptiveTrackSelection.Factory(bandwidthMeter))

        player = ExoPlayerFactory.newSimpleInstance(rendererFactory, trackSelector)
        player?.playWhenReady = false
        player?.addListener(eventsListener)
        playerView.player = player
        mediaSource = mediaSource(Uri.parse(source))
        player?.prepare(mediaSource)
    }

    private fun setUpRendererFactory(): DefaultRenderersFactory {
        val rendererFactory = DefaultRenderersFactory(context,
                buildDrmSessionManager(), DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
        return rendererFactory
    }
    
    @SuppressLint("NewApi")
    fun buildDrmSessionManager(): DrmSessionManager<FrameworkMediaCrypto>? {
        if (Util.SDK_INT < 18 || drmLicenseUrl == null) {
            return null
        }

        val defaultHttpDataSourceFactory = DefaultHttpDataSourceFactory(Util.getUserAgent(context, context?.packageName), bandwidthMeter)

        val drmMediaCallback = HttpMediaDrmCallback(drmLicenseUrl, defaultHttpDataSourceFactory)

        return DefaultDrmSessionManager(drmScheme, FrameworkMediaDrm.newInstance(drmScheme), drmMediaCallback, null, mainHandler, drmEventsListeners)
    }

    private fun checkPeriodicUpdates() {
        if (bufferPercentage != lastBufferPercentageSent) triggerBufferUpdateEvent()
        if (position != lastPositionSent) triggerPositionUpdateEvent()
    }

    private fun triggerBufferUpdateEvent() {
        val bundle = Bundle()
        val currentBufferPercentage = bufferPercentage

        bundle.putDouble("percentage", currentBufferPercentage)
        trigger(Event.BUFFER_UPDATE.value, bundle)
        lastBufferPercentageSent = currentBufferPercentage
    }

    private fun triggerPositionUpdateEvent() {
        val bundle = Bundle()
        val currentPosition = position
        val percentage = if (duration != 0.0) (currentPosition / duration) * 100 else 0.0

        bundle.putDouble("percentage", percentage)
        bundle.putDouble("time", currentPosition)
        trigger(Event.POSITION_UPDATE.value, bundle)
        lastPositionSent = currentPosition
    }

    private fun updateState(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            ExoPlayer.STATE_IDLE -> handleExoplayerIdleState()
            ExoPlayer.STATE_ENDED -> handleExoplayerEndedState()
            ExoPlayer.STATE_BUFFERING -> handleExoplayerBufferingState()
            ExoPlayer.STATE_READY -> handleExoplayerReadyState(playWhenReady)
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
            currentState = State.STALLED
            trigger(Event.STALLED)
        }
    }

    private fun handleExoplayerEndedState() {
        currentState = State.IDLE
        trigger(Event.DID_COMPLETE)
        stop()
    }

    private fun handleExoplayerIdleState() {
        timeElapsedHandler.cancel()
        currentState = State.NONE
    }

    private fun trigger(event: Event) {
        trigger(event.value)
    }

    protected fun handleError(error: Exception?) {
        if (currentState != State.ERROR) {
            timeElapsedHandler.cancel()
            currentState = State.ERROR
            triggerErrorEvent(error)
        }
    }

    private fun triggerErrorEvent(error: Exception?) {
        val bundle = Bundle()
        val message = error?.message ?: "Exoplayer Error"
        bundle.putParcelable(Event.ERROR.value, ErrorInfo(message, ErrorCode.PLAYBACK_ERROR))
        trigger(Event.ERROR.value, bundle)
    }

    private fun setUpMediaOptions() {
        if (useSubtitleFromOptions) {
            setupAudioOptions()
            setupSubtitlesFromClapprOptions()
        } else {
            setupAudioAndSubtitleOptions()
        }

        setDefaultMedias()
        trigger(InternalEvent.MEDIA_OPTIONS_READY.value)
        Logger.info("MEDIA_OPTIONS_READY", tag)
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

    private fun setupSubtitlesFromClapprOptions() {
        subtitlesFromOptions?.forEach {
            val textFormat = Format.createTextSampleFormat(null, MimeTypes.APPLICATION_SUBRIP, null, Format.NO_VALUE, Format.NO_VALUE, it.key, null)
            val subtitleSource = SingleSampleMediaSource(Uri.parse(it.value), dataSourceFactory, textFormat, C.TIME_UNSET)
            addAvailableMediaOption(createSubtitleMediaOption(textFormat, subtitleSource))
        }
    }

    private fun setUpOptions(renderedIndex: Int, trackGroups: MappingTrackSelector.MappedTrackInfo, block: (format: Format, mediaInfo: Options) -> MediaOption?) {
        trackGroups.forEachGroupIndexed(renderedIndex) { index, trackGroup ->
            addOptions(renderedIndex, index, trackGroup, block)
        }
    }

    private fun addOptions(renderedIndex: Int, trackGroupIndex: Int, trackGroup: TrackGroup, block: (format: Format, mediaInfo: Options) -> MediaOption?) {
        trackGroup.forEachFormatIndexed { index, format ->
            format.language?.let {
                val mediaInfo = createMediaInfo(renderedIndex, trackGroupIndex, index)
                val mediaOption = block(format, mediaInfo)

                mediaOption?.let {
                    addAvailableMediaOption(mediaOption)
                    selectActualSelectedMediaOption(renderedIndex, format, mediaOption)
                }
            }
        }
    }

    private fun selectActualSelectedMediaOption(renderedAudioIndex: Int, format: Format, mediaOption: MediaOption) {
        player?.let {
            if (it.currentTrackSelections.get(renderedAudioIndex)?.selectedFormat == format)
                setSelectedMediaOption(mediaOption)
        }
    }

    private fun createAudioMediaOption(format: Format, mediaInfo: Options): MediaOption? {
        return when (format.language) {
            "und" -> MediaOption(MediaOptionType.Audio.ORIGINAL.value, MediaOptionType.AUDIO, mediaInfo, null)
            "pt" -> MediaOption(MediaOptionType.Audio.PT_BR.value, MediaOptionType.AUDIO, mediaInfo, null)
            "en" -> MediaOption(MediaOptionType.Audio.EN.value, MediaOptionType.AUDIO, mediaInfo, null)
            null -> createAudioOffOption(mediaInfo)
            else -> MediaOption(format.language, MediaOptionType.AUDIO, mediaInfo, null)
        }
    }

    private fun createAudioOffOption(mediaInfo: Options) = MediaOption(MediaOptionType.Audio.ORIGINAL.value, MediaOptionType.AUDIO, mediaInfo, null)

    private fun createSubtitleMediaOption(format: Format, raw: Any?): MediaOption {
        val mediaOption = when (format.language) {
            "pt" -> MediaOption(MediaOptionType.Language.PT_BR.value, MediaOptionType.SUBTITLE, raw, null)
            else -> MediaOption(format.language, MediaOptionType.SUBTITLE, raw, null)
        }

        return mediaOption
    }

    private fun createMediaInfo(renderedTextIndex: Int, trackGroupIndex: Int, formatIndex: Int): Options {
        val mediaInfo = Options()
        mediaInfo.put(trackIndexKey, renderedTextIndex)
        mediaInfo.put(trackGroupIndexKey, trackGroupIndex)
        mediaInfo.put(formatIndexKey, formatIndex)
        return mediaInfo
    }

    override fun setSelectedMediaOption(mediaOption: MediaOption) {
        playerView.subtitleView.visibility = if (mediaOption == SUBTITLE_OFF) View.GONE else View.VISIBLE

        trackSelector?.currentMappedTrackInfo?.let {
            setMediaOptionOnPlayback(mediaOption, it)
            super.setSelectedMediaOption(mediaOption)
        }

        Logger.info("setSelectedMediaOption", tag)
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
                trackSelector?.setRendererDisabled(trackIndex, false)
                val selectionOverride = MappingTrackSelector.SelectionOverride(FixedTrackSelection.Factory(), trackGroupIndexKey, formatIndexKey)
                trackSelector?.setSelectionOverride(trackIndex, mappedTrackInfo.getTrackGroups(trackIndex), selectionOverride)
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

    inner class ExoplayerEventsListener : AdaptiveMediaSourceEventListener, ExtractorMediaSource.EventListener, ExoPlayer.EventListener {
        override fun onLoadError(error: IOException?) {
            handleError(error)
        }

        override fun onLoadError(dataSpec: DataSpec?, dataType: Int, trackType: Int, trackFormat: Format?, trackSelectionReason: Int, trackSelectionData: Any?, mediaStartTimeMs: Long, mediaEndTimeMs: Long, elapsedRealtimeMs: Long, loadDurationMs: Long, bytesLoaded: Long, error: IOException?, wasCanceled: Boolean) {
            handleError(error)
        }

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

        override fun onPositionDiscontinuity() {
        }

        override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {
        }

        override fun onLoadStarted(dataSpec: DataSpec?, dataType: Int, trackType: Int, trackFormat: Format?, trackSelectionReason: Int, trackSelectionData: Any?, mediaStartTimeMs: Long, mediaEndTimeMs: Long, elapsedRealtimeMs: Long) {
        }

        override fun onDownstreamFormatChanged(trackType: Int, trackFormat: Format?, trackSelectionReason: Int, trackSelectionData: Any?, mediaTimeMs: Long) {
        }

        override fun onUpstreamDiscarded(trackType: Int, mediaStartTimeMs: Long, mediaEndTimeMs: Long) {
        }

        override fun onLoadCanceled(dataSpec: DataSpec?, dataType: Int, trackType: Int, trackFormat: Format?, trackSelectionReason: Int, trackSelectionData: Any?, mediaStartTimeMs: Long, mediaEndTimeMs: Long, elapsedRealtimeMs: Long, loadDurationMs: Long, bytesLoaded: Long) {
        }

        override fun onLoadCompleted(dataSpec: DataSpec?, dataType: Int, trackType: Int, trackFormat: Format?, trackSelectionReason: Int, trackSelectionData: Any?, mediaStartTimeMs: Long, mediaEndTimeMs: Long, elapsedRealtimeMs: Long, loadDurationMs: Long, bytesLoaded: Long) {
        }

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
        }

        override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
            Logger.info("onTracksChanged", tag)
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
