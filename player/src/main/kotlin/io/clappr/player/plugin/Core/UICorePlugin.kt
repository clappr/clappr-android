package io.clappr.player.plugin.Core

import com.globo.clappr.base.NamedType
import com.globo.clappr.components.Core
import com.globo.clappr.plugin.UIPlugin

open class UICorePlugin(val core: Core) : UIPlugin(core) {
    companion object: NamedType {
        override val name = "uicoreplugin"
    }
}