package com.globo.clappr.plugin.Playback

import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.view.ViewGroup
import com.globo.clappr.components.Playback
import com.globo.clappr.plugin.UIPlugin
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory

open class ExoPlayerPlugin(val playback: Playback) : UIPlugin(playback) {
    companion object {
        const val name = "exoplayerplugin"

        var containerView: ViewGroup? = null
    }

    var playerView = SimpleExoPlayerView(context)

    init {
        playerView.setBackgroundColor(Color.parseColor("#00ff00"))
        containerView?.addView(playerView)

        val mainHandler = Handler()
        val bandwidthMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory = AdaptiveVideoTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(mainHandler, videoTrackSelectionFactory)

        val player = ExoPlayerFactory.newSimpleInstance(context, trackSelector, DefaultLoadControl())
        playerView.player = player
        player.playWhenReady = true

        val dataSourceFactory = DefaultDataSourceFactory(context, "agent", bandwidthMeter)
        val extractorsFactory = DefaultExtractorsFactory()
        val mp4VideoUri = Uri.parse("http://clappr.io/highline.mp4")

        val videoSource = ExtractorMediaSource(mp4VideoUri, dataSourceFactory, extractorsFactory, null, null)
        player.prepare(videoSource)
    }
}