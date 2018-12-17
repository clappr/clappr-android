package io.clappr.player.plugin.core

import io.clappr.player.base.NamedType
import io.clappr.player.base.UIObject
import io.clappr.player.components.Core
import io.clappr.player.plugin.UIPlugin

open class UICorePlugin(core: Core, override val base: UIObject = UIObject()) : CorePlugin(core, base), UIPlugin {
    companion object: NamedType {
        override val name = "uicoreplugin"
    }

    override var visibility = UIPlugin.Visibility.HIDDEN

    override val uiObject: UIObject
        get() = base
}