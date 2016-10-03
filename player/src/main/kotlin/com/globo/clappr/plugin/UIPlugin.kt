package com.globo.clappr.plugin

import android.view.View
import com.globo.clappr.base.EventInterface
import com.globo.clappr.base.UIObject

class UIPlugin (private val plugin: Plugin = Plugin()) : UIObject(), PluginInterface by plugin {
    override val name = "uiplugin"
    var visibility = PluginVisibility.VISIBLE
}