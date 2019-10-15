package io.clappr.player.extensions

import android.os.Bundle
import io.clappr.player.base.EventData
import io.clappr.player.base.keys.Action
import io.clappr.player.base.keys.Key

data class InputKey(val key: Key, val action: Action)

fun Bundle.extractInputKey(): InputKey? {
    val keyCode = getString(EventData.INPUT_KEY_CODE.value).orEmpty()
    val keyAction = getString(EventData.INPUT_KEY_ACTION.value).orEmpty()

    val key = Key.getByValue(keyCode)
    val action = Action.getByValue(keyAction)

    return when {
        key != null && action != null -> InputKey(key, action)
        else -> null
    }
}