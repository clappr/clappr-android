package io.clappr.player.playback

import android.os.Bundle
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.analytics.AnalyticsListener.EventTime
import com.google.android.exoplayer2.source.MediaSourceEventListener.LoadEventInfo
import com.google.android.exoplayer2.source.MediaSourceEventListener.MediaLoadData
import io.clappr.player.base.Event.DID_UPDATE_BITRATE
import io.clappr.player.base.EventData.BITRATE
import io.clappr.player.bitrate.BitrateHistory
import io.clappr.player.components.Playback
import io.clappr.player.log.Logger
import io.clappr.player.utils.withPayload

class ExoPlayerBitrateLogger(
    private val playback: Playback,
    private val bitrateHistory: BitrateHistory = BitrateHistory { System.nanoTime() }
) : AnalyticsListener {

    private var audio = 0L
    private var video = 0L

    var lastBitrate: Long = 0
        set(value) {

            val oldValue = field

            field = value

            try {
                bitrateHistory.addBitrate(field)
            } catch (e: BitrateHistory.BitrateLog.WrongTimeIntervalException) {
                Logger.error(ExoPlayerBitrateLogger::class.java.simpleName, e.message
                    ?: "Can not add bitrate on history")
            }

            if (oldValue != field) {
                val userData = Bundle().withPayload(BITRATE.value to field)
                playback.trigger(DID_UPDATE_BITRATE.value, userData)
            }
        }

    override fun onLoadCompleted(eventTime: EventTime?, loadEventInfo: LoadEventInfo?, mediaLoadData: MediaLoadData) {
        when (mediaLoadData.trackType) {
            C.TRACK_TYPE_DEFAULT, C.TRACK_TYPE_VIDEO -> {
                mediaLoadData.trackFormat?.bitrate?.let {
                    video = it.toLong()
                }
            }
            C.TRACK_TYPE_AUDIO -> {
                mediaLoadData.trackFormat?.bitrate
                    ?.takeIf { it > 0 }
                    ?.let { audio = it.toLong() }
            }
        }
        lastBitrate = if ((video + audio) > 0) video + audio else 0
    }

    fun averageBitrate() = bitrateHistory.averageBitrate()
    fun clearHistory() = bitrateHistory.clear()
}