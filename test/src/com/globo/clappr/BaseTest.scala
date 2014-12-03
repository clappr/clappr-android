package com.globo.clappr

import android.content.Context
import org.junit.runner.RunWith
import org.robolectric.{Robolectric, RobolectricTestRunner}
import scala.collection.JavaConversions._
import org.scalatest.Matchers

@RunWith(classOf[RobolectricTestRunner])
abstract class BaseTest extends Matchers {
  val context : Context = Robolectric.application
}
