package com.globo.clappr.plugin.Playback

import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.ViewGroup
import com.globo.clappr.base.ClapprEvent
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

open class ExoPlayerPlugin(options: Options) : Playback(options), ExoPlayer.EventListener {
    companion object : PlaybackSupportInterface {
        override fun supportsSource(source: String, mimeType: String?): Boolean {
            val uri = Uri.parse(source)
            val type = Util.inferContentType(uri.lastPathSegment)
            return type == C.TYPE_SS || type == C.TYPE_HLS || type == C.TYPE_DASH || type == C.TYPE_OTHER
        }

        const val name = "exoplayerplugin"

        var containerView: ViewGroup? = null
    }

    val mainHandler = Handler()
    val bandwidthMeter = DefaultBandwidthMeter()
    var playerView = SimpleExoPlayerView(context)
    val mediaSourceLogger = MediaSourceLogger()
    val urlString = "http://playready.directtaps.net/smoothstreaming/SSWSS720H264/SuperSpeedway_720.ism"
    var player: SimpleExoPlayer? = null
    var currentState = Playback.State.NONE
    val timeElapsedHandler = Handler()
    val timeElapsedRunnable = TimeElapsedRunnable(timeElapsedHandler, { dispatchTimeElapsedEvents() })

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
        get() = duration != 0.0

    override fun play() {
        trigger(ClapprEvent.WILL_PLAY.value)
        player?.playWhenReady = true
    }

    override fun pause() {
        player?.playWhenReady = false
    }

    override fun stop() {
        player?.stop()
        timeElapsedHandler.removeCallbacks(timeElapsedRunnable)
    }

    override fun seek(seconds: Int) {
        player?.seekTo((seconds * 1000).toLong())
    }

    override fun load(source: String, mimeType: String?) {
        player?.prepare(mediaSource(Uri.parse(source)))
        timeElapsedHandler.post(timeElapsedRunnable)
    }

    init {
        setupPlayer()
        load(urlString, null)
    }

    fun setupPlayer() {
        val videoTrackSelectionFactory = AdaptiveVideoTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(mainHandler, videoTrackSelectionFactory)
        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector, DefaultLoadControl())
        player?.playWhenReady = true
        player?.addListener(this)
        setupPlayerView()
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
        //dispatch buffer and time elapsed events
        Log.i("Exoplayer", "Time elapsed event")
    }

    fun updateState(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            ExoPlayer.STATE_IDLE -> {
                currentState = State.IDLE
            }
            ExoPlayer.STATE_ENDED -> {
                currentState = State.IDLE
                stop()
            }
            ExoPlayer.STATE_BUFFERING -> {
                currentState = State.STALLED
            }
            ExoPlayer.STATE_READY -> {
                currentState = if (playWhenReady) State.PLAYING else State.PAUSED
            }
        }
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        updateState(playWhenReady, playbackState)
    }

    override fun onPlayerError(error: ExoPlaybackException?) {
        //log error, dispatch event
    }

    override fun onLoadingChanged(isLoading: Boolean) {
    }

    override fun onPositionDiscontinuity() {
    }

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {
    }

    class TimeElapsedRunnable(val handler: Handler, val function: () -> Unit): Runnable {
        override fun run() {
            function()
            handler.postDelayed(this, 200)
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