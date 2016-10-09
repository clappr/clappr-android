package com.globo.clappr.plugin

import com.globo.clappr.base.BaseObject

enum class PluginState { ENABLED, DISABLED }

abstract class Plugin(component: BaseObject) : BaseObject() {
    companion object {
        const val name: String = ""
    }
    var state = PluginState.DISABLED
}