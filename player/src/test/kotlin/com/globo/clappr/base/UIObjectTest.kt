package com.globo.clappr.base

import android.os.Bundle
import com.globo.clappr.BuildConfig
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricGradleTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication

@RunWith(RobolectricGradleTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(21))
public open class UIObjectTest {
    @Test
    fun shouldHandleEvents() {
        BaseObject.context = ShadowApplication.getInstance().applicationContext
        val uiObject = UIObject()
        var callbackWasCalled = false

        uiObject.on("some-event", {bundle: Bundle? -> callbackWasCalled = true})
        uiObject.trigger("some-event")

        assertTrue("event not triggered", callbackWasCalled)
    }
}