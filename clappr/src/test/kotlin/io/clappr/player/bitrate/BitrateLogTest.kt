package io.clappr.player.bitrate

import org.junit.Test
import kotlin.test.assertEquals


class BitrateLogTest {

    @Test
    fun shouldSetBitrateTimeAsDifferenceBetweenFirstAndLastTimes() {
        val bitRateLog = BitrateHistory.BitrateLog(1, 2, 2)
        assertEquals(1, bitRateLog.totalActiveTimeInMillis)
    }

    @Test(expected = BitrateHistory.BitrateLog.WrongTimeIntervalException::class)
    fun shouldThrowWrongTimeIntervalExceptionWhenEndTimeIsLessThanStartTime(){
       BitrateHistory.BitrateLog(2, 1, 2).totalActiveTimeInMillis
    }
}