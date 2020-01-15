package io.clappr.player.plugin.core

import io.clappr.player.base.NamedType
import io.clappr.player.base.UIObject
import io.clappr.player.components.Core
import io.clappr.player.plugin.CorePluginFactory
import io.clappr.player.plugin.PluginEntry
import io.clappr.player.plugin.UIPlugin

open class UICorePlugin(core: Core, override val base: UIObject = UIObject(), name: String = Companion.name) :
        CorePlugin(core, base, name), UIPlugin {
    companion object : NamedType {
        override val name: String = "uicoreplugin"

        fun pluginEntry(name: String, activeInChromelessMode: Boolean = false, factory: CorePluginFactory) =
            PluginEntry.Core(name, activeInChromelessMode, factory)
    }

    override var visibility = UIPlugin.Visibility.HIDDEN

    override val uiObject: UIObject
        get() = base
}