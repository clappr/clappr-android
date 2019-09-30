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

class BundleExtensionsTest {
    @Test
    fun `should extract key and action from bundle`() {
        val bundle = mock<Bundle>()

        whenever(bundle.getString(EventData.INPUT_KEY_CODE.value)) doReturn Key.BACK.value
        whenever(bundle.getString(EventData.INPUT_KEY_ACTION.value)) doReturn Action.UP.value

        val (key, action) = bundle.extractInputKey()

        assertEquals(Key.BACK, key)
        assertEquals(Action.UP, action)
    }

    @Test
    fun `should return undefined key and action when there is no key and action available`() {
        val bundle = mock<Bundle>()

        whenever(bundle.getString(EventData.INPUT_KEY_CODE.value)) doReturn null
        whenever(bundle.getString(EventData.INPUT_KEY_ACTION.value)) doReturn null

        val (key, action) = bundle.extractInputKey()

        assertEquals(Key.UNDEFINED, key)
        assertEquals(Action.UNDEFINED, action)
    }

    @Test
    fun `should return undefined key and action when the key and action are not mapped`() {
        val bundle = mock<Bundle>()

        whenever(bundle.getString(EventData.INPUT_KEY_CODE.value)) doReturn "unknown"
        whenever(bundle.getString(EventData.INPUT_KEY_ACTION.value)) doReturn "unknown"

        val (key, action) = bundle.extractInputKey()

        assertEquals(Key.UNDEFINED, key)
        assertEquals(Action.UNDEFINED, action)
    }
}
