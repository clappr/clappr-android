package com.globo.clappr.plugin.container

import com.globo.clappr.base.NamedType
import com.globo.clappr.components.Container
import com.globo.clappr.plugin.Plugin

open class ContainerPlugin(val container: Container) : Plugin(container) {
    companion object: NamedType {
        override val name = "containerplugin"
    }
}