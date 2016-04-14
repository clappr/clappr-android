package com.globo.clappr.base

import android.os.Bundle
import com.globo.clappr.BuildConfig
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricGradleTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication

@RunWith(RobolectricGradleTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(21))
public open class BaseObjectTest {
    var baseObject: BaseObject? = null
    var callBackWasCalled = false

    val eventName = "some-event"
    val callBack = { bundle: Bundle? ->
        callBackWasCalled = true
    }

    @Before
    fun setup() {
        BaseObject.context = ShadowApplication.getInstance().applicationContext
        baseObject = BaseObject()
        callBackWasCalled = false
    }

    @Test
    fun onCallbackShouldBeCalledOnEventTrigger() {
        baseObject?.on(eventName, callBack)
        baseObject?.trigger(eventName)

        assertTrue("event not triggered", callBackWasCalled)
    }

    @Test
    fun onCallbackShouldReceiveUserInfo() {
        var value = "Not Expected"
        baseObject?.on(eventName, {bundle: Bundle? -> value = bundle?.getString("value")!!})

        val userData = Bundle()
        userData.putString("value", "Expected")
        baseObject?.trigger(eventName, userData)

        assertTrue("userInfo not received", value == "Expected")
    }

    @Test
    fun onCallbackShouldBeCalledForEveryCallback() {
        baseObject?.on(eventName, callBack)

        var secondCallBackCalled = false
        baseObject?.on(eventName, {bundle: Bundle? -> secondCallBackCalled = true})

        baseObject?.trigger(eventName)

        assertTrue("event not triggered", callBackWasCalled)
        assertTrue("second event not triggered", secondCallBackCalled)
    }

    @Test
    fun onCallBackShouldNotBeCalledforAnotherTrigger() {
        baseObject?.on(eventName, callBack)

        baseObject?.trigger("another-event")

        assertFalse("event triggered", callBackWasCalled)
    }

    @Test
    fun onCallBackShouldNotBeCalledforAnotherObject() {
        val anotherObject = BaseObject()

        baseObject?.on(eventName, callBack)

        anotherObject.trigger(eventName)

        assertFalse("event triggered", callBackWasCalled)
    }

    @Test
    fun onceCallbackShouldBeCalledOnEvent() {
        baseObject?.once(eventName, callBack)
        baseObject?.trigger(eventName)

        assertTrue("event not triggered", callBackWasCalled)
    }

    @Test
    fun onceCallbackShouldNotBeCalledTwice() {
        baseObject?.once(eventName, callBack)

        baseObject?.trigger(eventName)
        callBackWasCalled = false
        baseObject?.trigger(eventName)

        assertFalse("event triggered", callBackWasCalled)
    }

    @Test
    fun onceCallbackShouldNotBeCalledIfRemoved() {
        val listenId = baseObject?.once(eventName, callBack)
        baseObject?.off(listenId!!)
        baseObject?.trigger(eventName)

        assertFalse("event triggered", callBackWasCalled)
    }
}