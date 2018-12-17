package io.clappr.player.plugin

import io.clappr.player.base.EventInterface
import io.clappr.player.base.NamedType

interface Plugin : EventInterface, NamedType {

    enum class State { ENABLED, DISABLED }

    val state: State
        get() = State.DISABLED

    fun destroy() {
        stopListening()
    }
}