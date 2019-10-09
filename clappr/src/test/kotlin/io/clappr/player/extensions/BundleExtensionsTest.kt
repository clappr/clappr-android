package io.clappr.player.extensions

import android.os.Bundle
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.clappr.player.base.EventData
import io.clappr.player.base.keys.Action
import io.clappr.player.base.keys.Key
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BundleExtensionsTest {
    @Test
    fun `should extract key and action from bundle`() {
        val bundle = mock<Bundle>()

        whenever(bundle.getString(EventData.INPUT_KEY_CODE.value)) doReturn Key.BACK.value
        whenever(bundle.getString(EventData.INPUT_KEY_ACTION.value)) doReturn Action.UP.value

        val inputKey = bundle.extractInputKey()

        assertEquals(Key.BACK, inputKey?.key)
        assertEquals(Action.UP, inputKey?.action)
    }

    @Test
    fun `should return null key and action when there is no key available`() {
        val bundle = mock<Bundle>()

        whenever(bundle.getString(EventData.INPUT_KEY_CODE.value)) doReturn null

        val inputKey = bundle.extractInputKey()

        assertNull(inputKey?.key)
    }

    @Test
    fun `should return null action when the action are not mapped`() {
        val bundle = mock<Bundle>()

        whenever(bundle.getString(EventData.INPUT_KEY_ACTION.value)) doReturn null

        val inputKey = bundle.extractInputKey()

        assertNull(inputKey?.action)
    }
}
