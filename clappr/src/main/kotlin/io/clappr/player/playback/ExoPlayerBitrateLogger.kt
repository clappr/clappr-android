package io.clappr.player.playback

import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.analytics.AnalyticsListener.EventTime
import com.google.android.exoplayer2.source.MediaSourceEventListener.LoadEventInfo
import com.google.android.exoplayer2.source.MediaSourceEventListener.MediaLoadData
import io.clappr.player.bitrate.BitrateHistory
import io.clappr.player.log.Logger

class ExoPlayerBitrateLogger(
    private val bitrateHistory: BitrateHistory = BitrateHistory { System.nanoTime() },
    private val didUpdateBitrate: ((Long) -> Unit)
) : AnalyticsListener {

    private var audio = 0L
    private var video = 0L

    var lastBitrate: Long = 0
        private set(value) {

            val oldValue = field

            field = value

            try {
                bitrateHistory.addBitrate(field)
            } catch (e: BitrateHistory.BitrateLog.WrongTimeIntervalException) {
                Logger.error(ExoPlayerBitrateLogger::class.java.simpleName, e.message
                    ?: "Can not add bitrate on history")
            }

            if (oldValue != field) {
                didUpdateBitrate(field)
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