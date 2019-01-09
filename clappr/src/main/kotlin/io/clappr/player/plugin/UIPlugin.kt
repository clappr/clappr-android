package io.clappr.player.plugin

import android.view.View
import io.clappr.player.base.BaseObject
import io.clappr.player.base.EventInterface
import io.clappr.player.base.UIObject

interface UIPlugin: Plugin {
    enum class Visibility { HIDDEN, VISIBLE }

    val uiObject: UIObject

    var visibility: Visibility

    val view: View?
        get() = uiObject.view

    fun render() {
        uiObject.render()
    }

    fun show() {
        visibility = Visibility.VISIBLE
        view?.visibility = View.VISIBLE
    }

    fun hide() {
        visibility = Visibility.HIDDEN
        view?.visibility = View.GONE
    }
}