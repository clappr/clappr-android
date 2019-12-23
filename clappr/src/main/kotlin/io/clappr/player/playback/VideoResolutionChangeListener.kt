package io.clappr.player.playback

import android.os.Bundle
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.util.EventLogger
import io.clappr.player.base.Event
import io.clappr.player.base.EventData
import io.clappr.player.components.Playback
import io.clappr.player.utils.withPayload

class VideoResolutionChangeListener(private val playback: Playback) : EventLogger(null) {

    override fun onVideoSizeChanged(eventTime: AnalyticsListener.EventTime?, width: Int, height: Int, unappliedRotationDegrees: Int, pixelWidthHeightRatio: Float) {
        val userData = Bundle().withPayload(
            EventData.WIDTH.value to width,
            EventData.HEIGHT.value to height
        )
        playback.trigger(Event.DID_UPDATE_VIDEO_RESOLUTION.value, userData)
    }
}
