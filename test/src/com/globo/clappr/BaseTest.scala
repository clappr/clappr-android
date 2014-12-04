package com.globo.clappr

import android.content.Context
import com.globo.clappr.components.PlayerInfo
import org.junit.runner.RunWith
import org.robolectric.{Robolectric, RobolectricTestRunner}
import org.scalatest.Matchers

@RunWith(classOf[RobolectricTestRunner])
abstract class BaseTest extends Matchers {
  PlayerInfo.context = Robolectric.application
}
