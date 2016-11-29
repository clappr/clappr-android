package io.clappr.player.plugin

import com.globo.clappr.base.BaseObject
import com.globo.clappr.base.NamedType

abstract class Plugin(val component: BaseObject) : BaseObject(), NamedType {

    enum class State { ENABLED, DISABLED }

    var state = State.DISABLED
}