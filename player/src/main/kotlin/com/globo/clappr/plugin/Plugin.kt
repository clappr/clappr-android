package com.globo.clappr.plugin

import com.globo.clappr.base.BaseObject

enum class PluginState { ENABLED, DISABLED }

abstract class Plugin : BaseObject() {
    abstract val name: String
    abstract fun setup(component: BaseObject)
    var state = PluginState.DISABLED
}