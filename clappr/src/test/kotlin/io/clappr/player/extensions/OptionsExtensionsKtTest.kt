package io.clappr.player.extensions

import io.clappr.player.base.ClapprOption
import io.clappr.player.base.Options
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OptionsExtensionsKtTest {

    @Test
    fun `should return false when option not present`() {
        assertFalse(Options().isChromeless)

    }

    @Test
    fun `should return boolean value when option present`() {
        val options = Options(options = hashMapOf(ClapprOption.CHROMELESS.value to true))
        assertTrue(options.isChromeless)

    }

    @Test
    fun `should return false value when option CHROMELESS is not boolean`() {
        val options = Options(options = hashMapOf(ClapprOption.CHROMELESS.value to "anotherValue"))
        assertFalse(options.isChromeless)
    }
}