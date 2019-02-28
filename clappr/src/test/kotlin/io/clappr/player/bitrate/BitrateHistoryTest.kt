package io.clappr.player.bitrate

import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class BitrateHistoryTest {
    private lateinit var bitrateHistoryUnderTest: BitrateHistory

    @Before
    fun setup() {
        bitrateHistoryUnderTest = BitrateHistory()
    }

    @Test
    fun shouldReturnZeroAverageBitrateWhenEmpty() {
        assertEquals(0, bitrateHistoryUnderTest.averageBitrate())
    }

    @Test
    fun shouldReturnZeroAverageBitrateWhenEmptyWithTimestamp() {
        assertEquals(0, bitrateHistoryUnderTest.averageBitrate(1000))
    }

    @Test
    fun shouldNotAddNewBitrateIfEqualsTheLastAdded() {
        bitrateHistoryUnderTest.addBitrate(0, 2)
        bitrateHistoryUnderTest.addBitrate(10, 3)
        bitrateHistoryUnderTest.addBitrate(10, 4)

        val totalTimeSum = 1 + 2
        val averageBitrate = ((0 * 1) + (10 * 2)) / totalTimeSum

        assertEquals(averageBitrate.toLong(), bitrateHistoryUnderTest.averageBitrate(5))
    }


    @Test
    fun shouldAddNewBitrateIfDifferentOnlyFromTheLastAdded() {
        val expectedBitrateLogs = listOf(
                BitrateHistory.BitrateLog(0, 2, 0),
                BitrateHistory.BitrateLog(2, 4, 10),
                BitrateHistory.BitrateLog(4,4, 0),
                BitrateHistory.BitrateLog(4,4, 10),
                BitrateHistory.BitrateLog(4,4, 0),
                BitrateHistory.BitrateLog(4,4, 10)
        )

        bitrateHistoryUnderTest.addBitrate(0, 2)
        bitrateHistoryUnderTest.addBitrate(10, 3)
        bitrateHistoryUnderTest.addBitrate(0, 4)
        bitrateHistoryUnderTest.addBitrate(10, 4)
        bitrateHistoryUnderTest.addBitrate(0, 4)
        bitrateHistoryUnderTest.addBitrate(10, 4)
        

        val totalTimeSum = expectedBitrateLogs.asSequence()
                .map { it.totalActiveTimeInMillis }
                .reduce { currentSum, next -> currentSum + next }

        val averageBitrate = expectedBitrateLogs.asSequence()
                .map { it.bitrate * it.totalActiveTimeInMillis }
                .reduce{ current, next -> current + next } / totalTimeSum

        assertEquals(averageBitrate, bitrateHistoryUnderTest.averageBitrate(4))
    }

    @Test
    fun shouldSumBitrateWithTime() {
        bitrateHistoryUnderTest.addBitrate(90, 2)
        bitrateHistoryUnderTest.addBitrate(100, 17)
        bitrateHistoryUnderTest.addBitrate(110, 31)

        val bitrateList = listOf(
                BitrateHistory.BitrateLog(2,17, 90),
                BitrateHistory.BitrateLog(17,31, 100),
                BitrateHistory.BitrateLog(31,4, 110)
        )

        bitrateList.last().endTime = 49

        val sumOfAllBitrateWithTime = bitrateList.asSequence()
                .map { it.bitrate * it.totalActiveTimeInMillis }
                .reduce { current, next -> current + next
        }

        val averageBitrate = sumOfAllBitrateWithTime / 47

        assertEquals(averageBitrate, bitrateHistoryUnderTest.averageBitrate(49))
    }

    @Test
    fun shouldSumTotalTime() {
        bitrateHistoryUnderTest.addBitrate(90, 2)
        bitrateHistoryUnderTest.addBitrate(100, 17)
        bitrateHistoryUnderTest.addBitrate(110, 31)

        val bitrateList = listOf(
                BitrateHistory.BitrateLog(2, 17, 90),
                BitrateHistory.BitrateLog(17, 31, 90),
                BitrateHistory.BitrateLog(31, 0, 110)
        )

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

        val bitrateList = listOf(
                BitrateHistory.BitrateLog(2, 17, 90),
                BitrateHistory.BitrateLog(17, 31, 100),
                BitrateHistory.BitrateLog(17, 0, 110)
        )



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