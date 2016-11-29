package io.clappr.player.plugin.container

import io.clappr.player.base.NamedType
import io.clappr.player.components.Container
import io.clappr.player.plugin.Plugin

open class ContainerPlugin(val container: Container) : Plugin(container) {
    companion object: NamedType {
        override val name = "containerplugin"
    }
}