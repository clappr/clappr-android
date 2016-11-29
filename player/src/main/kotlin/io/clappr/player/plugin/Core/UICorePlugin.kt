package io.clappr.player.plugin.core

import io.clappr.player.base.NamedType
import io.clappr.player.components.Core
import io.clappr.player.plugin.UIPlugin

open class UICorePlugin(val core: Core) : UIPlugin(core) {
    companion object: NamedType {
        override val name = "uicoreplugin"
    }
}