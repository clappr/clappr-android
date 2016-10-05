package com.globo.clappr.plugin.Core

import com.globo.clappr.components.Core
import com.globo.clappr.plugin.Plugin

open class CorePlugin (): Plugin() {
    override val name = "coreplugin"

    var core : Core? = null
}