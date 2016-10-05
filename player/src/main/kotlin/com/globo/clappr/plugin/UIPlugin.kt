package com.globo.clappr.plugin

import com.globo.clappr.base.EventInterface
import com.globo.clappr.base.UIObject

open class UIPlugin (private val uiObject: UIObject = UIObject()) : Plugin(), EventInterface by uiObject {
    override val name = "uiplugin"
    var visibility = PluginVisibility.HIDDEN
}