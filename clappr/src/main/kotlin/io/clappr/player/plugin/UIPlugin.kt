package io.clappr.player.plugin

import android.view.View
import io.clappr.player.base.BaseObject
import io.clappr.player.base.EventInterface
import io.clappr.player.base.UIObject

abstract class UIPlugin (component: BaseObject, private val uiObject: UIObject = UIObject()) : Plugin(component), EventInterface by uiObject {
    enum class Visibility { HIDDEN, VISIBLE }

    open var visibility = Visibility.HIDDEN

    open val view: View?
        get() = uiObject.view

    open fun render() {
        uiObject.render()
    }
}