package com.globo.clappr.plugin.Core

import com.globo.clappr.base.BaseObject
import com.globo.clappr.components.Core
import com.globo.clappr.plugin.UIPlugin

open class UICorePlugin(val core: Core) : UIPlugin(core) {
    companion object {
        const val name = "uicoreplugin"
    }
}