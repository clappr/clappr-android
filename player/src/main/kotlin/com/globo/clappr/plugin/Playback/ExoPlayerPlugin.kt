package com.globo.clappr.plugin.Playback

import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.ViewGroup
import com.globo.clappr.base.ClapprEvent
import com.globo.clappr.base.Options
import com.globo.clappr.components.Playback
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import java.io.IOException

open class ExoPlayerPlugin(options: Options) : Playback(options), ExoPlayer.EventListener {
    companion object {
        const val name = "exoplayerplugin"

        var containerView: ViewGroup? = null
    }

    val mainHandler = Handler()
    val bandwidthMeter = DefaultBandwidthMeter()
    var playerView = SimpleExoPlayerView(context)
    val mediaSourceLogger = MediaSourceLogger()
    val urlString = "http://qthttp.apple.com.edgesuite.net/1010qwoeiuryfg/sl.m3u8"
    var player: SimpleExoPlayer? = null
    var currentState = Playback.State.NONE

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
    }

    override fun seek(seconds: Int) {
        player?.seekTo((seconds * 1000).toLong())
    }

    override fun load(source: String, mimeType: String?) {
        player?.prepare(mediaSource(Uri.parse(source)))
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
        val dataSourceFactory = DefaultDataSourceFactory(context, "agent", bandwidthMeter)
        return HlsMediaSource(uri, dataSourceFactory, mainHandler, mediaSourceLogger)
    }

    fun updateState(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            ExoPlayer.STATE_IDLE -> {
                currentState = State.IDLE
            }
            ExoPlayer.STATE_ENDED -> {
                currentState = State.IDLE
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

    override fun onLoadingChanged(isLoading: Boolean) {}
    override fun onPositionDiscontinuity() {}
    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {}

    class MediaSourceLogger() : AdaptiveMediaSourceEventListener {
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