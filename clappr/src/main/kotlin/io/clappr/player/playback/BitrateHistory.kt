package io.clappr.player.playback

class BitrateHistory {
    
    internal val bitrateLogList: MutableList<BitrateLog> = mutableListOf()

    val avgBitrate: Long
        get() {
            val totalTime = bitrateLogList.map { log -> log.time }.reduce { currentSum, next -> currentSum + next }
            return (bitrateLogList.map { log -> log.bitrate.toLong() * log.time }
                    .reduce { currentSum, next -> currentSum + next }) / totalTime
        }

    fun addBitrateLog(bitrate: Int?) {
        bitrate?.let {
            val currentTime = System.currentTimeMillis()
            val startTime = if (bitrateLogList.size > 0) bitrateLogList[0].time
            else currentTime
            bitrateLogList.add(BitrateLog(time = currentTime - startTime, bitrate = bitrate))
        }
    }

    internal data class BitrateLog(val time: Long, val bitrate: Int = 0)
}