package io.clappr.player.plugin.Core

import com.globo.clappr.base.NamedType
import com.globo.clappr.components.Core
import com.globo.clappr.plugin.Plugin

open class CorePlugin (val core: Core) : Plugin(core) {
    companion object: NamedType {
        override val name = "coreplugin"
    }
}