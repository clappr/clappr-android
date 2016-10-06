package com.globo.clappr.plugin

import com.globo.clappr.base.EventInterface
import com.globo.clappr.base.UIObject

enum class PluginVisibility { HIDDEN, VISIBLE }

abstract class UIPlugin (private val uiObject: UIObject = UIObject()) : Plugin(), EventInterface by uiObject {
    var visibility = PluginVisibility.HIDDEN
}