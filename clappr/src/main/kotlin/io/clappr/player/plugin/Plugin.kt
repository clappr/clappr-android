package io.clappr.player.plugin

import io.clappr.player.base.BaseObject
import io.clappr.player.base.NamedType

abstract class Plugin(val component: BaseObject) : BaseObject(), NamedType {

    enum class State { ENABLED, DISABLED }

    open var state = State.DISABLED

    open fun destroy() {}
}