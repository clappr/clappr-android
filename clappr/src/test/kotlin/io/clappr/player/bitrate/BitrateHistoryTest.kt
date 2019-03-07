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

        assertEquals(6, bitrateHistoryUnderTest.averageBitrate(5))
    }


    @Test
    fun shouldAddNewBitrateIfDifferentOnlyFromTheLastAdded() {
        bitrateHistoryUnderTest.addBitrate(0, 2)
        bitrateHistoryUnderTest.addBitrate(10, 3)
        bitrateHistoryUnderTest.addBitrate(0, 4)
        bitrateHistoryUnderTest.addBitrate(10, 4)
        bitrateHistoryUnderTest.addBitrate(0, 4)
        bitrateHistoryUnderTest.addBitrate(10, 4)

        assertEquals(5, bitrateHistoryUnderTest.averageBitrate(4))
    }

    @Test
    fun shouldNotHaveTotalBitrateHistoryTimeEqualToZero(){
        bitrateHistoryUnderTest.addBitrate(90, 2)
        val bitrateAverage = bitrateHistoryUnderTest.averageBitrate(2)

        assertEquals(0, bitrateAverage)
    }

    @Test(expected = BitrateHistory.BitrateLog.WrongTimeIntervalException::class)
    fun shouldNotAddBitrateWithTimeStampBellowLastAddedBitrate(){
        val expectedLogMessage = "[BitrateHistory] Bitrate list time stamp should be crescent. Can not add time stamp with value bellow 2"

        bitrateHistoryUnderTest.addBitrate(90, 2)
        bitrateHistoryUnderTest.addBitrate(100, 1)

        assertEquals(expectedLogMessage, ShadowLog.getLogs()[0].msg)
    }

    @Test
    fun shouldAverageBitrate() {
        bitrateHistoryUnderTest.addBitrate(90, 2)
        bitrateHistoryUnderTest.addBitrate(100, 17)
        bitrateHistoryUnderTest.addBitrate(110, 31)

        assertEquals(100, bitrateHistoryUnderTest.averageBitrate(49))
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

    @Test
    fun shouldReturnZeroAverageBitrateWhenEndTimeIsEqualThanStartTimeForABitrateLog(){
        bitrateHistoryUnderTest.addBitrate(90, 2)
        bitrateHistoryUnderTest.addBitrate(100, 2)
        bitrateHistoryUnderTest.addBitrate(110, 3)
        bitrateHistoryUnderTest.addBitrate(120, 3)

        val bitrateAverage = bitrateHistoryUnderTest.averageBitrate(4)

        assertEquals(110, bitrateAverage)
    }
}