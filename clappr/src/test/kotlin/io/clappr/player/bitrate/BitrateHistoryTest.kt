package io.clappr.player.bitrate

import io.clappr.player.BuildConfig
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals


@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [23])
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
    fun shouldNotAddBitrateWhenNull() {
        bitrateHistoryUnderTest.addBitrate(null)

        assertEquals(0, bitrateHistoryUnderTest.bitrateLogList.size)
    }

    @Test
    fun shouldIgnoreBitrateWhenNullFromList() {
        bitrateHistoryUnderTest.addBitrate(0)
        bitrateHistoryUnderTest.addBitrate(null)
        bitrateHistoryUnderTest.addBitrate(1)
        bitrateHistoryUnderTest.addBitrate(2)

        assertEquals(3, bitrateHistoryUnderTest.bitrateLogList.size)
    }

    @Test
    fun shouldSetBitrateTimeStampAsStartTime() {
        bitrateHistoryUnderTest.addBitrate(0, 2)
        assertEquals(2, bitrateHistoryUnderTest.bitrateLogList[0].start)
    }

    @Test
    fun shouldSetBitrateEndTime() {
        bitrateHistoryUnderTest.addBitrate(0, 2)
        bitrateHistoryUnderTest.addBitrate(1, 3)
        assertEquals(3, bitrateHistoryUnderTest.bitrateLogList[0].end)
    }

    @Test
    fun shouldNotSetBitrateEndTime() {
        bitrateHistoryUnderTest.addBitrate(0, 2)
        assertEquals(0, bitrateHistoryUnderTest.bitrateLogList[0].end)
    }

    @Test
    fun shouldSetBitrateTimeAsDifferenceBetweenFirstAndLastTimes() {
        bitrateHistoryUnderTest.addBitrate(0, 2)
        bitrateHistoryUnderTest.addBitrate(1, 3)
        assertEquals(1, bitrateHistoryUnderTest.bitrateLogList[0].time)
    }

    @Test
    fun shouldNotSetBitrateTime() {
        bitrateHistoryUnderTest.addBitrate(0, 2)
        assertEquals(0, bitrateHistoryUnderTest.bitrateLogList[0].time)
    }

    @Test
    fun shouldEqualFirstBitrateEndTimeWithSecondBitrateStartTime() {
        bitrateHistoryUnderTest.addBitrate(0, 2)
        bitrateHistoryUnderTest.addBitrate(1, 3)
        assertEquals(bitrateHistoryUnderTest.bitrateLogList[0].end ,bitrateHistoryUnderTest.bitrateLogList[1].start)
    }

    @Test
    fun shouldEqualFirstBitrateEndTimeWithSecondBitrateStartTimeWhenNullBitrateAdded() {
        bitrateHistoryUnderTest.addBitrate(0, 2)
        bitrateHistoryUnderTest.addBitrate(null, 500)
        bitrateHistoryUnderTest.addBitrate(1, 3)
        assertEquals(bitrateHistoryUnderTest.bitrateLogList[0].end ,bitrateHistoryUnderTest.bitrateLogList[1].start)
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
        bitrateHistoryUnderTest.addBitrate(100, 3)
        bitrateHistoryUnderTest.addBitrate(110, 4)

        val bitrateList = bitrateHistoryUnderTest.bitrateLogList

        val firstBitrateLog = bitrateList.first()
        val secondBitrateLog = bitrateList[1]
        val thirdBitrateLog = bitrateList[2]


        val sumOfAllBitrateWithTime = ((firstBitrateLog.bitrate * firstBitrateLog.time)
                + (secondBitrateLog.bitrate * secondBitrateLog.time)
                + (thirdBitrateLog.bitrate * thirdBitrateLog.time))

        assertEquals(sumOfAllBitrateWithTime, bitrateHistoryUnderTest.sumOfAllBitrateWithTime())
    }

    @Test
    fun shouldSumTotalTime() {
        bitrateHistoryUnderTest.addBitrate(90, 2)
        bitrateHistoryUnderTest.addBitrate(100, 3)
        bitrateHistoryUnderTest.addBitrate(110, 4)

        val bitrateList = bitrateHistoryUnderTest.bitrateLogList

        val firstBitrateLog = bitrateList.first()
        val secondBitrateLog = bitrateList[1]
        val thirdBitrateLog = bitrateList[2]

        bitrateList.last().time = 5 - bitrateList.last().start

        val totalTimeSum = firstBitrateLog.time + secondBitrateLog.time + thirdBitrateLog.time
        assertEquals(totalTimeSum, bitrateHistoryUnderTest.totalBitrateHistoryTime())
    }

    @Test
    fun shouldEqualsTotalTimeSumWithBitrateDelta() {
        bitrateHistoryUnderTest.addBitrate(90, 2)
        bitrateHistoryUnderTest.addBitrate(100, 3)
        bitrateHistoryUnderTest.addBitrate(110, 4)

        val bitrateList = bitrateHistoryUnderTest.bitrateLogList
        bitrateList.last().end = 5
        bitrateList.last().time = 5 - bitrateList.last().start

        val bitrateDelta = bitrateList.last().end - bitrateList.first().start
        val totalTime = bitrateHistoryUnderTest.totalBitrateHistoryTime()

        assertEquals(bitrateDelta, totalTime)
    }

    @Test
    fun shouldSetLastBitrateTimeAndDivideSums() {

        bitrateHistoryUnderTest.addBitrate(90, 2)
        bitrateHistoryUnderTest.addBitrate(100, 3)
        bitrateHistoryUnderTest.addBitrate(110, 4)

        val bitrateList = bitrateHistoryUnderTest.bitrateLogList

        bitrateList.last().time = 5 - bitrateList.last().start

        val averageBitrate =  bitrateHistoryUnderTest.sumOfAllBitrateWithTime() / bitrateHistoryUnderTest.totalBitrateHistoryTime()

        assertEquals(averageBitrate, bitrateHistoryUnderTest.averageBitrate(5))
    }
}