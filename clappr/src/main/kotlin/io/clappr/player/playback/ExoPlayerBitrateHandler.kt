package io.clappr.player.playback

import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.analytics.AnalyticsListener.EventTime
import com.google.android.exoplayer2.source.MediaSourceEventListener.LoadEventInfo
import com.google.android.exoplayer2.source.MediaSourceEventListener.MediaLoadData
import com.google.android.exoplayer2.util.EventLogger
import io.clappr.player.bitrate.BitrateHistory
import io.clappr.player.log.Logger
import kotlin.math.max

class ExoPlayerBitrateHandler(
    private val bitrateHistory: BitrateHistory = BitrateHistory { System.nanoTime() },
    private val didUpdateBitrate: (bitrate: Long) -> Unit
) {

    private var audio = 0L
    private var video = 0L

    var currentBitrate = 0L
        private set(value) {

            val oldValue = field

            field = value

            try {
                bitrateHistory.addBitrate(field)
            } catch (e: BitrateHistory.BitrateLog.WrongTimeIntervalException) {
                Logger.error(ExoPlayerBitrateHandler::class.java.simpleName, e.message
                    ?: "Can not add bitrate on history")
            }

            if (oldValue != field) {
                didUpdateBitrate(field)
            }
        }

    val averageBitrate get() = bitrateHistory.averageBitrate()

    fun reset() = bitrateHistory.clear()

    val analyticsListener = object: EventLogger(null) {
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
            currentBitrate = max(video + audio, 0L)
        }
    }
}