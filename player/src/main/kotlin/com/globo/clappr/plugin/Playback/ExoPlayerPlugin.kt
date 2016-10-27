package com.globo.clappr.plugin.Playback

import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.ViewGroup
import com.globo.clappr.components.Playback
import com.globo.clappr.plugin.UIPlugin
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import java.io.IOException

open class ExoPlayerPlugin(val playback: Playback) : UIPlugin(playback) {
    companion object {
        const val name = "exoplayerplugin"

        var containerView: ViewGroup? = null
    }

    val mainHandler = Handler()
    val bandwidthMeter = DefaultBandwidthMeter()
    var playerView = SimpleExoPlayerView(context)
    val hlsLogger = HlsLogger()
    val urlString = "http://be.live.video.globoi.com/dvr/globo-sp1/playlist.m3u8"
    var player: SimpleExoPlayer? = null

    init {
        setupPlayer()
        load(Uri.parse(urlString))
    }

    fun setupPlayer() {
        val videoTrackSelectionFactory = AdaptiveVideoTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(mainHandler, videoTrackSelectionFactory)
        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector, DefaultLoadControl())
        player?.playWhenReady = true
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

    fun load(uri: Uri) {
        player?.prepare(mediaSource(uri))
    }

    class HlsLogger(): AdaptiveMediaSourceEventListener {
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