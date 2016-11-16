package com.globo.clappr.playback

import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.ViewGroup
import com.globo.clappr.base.Event
import com.globo.clappr.base.Options
import com.globo.clappr.components.Playback
import com.globo.clappr.components.PlaybackSupportInterface
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import java.io.IOException
import java.util.*

open class ExoPlayerPlayback(source: String, mimeType: String? = null, options: Options = Options()) : Playback(source, mimeType, options), ExoPlayer.EventListener {
    companion object : PlaybackSupportInterface {
        override fun supportsSource(source: String, mimeType: String?): Boolean {
            val uri = Uri.parse(source)
            val type = Util.inferContentType(uri.lastPathSegment)
            return type == C.TYPE_SS || type == C.TYPE_HLS || type == C.TYPE_DASH || type == C.TYPE_OTHER
        }

        override val name: String = "exoplayerplayback"
        const val TAG = "ExoplayerEvent"

        var containerView: ViewGroup? = null
    }

    val mainHandler = Handler()
    val bandwidthMeter = DefaultBandwidthMeter()
    var playerView = SimpleExoPlayerView(context)
    val mediaSourceLogger = MediaSourceLogger()
    var player: SimpleExoPlayer? = null
    var currentState = State.NONE
    private var trackSelector: DefaultTrackSelector? = null
    var timeElapsedEventsDispatcher: TimeElapsedManager? = null

    override val duration: Double
        get() = player?.duration?.toDouble() ?: 0.0

    override val state: State
        get() = currentState

    override val canPlay: Boolean
        get() = currentState == State.PAUSED ||
                currentState == State.IDLE ||
                (currentState == State.STALLED && player?.playWhenReady == false)

    override val canPause: Boolean
        get() = currentState == State.PLAYING

    override val canSeek: Boolean
        get() = duration != 0.0 && currentState != State.IDLE

    override fun play() {
        triggerEventWithLog(Event.WILL_PLAY)
        if (currentState == State.IDLE) {
            load(source, mimeType)
        }
        player?.playWhenReady = true
        return true
    }

    override fun pause() {
        triggerEventWithLog(Event.WILL_PAUSE)
        player?.playWhenReady = false
        return true
    }

    override fun stop() {
        triggerEventWithLog(Event.WILL_STOP)
        player?.stop()
        return true
    }

    override fun seek(seconds: Int) {
        triggerEventWithLog(Event.WILL_SEEK)
        player?.seekTo((seconds * 1000).toLong())
        return true
    }

    override fun load(source: String, mimeType: String?): Boolean {
        player?.prepare(mediaSource(Uri.parse(source)))
        return true
    }

    init {
        setupPlayer()
        setUpTimeElapsedCallBacks()
        load(source, mimeType)
    }

    fun setupPlayer() {
        val videoTrackSelectionFactory = AdaptiveVideoTrackSelection.Factory(bandwidthMeter)
        trackSelector = DefaultTrackSelector(mainHandler, videoTrackSelectionFactory)
        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector, DefaultLoadControl())
        player?.playWhenReady = true
        player?.addListener(this)
        setupPlayerView()
    }

    private fun setUpTimeElapsedCallBacks() {
        timeElapsedEventsDispatcher = TimeElapsedManager({ dispatchTimeElapsedEvents() })
    }

    fun setupPlayerView() {
        containerView?.addView(playerView)
        playerView.player = player
    }

    fun mediaSource(uri: Uri): MediaSource {
        val type = Util.inferContentType(uri.lastPathSegment)
        val dataSourceFactory = DefaultDataSourceFactory(context, "agent", bandwidthMeter)

        when (type) {
            C.TYPE_DASH -> return DashMediaSource(uri, dataSourceFactory, DefaultDashChunkSource.Factory(dataSourceFactory), mainHandler, mediaSourceLogger)
            C.TYPE_SS -> return SsMediaSource(uri, dataSourceFactory, DefaultSsChunkSource.Factory(dataSourceFactory), mainHandler, mediaSourceLogger)
            C.TYPE_HLS -> return HlsMediaSource(uri, dataSourceFactory, mainHandler, mediaSourceLogger)
            C.TYPE_OTHER -> return ExtractorMediaSource(uri, dataSourceFactory, DefaultExtractorsFactory(), mainHandler, mediaSourceLogger)
            else -> throw IllegalStateException("Unsupported type: " + type)
        }
    }

    fun dispatchTimeElapsedEvents() {
        Log.i(TAG, "Time elapsed event: buffer ${player?.bufferedPercentage}%")
        Log.i(TAG, "Time elapsed event: video percent played ${getPercentPlayed().toInt()}%")

    }

    private fun getPercentPlayed() = player?.currentPosition!!.toDouble() / player?.duration!!.toDouble() * 100

    fun updateState(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            ExoPlayer.STATE_IDLE -> {
                currentState = State.IDLE
                triggerEventWithLog(Event.DID_STOP)
                stopTimeElapsedCallBacks()
            }
            ExoPlayer.STATE_ENDED -> {
                currentState = State.IDLE
                triggerEventWithLog(Event.ENDED)
                stop()
            }
            ExoPlayer.STATE_BUFFERING -> {
                currentState = State.STALLED
                triggerEventWithLog(Event.STALLED)
            }
            ExoPlayer.STATE_READY -> {
                if (playWhenReady) {
                    currentState = State.PLAYING
                    triggerEventWithLog(Event.PLAYING)
                    startTimeElapsedCallBacks()
                } else {
                    currentState = State.PAUSED
                    triggerEventWithLog(Event.DID_PAUSE)
                }
            }
        }
    }

    private fun startTimeElapsedCallBacks() {
        timeElapsedEventsDispatcher?.start()
    }

    private fun stopTimeElapsedCallBacks() {
        timeElapsedEventsDispatcher?.stop()

    }

    private fun triggerEventWithLog(event: Event) {
        trigger(event.value)
        Log.i(TAG, event.value)
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        updateState(playWhenReady, playbackState)
    }

    override fun onPlayerError(error: ExoPlaybackException?) {
        triggerEventWithLog(Event.ERROR)
    }

    override fun onLoadingChanged(isLoading: Boolean) {
    }

    override fun onPositionDiscontinuity() {
    }

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {
    }

    class TimeElapsedManager(val callBack: () -> Unit) {

        var timer: Timer? = null

        fun start() {
            stop()
            timer = Timer()
            timer?.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    callBack()
                }
            }, 0, 200)
        }

        fun stop() {
            timer?.cancel()
        }
    }

    class MediaSourceLogger() : AdaptiveMediaSourceEventListener, ExtractorMediaSource.EventListener {
        override fun onLoadError(error: IOException?) {
            Log.i("EXOPlayer", "onLoadError")
        }

        override fun onLoadStarted(dataSpec: DataSpec?, dataType: Int, trackType: Int, trackFormat: Format?, trackSelectionReason: Int, trackSelectionData: Any?, mediaStartTimeMs: Long, mediaEndTimeMs: Long, elapsedRealtimeMs: Long) {
            Log.i("EXOPlayer", "onLoadStarted")
        }

        override fun onDownstreamFormatChanged(trackType: Int, trackFormat: Format?, trackSelectionReason: Int, trackSelectionData: Any?, mediaTimeMs: Long) {
            Log.i("EXOPlayer", "onDownstreamFormatChanged")
        }

        override fun onUpstreamDiscarded(trackType: Int, mediaStartTimeMs: Long, mediaEndTimeMs: Long) {
            Log.i("EXOPlayer", "onUpstreamDiscarded")
        }

        override fun onLoadCanceled(dataSpec: DataSpec?, dataType: Int, trackType: Int, trackFormat: Format?, trackSelectionReason: Int, trackSelectionData: Any?, mediaStartTimeMs: Long, mediaEndTimeMs: Long, elapsedRealtimeMs: Long, loadDurationMs: Long, bytesLoaded: Long) {
            Log.i("EXOPlayer", "onLoadCanceled")
        }

        override fun onLoadCompleted(dataSpec: DataSpec?, dataType: Int, trackType: Int, trackFormat: Format?, trackSelectionReason: Int, trackSelectionData: Any?, mediaStartTimeMs: Long, mediaEndTimeMs: Long, elapsedRealtimeMs: Long, loadDurationMs: Long, bytesLoaded: Long) {
            Log.i("EXOPlayer", "onLoadCompleted")
        }

        override fun onLoadError(dataSpec: DataSpec?, dataType: Int, trackType: Int, trackFormat: Format?, trackSelectionReason: Int, trackSelectionData: Any?, mediaStartTimeMs: Long, mediaEndTimeMs: Long, elapsedRealtimeMs: Long, loadDurationMs: Long, bytesLoaded: Long, error: IOException?, wasCanceled: Boolean) {
            Log.i("EXOPlayer", "onLoadError")
        }
    }
}