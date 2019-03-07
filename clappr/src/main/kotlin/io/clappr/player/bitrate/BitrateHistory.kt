package io.clappr.player.bitrate

import io.clappr.player.log.Logger


class BitrateHistory {

    private val bitrateLogList: MutableList<BitrateLog> = mutableListOf()

    fun averageBitrate(currentTimestamp: Long = System.nanoTime()): Long {
        return bitrateLogList.takeIf { it.isNotEmpty() }?.let {
            try {
                setTimesForLastBitrate(currentTimestamp)
                sumOfAllBitrateWithTime() / totalBitrateHistoryTime()
            } catch (e: BitrateLog.WrongTimeIntervalException) {
                Logger.error("BitrateHistory", "Error: ${e.message} - BitrateLog: ${e.bitrateLog}")
                0L
            }
        } ?: 0L
    }

    fun addBitrate(bitrate: Long?, currentTimestamp: Long = System.nanoTime()) {
        if (bitrateLogList.isNotEmpty() && bitrateLogList.last().startTime > currentTimestamp) {
            Logger.error("BitrateHistory", "Bitrate list time stamp should be crescent." +
                    " Can not add time stamp with value bellow ${bitrateLogList.last().startTime}")
            throw BitrateLog.WrongTimeIntervalException("Bitrate list time stamp should be crescent.", null)
        }

        bitrate?.takeIf { bitrateLogList.isEmpty() || bitrate != bitrateLogList.last().bitrate }?.apply {
                BitrateLog(startTime = currentTimestamp, bitrate = bitrate).apply {
                    setTimesForLastBitrate(currentTimestamp)
                    bitrateLogList.add(this)
                }
        }
    }

    fun clear() {
        bitrateLogList.clear()
    }

    private fun sumOfAllBitrateWithTime() =
            bitrateLogList.asSequence().map { it.bitrate * it.totalActiveTimeInMillis }
                    .reduce { currentSum, next -> currentSum + next }

    private fun totalBitrateHistoryTime() = (bitrateLogList.last().endTime - bitrateLogList.first().startTime).takeIf { it != 0L }
            ?: 1

    private fun setTimesForLastBitrate(currentTimestamp: Long) {
        if (bitrateLogList.size > 0) {
            val lastBitrate = bitrateLogList.last()
            lastBitrate.endTime = currentTimestamp
        }
    }

    data class BitrateLog(val startTime: Long, var endTime: Long = 0, val bitrate: Long = 0) {

        val totalActiveTimeInMillis: Long
            get() {
                if (startTime > endTime)
                    throw WrongTimeIntervalException("startTime should be less than endTime", this)

                return endTime - startTime
            }

        class WrongTimeIntervalException(message: String, val bitrateLog: BitrateLog? = null) : RuntimeException(message)
    }
}