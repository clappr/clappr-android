package io.clappr.player.plugin.core.externalinput

import android.os.Bundle
import android.view.KeyEvent
import io.clappr.player.base.Event
import io.clappr.player.base.EventData
import io.clappr.player.base.NamedType
import io.clappr.player.base.keys.Action
import io.clappr.player.base.keys.Key
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

        val aKeyCode = getKeyCode(event.keyCode)
        val actionCode = getActionCode(event.action)

        core.trigger(Event.KEY_PRESSED.value, Bundle().apply {
            putString(EventData.PRESSED_KEY_CODE.value, aKeyCode)
            putString(EventData.PRESSED_KEY_ACTION.value, actionCode)
        })
    }

    private fun getKeyCode(keyCode: Int) = when (keyCode) {
        KeyEvent.KEYCODE_MEDIA_PLAY -> Key.PLAY.value
        KeyEvent.KEYCODE_MEDIA_PAUSE -> Key.PAUSE.value
        else -> Key.UNDEFINED.value
    }

    private fun getActionCode(action: Int) = when (action) {
        KeyEvent.ACTION_UP -> Action.UP.value
        KeyEvent.ACTION_DOWN -> Action.DOWN.value
        else -> Action.UNDEFINED.value
    }
}