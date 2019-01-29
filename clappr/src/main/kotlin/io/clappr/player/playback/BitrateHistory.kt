package io.clappr.player.playback

class BitrateHistory {
    
    internal val bitrateLogList: MutableList<BitrateLog> = mutableListOf()

    val avgBitrate: Long
        get() {
            val totalTime = bitrateLogList.map { log -> log.time }.reduce { currentSum, next -> currentSum + next }
            return (bitrateLogList.map { log -> log.bitrate.toLong() * log.time }
                    .reduce { currentSum, next -> currentSum + next }) / totalTime
        }

    fun addBitrateLog(bitrate: Int?, bitrateTimestamp: Long = System.currentTimeMillis()) {
        bitrate?.let {
            setTimesForLastBitrate(bitrateTimestamp)
            bitrateLogList.add(BitrateLog(start = bitrateTimestamp, bitrate = bitrate))
        }
    }

    internal fun setTimesForLastBitrate(currentTime: Long) {
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