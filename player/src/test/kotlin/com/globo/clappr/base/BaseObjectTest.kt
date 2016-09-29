package com.globo.clappr.base

import android.os.Bundle
import com.globo.clappr.BuildConfig
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23))
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

    @Test
    fun linstenToShouldFireAnEvent() {
        val contextObject = BaseObject()

        baseObject?.listenTo(contextObject, eventName, callBack)
        contextObject.trigger(eventName)

        assertTrue("event not triggered", callBackWasCalled)
    }

    @Test
    fun offCallbackNotCalledIfRemoved() {
        val listenId = baseObject?.on(eventName, callBack)
        baseObject?.off(listenId!!)
        baseObject?.trigger(eventName)

        assertFalse("event triggered", callBackWasCalled)
    }

    @Test
    fun offOtherShouldBeCalledAfterRemoval() {
        var anotherCallbackWasCalled = false
        val anotherCallback = { bundle: Bundle? -> anotherCallbackWasCalled = true}

        val listenId = baseObject?.on(eventName, callBack)
        baseObject?.on(eventName, anotherCallback)

        baseObject?.off(listenId!!)
        baseObject?.trigger(eventName)

        assertFalse("event triggered", callBackWasCalled)
        assertTrue("event not triggered", anotherCallbackWasCalled)
    }

    @Test
    fun stopListeningShouldCancelAllHandlers() {
        baseObject?.on(eventName, callBack)
        baseObject?.on("another-event", callBack)

        baseObject?.stopListening()

        baseObject?.trigger(eventName)
        baseObject?.trigger("another-event")

        assertFalse("event triggered", callBackWasCalled)
    }

    @Test
    fun stopListeningShouldCancelOnlyOnObject() {
        val anotherObject = BaseObject()
        var anotherCallbackWasCalled = false
        anotherObject.on(eventName, { bundle: Bundle? -> anotherCallbackWasCalled = true})

        baseObject?.on(eventName, callBack)

        baseObject?.stopListening()

        baseObject?.trigger(eventName)
        anotherObject.trigger(eventName)

        assertFalse("event triggered", callBackWasCalled)
        assertTrue("event not triggered", anotherCallbackWasCalled)
    }

    @Test
    fun stopListeningShouldCancelOnBaseObject() {
        val contextObject = BaseObject()

        val listenId = baseObject?.listenTo(contextObject, eventName, callBack)
        baseObject?.stopListening(listenId!!)

        contextObject.trigger(eventName)

        assertFalse("event triggered", callBackWasCalled)
    }
}