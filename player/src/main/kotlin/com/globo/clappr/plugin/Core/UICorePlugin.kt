package com.globo.clappr.plugin.Core

import com.globo.clappr.base.BaseObject
import com.globo.clappr.components.Core
import com.globo.clappr.plugin.UIPlugin

open class UICorePlugin() : UIPlugin() {
    override val name = "uicoreplugin"

    var core : Core? = null
    override fun setup(context: BaseObject) {
        core = context as? Core
    }
}