package io.clappr.player.plugin.core.externalinput

import android.view.KeyEvent
import io.clappr.player.base.NamedType
import io.clappr.player.components.Core
import io.clappr.player.log.Logger
import io.clappr.player.plugin.PluginEntry
import io.clappr.player.plugin.core.CorePlugin

class ExternalInputPlugin(core: Core) : CorePlugin(core, name = name), ExternalInputDevice {
    companion object : NamedType {
        override val name = "externalInputPlugin"

        val entry = PluginEntry.Core(name = name, factory = { core -> ExternalInputPlugin(core) })
    }

    override fun holdKeyEvent(keyCode: Int, event: KeyEvent) {
        Logger.debug(name, "Keycode: $keyCode, Event: $event")
    }
}