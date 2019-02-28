package io.clappr.player.bitrate

import io.clappr.player.BuildConfig
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [23], shadows = [ShadowLog::class])
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
    fun shouldClear() {
        bitrateHistoryUnderTest.addBitrate(90, 2)
        bitrateHistoryUnderTest.addBitrate(100, 17)
        bitrateHistoryUnderTest.addBitrate(110, 31)
        bitrateHistoryUnderTest.clear()

        assertEquals(0, bitrateHistoryUnderTest.averageBitrate())
    }

    @Test
    fun shouldNotAddNewBitrateIfEqualsTheLastAdded() {
        bitrateHistoryUnderTest.addBitrate(0, 2)
        bitrateHistoryUnderTest.addBitrate(10, 3)
        bitrateHistoryUnderTest.addBitrate(10, 4)

        val bitrateLogsList = listOf(
                BitrateHistory.BitrateLog(0, 2, 0),
                BitrateHistory.BitrateLog(2, 5, 10)
        )

        assertEquals(expectedAverageBitrate(bitrateLogsList), bitrateHistoryUnderTest.averageBitrate(5))
    }


    @Test
    fun shouldAddNewBitrateIfDifferentOnlyFromTheLastAdded() {
        val bitrateLogsList = listOf(
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

        assertEquals(expectedAverageBitrate(bitrateLogsList), bitrateHistoryUnderTest.averageBitrate(4))
    }

    @Test
    fun shouldAverageBitrate() {
        bitrateHistoryUnderTest.addBitrate(90, 2)
        bitrateHistoryUnderTest.addBitrate(100, 17)
        bitrateHistoryUnderTest.addBitrate(110, 31)

        val bitrateLogsList = listOf(
                BitrateHistory.BitrateLog(2,17, 90),
                BitrateHistory.BitrateLog(17,31, 100),
                BitrateHistory.BitrateLog(31,49, 110)
        )

        assertEquals(expectedAverageBitrate(bitrateLogsList), bitrateHistoryUnderTest.averageBitrate(49))
    }

    @Test
    fun shouldHaveAverageBitrateForOneElement() {
        bitrateHistoryUnderTest.addBitrate(90, 2)

        val bitrate = BitrateHistory.BitrateLog(2,17, 90)
        val expectedAverageBitrate = bitrate.bitrate * bitrate.totalActiveTimeInMillis/ bitrate.totalActiveTimeInMillis

        assertEquals(expectedAverageBitrate, bitrateHistoryUnderTest.averageBitrate(17))
    }

    @Test
    fun shouldReturnZeroAverageBitrateWhenEndTimeIsLessThanStartTimeForABitrateLog(){
        val expectedLogMessage = "[BitrateHistory] Error: startTime should be less than endTime - BitrateLog: BitrateLog(startTime=2, endTime=-1, bitrate=90)"

        bitrateHistoryUnderTest.addBitrate(90, 2)
        val bitrateAverage = bitrateHistoryUnderTest.averageBitrate(-1)

        assertEquals(0, bitrateAverage)
        assertEquals(expectedLogMessage, ShadowLog.getLogs()[0].msg)
    }

    private fun expectedAverageBitrate(expectedBitrateLogs: List<BitrateHistory.BitrateLog>): Long {
        val totalTimeSum = expectedBitrateLogs.last().endTime - expectedBitrateLogs.first().startTime

        return expectedBitrateLogs.asSequence()
                .map { it.bitrate * it.totalActiveTimeInMillis }
                .reduce { current, next -> current + next } / totalTimeSum
    }
}