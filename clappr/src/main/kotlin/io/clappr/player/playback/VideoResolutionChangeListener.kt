package io.clappr.player.playback

import android.os.Bundle
import com.google.android.exoplayer2.analytics.AnalyticsListener
import io.clappr.player.base.Event
import io.clappr.player.base.EventData
import io.clappr.player.components.Playback

class VideoResolutionChangeListener(private val playback: Playback) : AnalyticsListener {

    override fun onVideoSizeChanged(eventTime: AnalyticsListener.EventTime?, width: Int, height: Int, unappliedRotationDegrees: Int, pixelWidthHeightRatio: Float) {
        playback.trigger(Event.DID_UPDATE_VIDEO_RESOLUTION.value, Bundle().apply {
            putInt(EventData.WIDTH.value, width)
            putInt(EventData.HEIGHT.value, height)
        })
    }
}