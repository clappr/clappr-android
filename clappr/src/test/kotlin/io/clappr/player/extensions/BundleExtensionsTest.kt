package io.clappr.player.extensions

import android.os.Bundle
import io.clappr.player.base.EventData
import io.clappr.player.base.keys.Action
import io.clappr.player.base.keys.Key
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BundleExtensionsTest {
    @Test
    fun `should extract key and action from bundle`() {
        val bundle = mockk<Bundle>(relaxed = true)

        every { bundle.getString(EventData.INPUT_KEY_CODE.value) } returns Key.BACK.value
        every { bundle.getString(EventData.INPUT_KEY_ACTION.value) } returns Action.UP.value

        val inputKey = bundle.extractInputKey()

        assertNotNull(inputKey)
        assertEquals(Key.BACK, inputKey.key)
        assertEquals(Action.UP, inputKey.action)
    }

    @Test
    fun `should extract is long press flag from bundle`() {
        val bundle = mockk<Bundle>()

        every { bundle.getString(EventData.INPUT_KEY_CODE.value) } returns Key.BACK.value
        every { bundle.getString(EventData.INPUT_KEY_ACTION.value) } returns Action.UP.value
        every {
            bundle.getBoolean(EventData.INPUT_KEY_IS_LONG_PRESS.value, false)
        } returns true

        val inputKey = bundle.extractInputKey()

        assertNotNull(inputKey)
        assertTrue(inputKey.isLongPress, "Is long press should be true")
    }

    @Test
    fun `should return null when there is no key available`() {
        val bundle = mockk<Bundle>(relaxed = true)

        every { bundle.getString(EventData.INPUT_KEY_CODE.value) } returns null
        every { bundle.getString(EventData.INPUT_KEY_ACTION.value) } returns Action.UP.value

        val inputKey = bundle.extractInputKey()

        assertNull(inputKey)
    }

    @Test
    fun `should return null when there is no action available`() {
        val bundle = mockk<Bundle>(relaxed = true)

        every { bundle.getString(EventData.INPUT_KEY_CODE.value) } returns Key.BACK.value
        every { bundle.getString(EventData.INPUT_KEY_ACTION.value) } returns null

        val inputKey = bundle.extractInputKey()

        assertNull(inputKey)
    }
}
