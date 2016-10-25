package com.globo.clappr.plugin

import com.globo.clappr.base.BaseObject
import com.globo.clappr.base.NamedType

enum class PluginState { ENABLED, DISABLED }

abstract class Plugin(val component: BaseObject) : BaseObject(), NamedType {
    var state = PluginState.DISABLED
}