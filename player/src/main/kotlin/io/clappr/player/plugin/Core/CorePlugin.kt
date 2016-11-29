package io.clappr.player.plugin.core

import io.clappr.player.base.NamedType
import io.clappr.player.components.Core
import io.clappr.player.plugin.Plugin

open class CorePlugin (val core: Core) : Plugin(core) {
    companion object: NamedType {
        override val name = "coreplugin"
    }
}