package io.clappr.player.playback

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.trackselection.*
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import io.clappr.player.base.*
import io.clappr.player.components.*
import io.clappr.player.periodicTimer.PeriodicTimeElapsedHandler
import java.io.IOException

open class ExoPlayerPlayback(source: String, mimeType: String? = null, options: Options = Options()) : Playback(source, mimeType, options) {
    companion object : PlaybackSupportInterface {
        override fun supportsSource(source: String, mimeType: String?): Boolean {
            val uri = Uri.parse(source)
            val type = Util.inferContentType(uri.lastPathSegment)
            return type == C.TYPE_SS || type == C.TYPE_HLS || type == C.TYPE_DASH || type == C.TYPE_OTHER
        }

        override val name: String = "exoplayerplayback"
    }

    private val ONE_SECOND_IN_MILLIS: Int = 1000

    private val mainHandler = Handler()
    private val bandwidthMeter = DefaultBandwidthMeter()
    private val eventsListener = ExoplayerEventsListener()
    private var player: SimpleExoPlayer? = null
    private var currentState = State.NONE
    private var trackSelector: DefaultTrackSelector? = null
    private val timeElapsedHandler = PeriodicTimeElapsedHandler(200L, { checkPeriodicUpdates() })
    private var lastBufferPercentageSent = 0.0
    private var lastPositionSent = 0.0

    private var needSetupMediaOptions = true
    private val trackIndexKey = "trackIndexKey"
    private val trackGroupIndexKey = "trackGroupIndexKey"
    private val formatIndexKey = "formatIndexKey"
    private var subtitleOff: MediaOption? = null

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
    }

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
        val type = Util.inferContentType(uri.lastPathSegment)
        val dataSourceFactory = DefaultDataSourceFactory(context, "agent", bandwidthMeter)

        when (type) {
            C.TYPE_DASH -> return DashMediaSource(uri, dataSourceFactory, DefaultDashChunkSource.Factory(dataSourceFactory), mainHandler, eventsListener)
            C.TYPE_SS -> return SsMediaSource(uri, dataSourceFactory, DefaultSsChunkSource.Factory(dataSourceFactory), mainHandler, eventsListener)
            C.TYPE_HLS -> return HlsMediaSource(uri, dataSourceFactory, mainHandler, eventsListener)
            C.TYPE_OTHER -> return ExtractorMediaSource(uri, dataSourceFactory, DefaultExtractorsFactory(), mainHandler, eventsListener)
            else -> throw IllegalStateException("Unsupported type: " + type)
        }
    }

    private fun setupPlayer() {
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector)
        player?.playWhenReady = false
        player?.addListener(eventsListener)
        playerView.player = player
        player?.prepare(mediaSource(Uri.parse(source)))
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

    private fun handleError(error: Exception?) {
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

        }
    }

    private fun setUpMediaOptions() {
        setupAudioAndSubtitleOptions()
        setDefaultSubtitle()
        trigger(InternalEvent.MEDIA_OPTIONS_READY.value)
    }

    private fun setDefaultSubtitle() {
        if (selectedMediaOption(MediaOptionType.SUBTITLE) == null) {
            setSelectedMediaOption(SUBTITLE_OFF)
        }
    }

    private fun setupAudioAndSubtitleOptions() {
        trackSelector?.currentMappedTrackInfo?.let {
            (0..it.length - 1).forEachIndexed { index, _ ->
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

    private fun setUpOptions(renderedIndex: Int, trackGroups: MappingTrackSelector.MappedTrackInfo, block: (format: Format, mediaInfo: Options) -> MediaOption?) {
        trackGroups.forEachGroupIndexed(renderedIndex) { index, trackGroup ->
            addOptions(renderedIndex, index, trackGroup, block)
        }
    }

    private fun addOptions(renderedIndex: Int, trackGroupIndex: Int, trackGroup: TrackGroup, block: (format: Format, mediaInfo: Options) -> MediaOption?) {
        trackGroup.forEachFormatIndexed { index, format ->

            val mediaInfo = createMediaInfo(renderedIndex, trackGroupIndex, index)
            val mediaOption = block(format, mediaInfo)

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

    private fun createAudioMediaOption(format: Format, mediaInfo: Options): MediaOption? {
        return when (format.language) {
            "und" -> MediaOption(MediaOptionType.Audio.ORIGINAL.value, MediaOptionType.AUDIO, format, mediaInfo)
            "pt" -> MediaOption(MediaOptionType.Audio.PT_BR.value, MediaOptionType.AUDIO, format, mediaInfo)
            null -> createAudioOffOption(format, mediaInfo)
            else -> MediaOption(format.language, MediaOptionType.AUDIO, format, mediaInfo)
        }
    }

    private fun createAudioOffOption(format: Format, mediaInfo: Options) = MediaOption("Original", MediaOptionType.AUDIO, format, mediaInfo)

    private fun createSubtitleMediaOption(format: Format, mediaInfo: Options): MediaOption {
        val mediaOption = when (format.language) {
            "pt" -> MediaOption(MediaOptionType.Language.PT_BR.value, MediaOptionType.SUBTITLE, format, mediaInfo)
            null -> createSubtitleOffOption(format, mediaInfo)
            else -> MediaOption(format.language, MediaOptionType.SUBTITLE, format, mediaInfo)
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

    private fun createSubtitleOffOption(format: Format, mediaInfo: Options): MediaOption {
        subtitleOff = MediaOption(SUBTITLE_OFF.name, MediaOptionType.SUBTITLE, format, mediaInfo)
        return SUBTITLE_OFF
    }

    override fun setSelectedMediaOption(mediaOption: MediaOption) {
        trackSelector?.currentMappedTrackInfo?.let {
            setMediaOption(mediaOption, it)
            super.setSelectedMediaOption(mediaOption)
        }
    }

    private fun setMediaOption(mediaOption: MediaOption, mappedTrackInfo: MappingTrackSelector.MappedTrackInfo) {
        if (mediaOption == SUBTITLE_OFF) {
            subtitleOff?.let { setMediaOptionOnPlayback(it, mappedTrackInfo) }
        } else {
            setMediaOptionOnPlayback(mediaOption, mappedTrackInfo)
        }
    }

    private fun setMediaOptionOnPlayback(mediaOption: MediaOption, mappedTrackInfo: MappingTrackSelector.MappedTrackInfo) {
        mediaOption.info?.let {
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
        (0..(trackGroup.length - 1)).forEachIndexed { index, _ ->
            function(index, trackGroup.get(index))
        }
    }

    private fun TrackGroup.forEachFormatIndexed(function: (index: Int, format: Format) -> Unit) {
        (0..(length - 1)).forEachIndexed { index, _ -> function(index, getFormat(index)) }
    }
}
