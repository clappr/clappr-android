package io.clappr.player.bitrate

import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BitrateHistoryTest {
    private lateinit var bitrateHistoryUnderTest: BitrateHistory

    @Before
    fun setup() {
        bitrateHistoryUnderTest = BitrateHistory()
    }

    @Test
    fun shouldListBitrate() {
        bitrateHistoryUnderTest.addBitrate(0)
        bitrateHistoryUnderTest.addBitrate(1)
        bitrateHistoryUnderTest.addBitrate(2)
        bitrateHistoryUnderTest.addBitrate(3)

        assertEquals(4, bitrateHistoryUnderTest.bitrateLogList.size, "Bitrate log list size incorrect")
    }

    @Test
    fun shouldSetBitrateValueOnBitrateLog() {
        bitrateHistoryUnderTest.addBitrate(123456)
        val bitrateLog =  bitrateHistoryUnderTest.bitrateLogList.first()

        assertEquals(123456, bitrateLog.bitrate)
    }

    @Test
    fun shouldSetBitrateTimeStampAsStartTime() {
        bitrateHistoryUnderTest.addBitrate(0, 2)
        assertEquals(2, bitrateHistoryUnderTest.bitrateLogList[0].startTime)
    }

    @Test
    fun shouldSetBitrateEndTime() {
        bitrateHistoryUnderTest.addBitrate(0, 2)
        bitrateHistoryUnderTest.addBitrate(1, 3)
        assertEquals(3, bitrateHistoryUnderTest.bitrateLogList[0].endTime)
    }

    @Test
    fun shouldNotSetBitrateEndTime() {
        bitrateHistoryUnderTest.addBitrate(0, 2)
        assertEquals(0, bitrateHistoryUnderTest.bitrateLogList[0].endTime)
    }

    @Test
    fun shouldSetBitrateTimeAsDifferenceBetweenFirstAndLastTimes() {
        bitrateHistoryUnderTest.addBitrate(0, 2)
        bitrateHistoryUnderTest.addBitrate(1, 3)
        assertEquals(1, bitrateHistoryUnderTest.bitrateLogList[0].totalActiveTimeInMillis)
    }

    @Test
    fun shouldNotSetBitrateTime() {
        bitrateHistoryUnderTest.addBitrate(0, 2)
        assertTrue { bitrateHistoryUnderTest.bitrateLogList[0].totalActiveTimeInMillis <= 0 }
    }

    @Test
    fun shouldEqualFirstBitrateEndTimeWithSecondBitrateStartTime() {
        bitrateHistoryUnderTest.addBitrate(0, 2)
        bitrateHistoryUnderTest.addBitrate(1, 3)
        assertEquals(bitrateHistoryUnderTest.bitrateLogList[0].endTime, bitrateHistoryUnderTest.bitrateLogList[1].startTime)
    }


    @Test
    fun shouldNotAddNewBitrateIfEqualsTheLastAdded() {
        bitrateHistoryUnderTest.addBitrate(0, 2)
        bitrateHistoryUnderTest.addBitrate(1, 3)
        bitrateHistoryUnderTest.addBitrate(1, 4)

        assertEquals(2, bitrateHistoryUnderTest.bitrateLogList.size)
    }

    @Test
    fun shouldAddNewBitrateIfDifferentOnlyFromTheLastAdded() {
        bitrateHistoryUnderTest.addBitrate(0, 2)
        bitrateHistoryUnderTest.addBitrate(1, 3)
        bitrateHistoryUnderTest.addBitrate(0, 4)
        bitrateHistoryUnderTest.addBitrate(1, 4)
        bitrateHistoryUnderTest.addBitrate(0, 4)
        bitrateHistoryUnderTest.addBitrate(1, 4)

        assertEquals(6, bitrateHistoryUnderTest.bitrateLogList.size)
    }

    @Test
    fun shouldSumBitrateWithTime() {
        bitrateHistoryUnderTest.addBitrate(90, 2)
        bitrateHistoryUnderTest.addBitrate(100, 17)
        bitrateHistoryUnderTest.addBitrate(110, 31)

        val bitrateList = bitrateHistoryUnderTest.bitrateLogList

        val firstBitrateLog = bitrateList.first()
        val secondBitrateLog = bitrateList[1]
        val thirdBitrateLog = bitrateList[2]

        bitrateList.last().endTime = 49

        val sumOfAllBitrateWithTime = ((firstBitrateLog.bitrate * firstBitrateLog.totalActiveTimeInMillis)
                + (secondBitrateLog.bitrate * secondBitrateLog.totalActiveTimeInMillis)
                + (thirdBitrateLog.bitrate * thirdBitrateLog.totalActiveTimeInMillis))

        val averageBitrate = sumOfAllBitrateWithTime / 47

        assertEquals(averageBitrate, bitrateHistoryUnderTest.averageBitrate(49))
    }

    @Test
    fun shouldSumTotalTime() {
        bitrateHistoryUnderTest.addBitrate(90, 2)
        bitrateHistoryUnderTest.addBitrate(100, 17)
        bitrateHistoryUnderTest.addBitrate(110, 31)

        val bitrateList = bitrateHistoryUnderTest.bitrateLogList

        val firstBitrateLog = bitrateList.first()
        val secondBitrateLog = bitrateList[1]
        val thirdBitrateLog = bitrateList[2]

        bitrateList.last().endTime = 49

        val totalTimeSum = (firstBitrateLog.totalActiveTimeInMillis
                + secondBitrateLog.totalActiveTimeInMillis
                + thirdBitrateLog.totalActiveTimeInMillis)

        val averageBitrate = ((90 * 15) + (100 * 14) + (110 * 18)) / totalTimeSum

        assertEquals(averageBitrate, bitrateHistoryUnderTest.averageBitrate(49))
    }

    @Test
    fun shouldEqualsTotalTimeSumWithBitrateDelta() {
        bitrateHistoryUnderTest.addBitrate(90, 2)
        bitrateHistoryUnderTest.addBitrate(100, 17)
        bitrateHistoryUnderTest.addBitrate(110, 31)

        val bitrateList = bitrateHistoryUnderTest.bitrateLogList

        bitrateList.last().endTime = 49

        val bitrateDelta = bitrateList.last().endTime - bitrateList.first().startTime
        val averageBitrate = ((90 * 15) + (100 * 14) + (110 * 18)) / bitrateDelta

        assertEquals(averageBitrate, bitrateHistoryUnderTest.averageBitrate(49))
    }

    @Test
    fun shouldSetLastBitrateTimeAndDivideSums() {

        bitrateHistoryUnderTest.addBitrate(90, 2)
        bitrateHistoryUnderTest.addBitrate(100, 17)
        bitrateHistoryUnderTest.addBitrate(110, 31)

        val averageBitrate = (((90 * 15) + (100 * 14) + (110 * 18)) / 47).toLong()

        assertEquals(averageBitrate, bitrateHistoryUnderTest.averageBitrate(49))
    }
}