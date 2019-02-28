package io.clappr.player.bitrate

import org.junit.Test
import kotlin.test.assertEquals


class BitrateLogTest {

    @Test
    fun shouldSetBitrateTimeAsDifferenceBetweenFirstAndLastTimes() {
        val bitRateLog = BitrateHistory.BitrateLog(1, 2, 2)
        assertEquals(1, bitRateLog.totalActiveTimeInMillis)
    }

    @Test
    fun shouldNotReturnBitrateTimeNegative(){
        val bitRateLog = BitrateHistory.BitrateLog(2, 1, 2)
        assertEquals(1, bitRateLog.totalActiveTimeInMillis)
    }
}