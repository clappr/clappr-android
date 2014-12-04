package com.globo.clappr.base

import android.content.Intent
import com.globo.clappr.BaseTest
import com.globo.clappr.base.BaseObject.EventHandler
import org.junit.Test

class BaseObjectTest extends BaseTest {

  def functionToEventHandler(func : (Intent) => Unit): EventHandler = new EventHandler() {
    override def handleEvent(intent: Intent): Unit = func(intent)
  }

  @Test
  def onCallbackShouldBeCalledOnTrigger = {
    val testObj = new BaseObject()
    var callbackCalled = false
    testObj.on("baseobject:testevent", functionToEventHandler(intent => { callbackCalled = true }))
    testObj.trigger("baseobject:testevent", true)
    callbackCalled shouldBe true
  }

  @Test
  def onCallbackShouldNotBeCalledForAnotherEventName = {
    val testObj = new BaseObject()
    var callbackCalled = false
    testObj.on("baseobject:testevent", functionToEventHandler(intent => { callbackCalled = true }))
    testObj.trigger("baseobject:testevent2", true)
    callbackCalled shouldBe false
  }

  @Test
  def onCallbackShouldNotBeCalledForAnotherContextObject = {
    val testObj = new BaseObject()
    val triggerObj = new BaseObject()
    var callbackCalled = false
    testObj.on("baseobject:testevent", functionToEventHandler(intent => { callbackCalled = true }))
    triggerObj.trigger("baseobject:testevent", true)
    callbackCalled shouldBe false
  }

  @Test
  def onCallbackShouldNotBeCalledWhenHandlerIsRemoved = {
    val testObj = new BaseObject()
    val triggerObj = new BaseObject()
    var callbackCalled = false
    val eventHandler = functionToEventHandler(intent => { callbackCalled = true })
    testObj.on("baseobject:testevent", eventHandler)
    testObj.off("baseobject:testevent", eventHandler)
    triggerObj.trigger("baseobject:testevent", true)
    callbackCalled shouldBe false
  }

  @Test
  def onceCallbackShouldBeCalledOnEvent = {
    val testObj = new BaseObject()
    var callbackCalled = false
    testObj.once("baseobject:testevent", functionToEventHandler(intent => { callbackCalled = true }))
    testObj.trigger("baseobject:testevent", true)
    callbackCalled shouldBe true
  }

  @Test
  def onceCallbackShouldNotBeCalledTwice = {
    val testObj = new BaseObject()
    var callbackCalled = false
    testObj.once("baseobject:testevent", functionToEventHandler(intent => { callbackCalled = true }))
    testObj.trigger("baseobject:testevent", true)
    callbackCalled = false
    testObj.trigger("baseobject:testevent", true)
    callbackCalled shouldBe false
  }

  @Test
  def stopListeningShouldCancelAllEventHandlers = {
    val testObj = new BaseObject()
    var callbackCalled = false
    var callbackCalled2 = false
    testObj.on("baseobject:testevent", functionToEventHandler(intent => { callbackCalled = true }))
    testObj.on("baseobject:testevent2", functionToEventHandler(intent => { callbackCalled2 = true }))
    testObj.stopListening()
    testObj.trigger("baseobject:testevent", true)
    testObj.trigger("baseobject:testevent2", true)
    callbackCalled shouldBe false
    callbackCalled2 shouldBe false
  }

  @Test
  def stopListeningShouldCancelEventHandlersOnlyOnContextObject = {
    val testObj = new BaseObject()
    val testObj2 = new BaseObject()
    var callbackCalled = false
    var callbackCalled2 = false
    testObj.on("baseobject:testevent", functionToEventHandler(intent => { callbackCalled = true }))
    testObj2.on("baseobject:testevent2", functionToEventHandler(intent => { callbackCalled2 = true }))
    testObj.stopListening()
    testObj.trigger("baseobject:testevent", true)
    testObj2.trigger("baseobject:testevent2", true)
    callbackCalled shouldBe false
    callbackCalled2 shouldBe true
  }

  @Test
  def listenToShouldFireCallbackForAnEventOnAGivenContextObject = {
    val testObj = new BaseObject()
    val contextObj = new BaseObject()
    var callbackCalled = false
    testObj.listenTo(contextObj, "baseobject:testevent", functionToEventHandler(intent => { callbackCalled = true }))
    contextObj.trigger("baseobject:testevent", true)
    callbackCalled shouldBe true
  }


  @Test
  def stopListeningShouldCancelHandlerForAnEventOnAGivenContextObject = {
    val testObj = new BaseObject()
    val contextObj = new BaseObject()
    var callbackCalled = false
    val handler = functionToEventHandler(intent => { callbackCalled = true })
    testObj.listenTo(contextObj, "baseobject:testevent", handler)
    testObj.stopListening(contextObj, "baseobject:testevent", handler)
    contextObj.trigger("baseobject:testevent", true)
    callbackCalled shouldBe false
  }
}
