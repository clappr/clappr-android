package io.clappr.player.bitrate

import kotlin.RuntimeException

class BitrateHistory {

    private val bitrateLogList: MutableList<BitrateLog> = mutableListOf()

    fun averageBitrate(currentTimestamp: Long = System.nanoTime()): Long {
        return bitrateLogList.takeIf { it.isNotEmpty() }?.let {
            setTimesForLastBitrate(currentTimestamp)
            sumOfAllBitrateWithTime() / totalBitrateHistoryTime()
        } ?: 0
    }

    fun addBitrate(bitrate: Long?, currentTimestamp: Long = System.nanoTime()) {
        bitrate?.takeIf { bitrateLogList.isEmpty() || bitrate != bitrateLogList.last().bitrate }?.apply {
            setTimesForLastBitrate(currentTimestamp)
            bitrateLogList.add(BitrateLog(startTime = currentTimestamp, bitrate = bitrate))
        }
    }

    fun clear() {
        bitrateLogList.clear()
    }

    private fun sumOfAllBitrateWithTime() =
            bitrateLogList.asSequence().map { it.bitrate * it.totalActiveTimeInMillis }
                    .reduce { currentSum, next -> currentSum + next }

    private fun totalBitrateHistoryTime() = bitrateLogList.last().endTime - bitrateLogList.first().startTime

    private fun setTimesForLastBitrate(currentTimestamp: Long) {
        if (bitrateLogList.size > 0) {
            val lastBitrate = bitrateLogList.last()
            lastBitrate.endTime = currentTimestamp
        }
    }

    data class BitrateLog(val startTime: Long, var endTime: Long = 0, val bitrate: Long = 0) {

        init {
            if(startTime > endTime)
                throw WrongTimeIntervalException("startTime should be less than endTime")
        }

        val totalActiveTimeInMillis: Long
            get() = endTime - startTime

        class WrongTimeIntervalException(message: String): RuntimeException(message)
    }
}