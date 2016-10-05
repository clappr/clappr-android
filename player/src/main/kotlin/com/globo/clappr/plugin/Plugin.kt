package com.globo.clappr.plugin

import com.globo.clappr.base.BaseObject

open class Plugin : BaseObject(), PluginInterface {
    override val name = "plugin"
    var state = PluginState.DISABLED
}