package io.clappr.player.plugin

import io.clappr.player.base.BaseObject
import io.clappr.player.base.EventInterface
import io.clappr.player.base.UIObject

abstract class UIPlugin (component: BaseObject, private val uiObject: UIObject = UIObject()) : Plugin(component), EventInterface by uiObject {
    enum class Visibility { HIDDEN, VISIBLE }

    var visibility = Visibility.HIDDEN
}