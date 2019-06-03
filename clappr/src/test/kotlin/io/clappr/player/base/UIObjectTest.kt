package io.clappr.player.base

import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
open class UIObjectTest {
    @Test
    fun shouldHandleEvents() {
        BaseObject.applicationContext = ApplicationProvider.getApplicationContext()
        val uiObject = UIObject()
        var callbackWasCalled = false

        uiObject.on("some-event") { callbackWasCalled = true }
        uiObject.trigger("some-event")

        assertTrue("event not triggered", callbackWasCalled)
    }
}