package io.clappr.player.base

import android.os.Bundle
import io.clappr.player.log.Logger
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class BaseObjectTest {
    private lateinit var baseObject: BaseObject
    private var callbackWasCalled = false

    private val eventName = "some-event"
    private val callback: EventHandler = { callbackWasCalled = true }

    private lateinit var logger: Logger

    @Before
    fun setup() {
        logger = mockk(relaxUnitFun = true)
        baseObject = BaseObject(logger)
        callbackWasCalled = false
    }

    @Test
    fun onCallbackShouldBeCalledOnEventTrigger() {
        baseObject.on(eventName, callback)
        baseObject.trigger(eventName)

        assertTrue("event not triggered", callbackWasCalled)
    }

    @Test
    fun onCallbackShouldReceiveUserInfo() {
        var value: String? = null

        val userData = mockk<Bundle>()
        every { userData.getString("value") } returns "Expected"

        baseObject.on(eventName) { value = it?.getString("value") }
        baseObject.trigger(eventName, userData)

        assertTrue("userInfo not received", value == "Expected")
    }

    @Test
    fun onCallbackShouldBeCalledForEveryCallback() {
        baseObject.on(eventName, callback)

        var secondCallbackCalled = false
        baseObject.on(eventName) { secondCallbackCalled = true }

        baseObject.trigger(eventName)

        assertTrue("event not triggered", callbackWasCalled)
        assertTrue("second event not triggered", secondCallbackCalled)
    }

    @Test
    fun onCallbackShouldOnlyBeRegisteredOnce() {
        var numberOfCalls = 0
        val localCallback: EventHandler = { numberOfCalls += 1 }
        baseObject.on(eventName, localCallback)
        baseObject.on(eventName, localCallback)

        baseObject.trigger(eventName)

        assertEquals("event handler should only be called once", 1, numberOfCalls)
    }

    @Test
    fun onCallbackShouldNotBeCalledforAnotherTrigger() {
        baseObject.on(eventName, callback)

        baseObject.trigger("another-event")

        assertFalse("event triggered", callbackWasCalled)
    }

    @Test
    fun onCallbackShouldNotBeCalledforAnotherObject() {
        val anotherObject = BaseObject(logger)

        baseObject.on(eventName, callback)

        anotherObject.trigger(eventName)

        assertFalse("event triggered", callbackWasCalled)
    }

    @Test
    fun onceCallbackShouldBeCalledOnEvent() {
        baseObject.once(eventName, callback)
        baseObject.trigger(eventName)

        assertTrue("event not triggered", callbackWasCalled)
    }

    @Test
    fun onceCallbackShouldNotBeCalledTwice() {
        baseObject.once(eventName, callback)

        baseObject.trigger(eventName)
        callbackWasCalled = false
        baseObject.trigger(eventName)

        assertFalse("event triggered", callbackWasCalled)
    }

    @Test
    fun onceCallbackShouldNotBeCalledIfRemoved() {
        val listenId = baseObject.once(eventName, callback)
        baseObject.off(listenId)
        baseObject.trigger(eventName)

        assertFalse("event triggered", callbackWasCalled)
    }

    @Test
    fun listenToShouldFireAnEvent() {
        val contextObject = BaseObject(logger)

        baseObject.listenTo(contextObject, eventName, callback)
        contextObject.trigger(eventName)

        assertTrue("event not triggered", callbackWasCalled)
    }

    @Test
    fun listenToShouldHandleCallbackException() {
        val exception = NullPointerException()
        val brokenCallback: EventHandler = { throw exception }
        val expectedLogMessage =
            "Plugin ${brokenCallback.javaClass.name} crashed during invocation of event $eventName"

        val contextObject = BaseObject(logger)

        baseObject.listenTo(contextObject, eventName, brokenCallback)
        contextObject.trigger(eventName)

        verify {
            logger.error(BaseObject::class.java.simpleName, expectedLogMessage, exception)
        }
    }

    @Test
    fun offCallbackNotCalledIfRemoved() {
        val listenId = baseObject.on(eventName, callback)
        baseObject.off(listenId)
        baseObject.trigger(eventName)

        assertFalse("event triggered", callbackWasCalled)
    }

    @Test
    fun offOtherShouldBeCalledAfterRemoval() {
        var anotherCallbackWasCalled = false
        val anotherCallback: EventHandler = { anotherCallbackWasCalled = true }

        val listenId = baseObject.on(eventName, callback)
        baseObject.on(eventName, anotherCallback)

        baseObject.off(listenId)
        baseObject.trigger(eventName)

        assertFalse("event triggered", callbackWasCalled)
        assertTrue("event not triggered", anotherCallbackWasCalled)
    }

    @Test
    fun stopListeningShouldCancelAllHandlers() {
        baseObject.on(eventName, callback)
        baseObject.on("another-event", callback)

        baseObject.stopListening()

        baseObject.trigger(eventName)
        baseObject.trigger("another-event")

        assertFalse("event triggered", callbackWasCalled)
    }

    @Test
    fun stopListeningShouldCancelOnlyOnObject() {
        val anotherObject = BaseObject(logger)
        var anotherCallbackWasCalled = false
        anotherObject.on(eventName) { anotherCallbackWasCalled = true }

        baseObject.on(eventName, callback)

        baseObject.stopListening()

        baseObject.trigger(eventName)
        anotherObject.trigger(eventName)

        assertFalse("event triggered", callbackWasCalled)
        assertTrue("event not triggered", anotherCallbackWasCalled)
    }

    @Test
    fun stopListeningShouldCancelOnBaseObject() {
        val contextObject = BaseObject(logger)

        val listenId = baseObject.listenTo(contextObject, eventName, callback)
        baseObject.stopListening(listenId)

        contextObject.trigger(eventName)

        assertFalse("event triggered", callbackWasCalled)
    }
}