package com.globo.clappr.base

import android.content.Intent
import com.globo.clappr.BaseTest
import com.globo.clappr.base.BaseObject.EventHandler
import groovy.transform.CompileStatic
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.robolectric.annotation.Config

import static org.junit.Assert.assertThat
import static org.hamcrest.CoreMatchers.*
import static org.junit.matchers.JUnitMatchers.*

@CompileStatic
@Config(manifest=Config.NONE)
class BaseObjectTest extends BaseTest {
  BaseObject testObj

  @BeforeClass static void setUpClass() {
    PlayerInfo.setContext(Robolectric.application)
  }

  @Before
  void setUp() {
    testObj = new BaseObject()
  }

  @After
  void tearDown() {
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
    def eventHandler = { intent -> println('teste'); callbackCalled = true }
    testObj.on("baseobject:testevent", eventHandler)
    testObj.off("baseobject:testevent", eventHandler)
    testObj.trigger("baseobject:testevent", true)
    // assertThat callbackCalled, is(false)
    assert callbackCalled == false
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
}
