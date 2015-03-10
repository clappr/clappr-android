package com.globo.clappr.base

import com.globo.clappr.BaseTest
import groovy.transform.CompileStatic
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.robolectric.annotation.Config

import static org.hamcrest.CoreMatchers.is
import static org.junit.Assert.assertThat

@CompileStatic
@Config(manifest = Config.NONE)
class BaseObjectTest extends BaseTest {
    BaseObject testObj

    @Before
    void setUp() {
        super.setUp()
        testObj = new BaseObject()
    }

    @After
    void tearDown() {
        super.tearDown()
        testObj.stopListening()
    }

    @Test
    void onCallbackShouldBeCalledOnTrigger() {
        def callbackCalled = false
        testObj.on("baseobject:testevent", { intent -> callbackCalled = true })
        testObj.trigger("baseobject:testevent", true)
        assertThat callbackCalled, is(true)
    }

    @Test
    void onCallbackShouldNotBeCalledForAnotherEventName() {
        def callbackCalled = false
        testObj.on("baseobject:testevent", { intent -> callbackCalled = true })
        testObj.trigger("baseobject:testevent2", true)
        assertThat callbackCalled, is(false)
    }

    @Test
    void onCallbackShouldNotBeCalledForAnotherContextObject() {
        def triggerObj = new BaseObject()
        def callbackCalled = false
        testObj.on("baseobject:testevent", { intent -> callbackCalled = true })
        triggerObj.trigger("baseobject:testevent", true)
        assertThat callbackCalled, is(false)
    }

    @Test
    void onCallbackShouldNotBeCalledWhenHandlerIsRemoved() {
        def callbackCalled = false
        BaseObject.EventHandler eventHandler = { intent -> callbackCalled = true }
        testObj.on("baseobject:testevent", eventHandler)
        testObj.off("baseobject:testevent", eventHandler)
        testObj.trigger("baseobject:testevent", true)
        assertThat callbackCalled, is(false)
    }

    @Test
    void onceCallbackShouldBeCalledOnEvent() {
        def callbackCalled = false
        testObj.once("baseobject:testevent", { intent -> callbackCalled = true })
        testObj.trigger("baseobject:testevent", true)
        assertThat callbackCalled, is(true)
    }

    @Test
    void onceCallbackShouldNotBeCalledTwice() {
        def callbackCalled = false
        testObj.once("baseobject:testevent", { intent -> callbackCalled = true })
        testObj.trigger("baseobject:testevent", true)
        callbackCalled = false
        testObj.trigger("baseobject:testevent", true)
        assertThat callbackCalled, is(false)
    }

    @Test
    void stopListeningShouldCancelAllEventHandlers() {
        def callbackCalled = false
        def callbackCalled2 = false
        testObj.on("baseobject:testevent", { intent -> callbackCalled = true })
        testObj.on("baseobject:testevent2", { intent -> callbackCalled2 = true })
        testObj.stopListening()
        testObj.trigger("baseobject:testevent", true)
        testObj.trigger("baseobject:testevent2", true)
        assertThat callbackCalled, is(false)
        assertThat callbackCalled2, is(false)
    }

    @Test
    void stopListeningShouldCancelEventHandlersOnlyOnContextObject() {
        def testObj2 = new BaseObject()
        def callbackCalled = false
        def callbackCalled2 = false
        testObj.on("baseobject:testevent", { intent -> callbackCalled = true })
        testObj2.on("baseobject:testevent2", { intent -> callbackCalled2 = true })
        testObj.stopListening()
        testObj.trigger("baseobject:testevent", true)
        testObj2.trigger("baseobject:testevent2", true)
        assertThat callbackCalled, is(false)
        assertThat callbackCalled2, is(true)
    }

    @Test
    void listenToShouldFireCallbackForAnEventOnAGivenContextObject() {
        def contextObj = new BaseObject()
        def callbackCalled = false
        testObj.listenTo(contextObj, "baseobject:testevent", { intent -> callbackCalled = true })
        contextObj.trigger("baseobject:testevent", true)
        assertThat callbackCalled, is(true)
    }


    @Test
    void stopListeningShouldCancelHandlerForAnEventOnAGivenContextObject() {
        def testObj = new BaseObject()
        def contextObj = new BaseObject()
        def callbackCalled = false
        BaseObject.EventHandler handler = { intent -> callbackCalled = true }
        testObj.listenTo(contextObj, "baseobject:testevent", handler)
        testObj.stopListening(contextObj, "baseobject:testevent", handler)
        contextObj.trigger("baseobject:testevent", true)
        assertThat callbackCalled, is(false)
    }
}
