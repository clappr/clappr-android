package com.globo.clappr.plugin.Core

import com.globo.clappr.base.BaseObject
import com.globo.clappr.components.Core
import com.globo.clappr.plugin.Plugin

open class CorePlugin (): Plugin() {
    override val name = "coreplugin"

    var core : Core? = null
    override fun setup(component: BaseObject) {
        core = component as? Core
    }
}