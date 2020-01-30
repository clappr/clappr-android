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

        val entry = PluginEntry.Core(name = name, factory = ::ExternalInputPlugin)
    }

    private val supportedKeys = hashMapOf(
        KeyEvent.KEYCODE_MEDIA_PLAY to Key.PLAY.value,
        KeyEvent.KEYCODE_MEDIA_PAUSE to Key.PAUSE.value,
        KeyEvent.KEYCODE_MEDIA_STOP to Key.STOP.value,
        KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE to Key.PLAY_PAUSE.value,
        KeyEvent.KEYCODE_DPAD_UP to Key.UP.value,
        KeyEvent.KEYCODE_DPAD_DOWN to Key.DOWN.value,
        KeyEvent.KEYCODE_DPAD_RIGHT to Key.RIGHT.value,
        KeyEvent.KEYCODE_DPAD_LEFT to Key.LEFT.value,
        KeyEvent.KEYCODE_BACK to Key.BACK.value,
        KeyEvent.KEYCODE_MEDIA_FAST_FORWARD to Key.FAST_FORWARD.value,
        KeyEvent.KEYCODE_MEDIA_REWIND to Key.REWIND.value
    )

    override fun holdKeyEvent(event: KeyEvent) {
        core.trigger(Event.DID_RECEIVE_INPUT_KEY.value, Bundle().apply {
            putString(EventData.INPUT_KEY_CODE.value, getKeyCode(event.keyCode))
            putString(EventData.INPUT_KEY_ACTION.value, getActionCode(event.action))
            putBoolean(EventData.INPUT_KEY_IS_LONG_PRESS.value, event.isLongPress)
        })
    }

    private fun getKeyCode(keyCode: Int) = supportedKeys[keyCode] ?: Key.UNDEFINED.value

    private fun getActionCode(action: Int) = when (action) {
        KeyEvent.ACTION_UP -> Action.UP.value
        KeyEvent.ACTION_DOWN -> Action.DOWN.value
        else -> Action.UNDEFINED.value
    }
}