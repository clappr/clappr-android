package io.clappr.player.log

import io.clappr.player.BuildConfig
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23))
class LoggerTest {

    @Test
    fun shouldFormatMessageWithScope() {
        val expected = "[scope] message"
        val result = Logger.formattedMessage("scope", "message")
        assertEquals(expected, result)
    }

    @Test
    fun shouldFormatMessageWithoutScope() {
        val expected = "message"
        val result = Logger.formattedMessage(message = "message")
        assertEquals(expected, result)
    }
}
