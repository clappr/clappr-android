package com.globo.clappr.plugin.Playback

import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
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

open class ExoPlayerPlugin(options: Options) : Playback(options) {
    companion object {
        const val name = "exoplayerplugin"

        var containerView: ViewGroup? = null
    }

    val mainHandler = Handler()
    val bandwidthMeter = DefaultBandwidthMeter()
    var playerView = SimpleExoPlayerView(context)
    val hlsLogger = HlsLogger()
    val urlString = "http://qthttp.apple.com.edgesuite.net/1010qwoeiuryfg/sl.m3u8"
    var player: SimpleExoPlayer? = null
    val listener = Listener()
    var currentState = Playback.State.NONE

    override val duration: Double
        get() = player?.duration as Double

    override val state: State
        get() = currentState

    override val canPlay: Boolean
        get() = super.canPlay

    override val canPause: Boolean
        get() = super.canPause

    override val canSeek: Boolean
        get() = super.canSeek

    override fun play() {
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
        player?.addListener(listener)
        setupPlayerView()
    }

    fun setupPlayerView() {
        playerView.setBackgroundColor(Color.parseColor("#00ff00"))
        containerView?.addView(playerView)
        playerView.player = player
    }

    fun mediaSource(uri: Uri): MediaSource {
        val dataSourceFactory = DefaultDataSourceFactory(context, "agent", bandwidthMeter)
        return HlsMediaSource(uri, dataSourceFactory, mainHandler, hlsLogger)
    }

    class Listener() : ExoPlayer.EventListener {
        override fun onPlayerError(error: ExoPlaybackException?) {
            Log.i("EXOPlayer", "onPlayerError")
        }

        override fun onLoadingChanged(isLoading: Boolean) {
            Log.i("EXOPlayer", "onLoadingChanged")
        }

        override fun onPositionDiscontinuity() {
            Log.i("EXOPlayer", "onPositionDiscontinuity")
        }

        override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {
            Log.i("EXOPlayer", "onTimelineChanged")
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            Log.i("EXOPlayer", "onPlayerStateChanged")
        }

    }

    class HlsLogger() : AdaptiveMediaSourceEventListener {
        override fun onLoadStarted(dataSpec: DataSpec?, dataType: Int, trackType: Int, trackFormat: Format?, trackSelectionReason: Int, trackSelectionData: Any?, mediaStartTimeMs: Long, mediaEndTimeMs: Long, elapsedRealtimeMs: Long) {
//            Log.i("EXOPlayer", "onLoadStarted")
        }

        override fun onDownstreamFormatChanged(trackType: Int, trackFormat: Format?, trackSelectionReason: Int, trackSelectionData: Any?, mediaTimeMs: Long) {
            Log.i("EXOPlayer", "onDownstreamFormatChanged")
        }

        override fun onUpstreamDiscarded(trackType: Int, mediaStartTimeMs: Long, mediaEndTimeMs: Long) {
//            Log.i("EXOPlayer", "onUpstreamDiscarded")
        }

        override fun onLoadCanceled(dataSpec: DataSpec?, dataType: Int, trackType: Int, trackFormat: Format?, trackSelectionReason: Int, trackSelectionData: Any?, mediaStartTimeMs: Long, mediaEndTimeMs: Long, elapsedRealtimeMs: Long, loadDurationMs: Long, bytesLoaded: Long) {
//            Log.i("EXOPlayer", "onLoadCanceled")
        }

        override fun onLoadCompleted(dataSpec: DataSpec?, dataType: Int, trackType: Int, trackFormat: Format?, trackSelectionReason: Int, trackSelectionData: Any?, mediaStartTimeMs: Long, mediaEndTimeMs: Long, elapsedRealtimeMs: Long, loadDurationMs: Long, bytesLoaded: Long) {
//            Log.i("EXOPlayer", "onLoadCompleted")
        }

        override fun onLoadError(dataSpec: DataSpec?, dataType: Int, trackType: Int, trackFormat: Format?, trackSelectionReason: Int, trackSelectionData: Any?, mediaStartTimeMs: Long, mediaEndTimeMs: Long, elapsedRealtimeMs: Long, loadDurationMs: Long, bytesLoaded: Long, error: IOException?, wasCanceled: Boolean) {
//            Log.i("EXOPlayer", "onLoadError")
        }
    }
}