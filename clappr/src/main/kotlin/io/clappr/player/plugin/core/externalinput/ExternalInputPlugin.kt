package io.clappr.player.plugin.core.externalinput

import android.os.Bundle
import android.view.KeyEvent
import io.clappr.player.base.Event
import io.clappr.player.base.EventData
import io.clappr.player.base.NamedType
import io.clappr.player.base.keys.Action
import io.clappr.player.base.keys.Key
import io.clappr.player.components.Core
import io.clappr.player.plugin.PluginEntry
import io.clappr.player.plugin.core.CorePlugin

class ExternalInputPlugin(core: Core) : CorePlugin(core, name = name), ExternalInputDevice {
    companion object : NamedType {
        override val name = "externalInputPlugin"

        val entry = PluginEntry.Core(name = name, factory = { core -> ExternalInputPlugin(core) })
    }


    private val supportedKeys = hashMapOf(
            KeyEvent.KEYCODE_MEDIA_PLAY to Key.PLAY.value,
            KeyEvent.KEYCODE_MEDIA_PAUSE to Key.PAUSE.value,
            KeyEvent.KEYCODE_MEDIA_STOP to Key.STOP.value,
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE to Key.PLAY_PAUSE.value)
    
    override fun holdKeyEvent(event: KeyEvent) {
        core.trigger(Event.DID_RECEIVE_INPUT_KEY.value, Bundle().apply {
            putString(EventData.PRESSED_KEY_CODE.value, getKeyCode(event.keyCode))
            putString(EventData.PRESSED_KEY_ACTION.value, getActionCode(event.action))
        })
    }

    private fun getKeyCode(keyCode: Int) = supportedKeys[keyCode] ?: Key.UNDEFINED.value

    private fun getActionCode(action: Int) = when (action) {
        KeyEvent.ACTION_UP -> Action.UP.value
        KeyEvent.ACTION_DOWN -> Action.DOWN.value
        else -> Action.UNDEFINED.value
    }
}