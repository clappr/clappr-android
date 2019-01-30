package io.clappr.player.playback

class BitrateHistory {

    internal val bitrateLogList: MutableList<BitrateLog> = mutableListOf()

    fun averageBitrate(currentTimestamp: Long = System.currentTimeMillis()): Long {
        bitrateLogList.last().time = currentTimestamp
        return sumOfAllBitrateWithTime() / totalBitrateHistoryTime()
    }

    fun addBitrateLog(bitrate: Int?, currentTimestamp: Long = System.currentTimeMillis()) {
        bitrate?.let {
            setTimesForLastBitrate(currentTimestamp)
            bitrateLogList.add(BitrateLog(start = currentTimestamp, bitrate = bitrate))
        }
    }

    internal fun sumOfAllBitrateWithTime() =
            (bitrateLogList.map { log -> log.bitrate.toLong() * log.time }
                    .reduce { currentSum, next -> currentSum + next })

    internal fun totalBitrateHistoryTime() =
            bitrateLogList.map { log -> log.time }.reduce { currentSum, next -> currentSum + next }

    private fun setTimesForLastBitrate(currentTime: Long) {
        if (bitrateLogList.size > 0) {
            val lastBitrate = bitrateLogList.last()
            lastBitrate.end = currentTime
            lastBitrate.time = currentTime - lastBitrate.start
        }
    }

    internal data class BitrateLog(
            val start: Long,
            var end: Long = 0,
            var time: Long = 0,
            val bitrate: Int = 0)
}