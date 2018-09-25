package io.clappr.player.extensions

import org.junit.Test
import kotlin.test.assertEquals

class DoubleExtensionsTest {

    @Test
    fun shouldConvertDoubleToStringTimeIntervalWithHoursMinutesAndSeconds() {
        val expectedTimeInterval = "01:30:15"
        val doubleNumber = 5415.0

        assertEquals(expectedTimeInterval, doubleNumber.asTimeInterval())
    }

    @Test
    fun shouldConvertDoubleToStringTimeIntervalOnlyWithMinutesAndSeconds() {
        val expectedTimeInterval = "30:15"
        val doubleNumber = 1815.0

        assertEquals(expectedTimeInterval, doubleNumber.asTimeInterval())
    }
}