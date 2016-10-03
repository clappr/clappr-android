package com.globo.clappr.plugin

import com.globo.clappr.base.BaseObject

class Plugin : BaseObject(), PluginInterface {
    override val name = "plugin"
    var state = PluginState.ENABLED
}