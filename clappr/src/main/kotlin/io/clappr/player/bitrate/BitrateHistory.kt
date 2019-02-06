package io.clappr.player.bitrate

class BitrateHistory {

    internal val bitrateLogList: MutableList<BitrateLog> = mutableListOf()

    fun averageBitrate(currentTimestamp: Long = System.currentTimeMillis()): Long {
        setTimesForLastBitrate(currentTimestamp)
        return sumOfAllBitrateWithTime() / totalBitrateHistoryTime()
    }

    fun addBitrate(bitrate: Int, currentTimestamp: Long = System.currentTimeMillis()) {
        bitrate.takeIf { bitrateLogList.isEmpty() || bitrate != bitrateLogList.last().bitrate }?.apply {
            setTimesForLastBitrate(currentTimestamp)
            bitrateLogList.add(BitrateLog(startTime = currentTimestamp, bitrate = bitrate))
        }
    }

    private fun sumOfAllBitrateWithTime() =
            bitrateLogList.asSequence().map { it.bitrate.toLong() * it.totalActiveTimeInMillis }
                    .reduce { currentSum, next -> currentSum + next }

    private fun totalBitrateHistoryTime() =
            bitrateLogList.asSequence().map { it.totalActiveTimeInMillis }.reduce { currentSum, next -> currentSum + next }

    private fun setTimesForLastBitrate(currentTimestamp: Long) {
        if (bitrateLogList.size > 0) {
            val lastBitrate = bitrateLogList.last()
            lastBitrate.endTime = currentTimestamp
        }
    }

    internal data class BitrateLog(val startTime: Long, var endTime: Long = 0, val bitrate: Int = 0) {

        val totalActiveTimeInMillis: Long
            get() = endTime - startTime
    }
}