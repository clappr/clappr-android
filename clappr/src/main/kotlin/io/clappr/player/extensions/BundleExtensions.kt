package io.clappr.player.extensions

import android.os.Bundle
import io.clappr.player.base.EventData
import io.clappr.player.base.keys.Action
import io.clappr.player.base.keys.Key

fun Bundle.extractInputKey(): Pair<Key, Action> {
    val keyCode = getString(EventData.INPUT_KEY_CODE.value).orEmpty()
    val keyAction = getString(EventData.INPUT_KEY_ACTION.value).orEmpty()

    val key = Key.getByValue(keyCode) ?: Key.UNDEFINED
    val action = Action.getByValue(keyAction) ?: Action.UNDEFINED

    return key to action
}