package com.globo.clappr.plugin.Core

import com.globo.clappr.base.BaseObject
import com.globo.clappr.components.Core
import com.globo.clappr.plugin.Plugin

open class CorePlugin (val core: Core) : Plugin(core) {
    companion object {
        const val name = "coreplugin"
    }
}