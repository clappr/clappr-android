package com.globo.clappr.base

import android.content.Context
import android.view.View
import com.globo.clappr.BaseTest
import com.globo.clappr.components.PlayerInfo
import org.junit.Test
import org.robolectric.Robolectric

class CustomView(context: Context) extends View(context)

class UIObjectTest extends BaseTest {
  @Test
  def objectShouldFirstCreateViewThroughTheRenderMethod = {
    val viewToBeRendered = new View(PlayerInfo.getContext)
    val testObj = new UIObject() {
      override def render(): UIObject = {
        setView(viewToBeRendered)
        return this
      }
    }
    testObj.getView().shouldEqual(viewToBeRendered)
  }

  @Test
  def objectShouldCreateDefaultViewIfRenderDoesNotCreateIt = {
    val testObj = new UIObject()
    testObj.getView.shouldNot(equal(null))
    testObj.getView.getClass.shouldBe(classOf[View])
  }

  @Test
  def objectShouldCreateViewWithCustomClassIfRenderDoesNotCreateIt = {
    PlayerInfo.setContext(Robolectric.application)
    val testObj = new UIObject() {
      override def viewClass() : Class[_] = {
        return classOf[CustomView]
      }
    }
    testObj.getView.getClass.shouldBe(classOf[CustomView])
  }
}
