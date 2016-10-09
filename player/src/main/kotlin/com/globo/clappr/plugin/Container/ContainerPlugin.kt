package com.globo.clappr.plugin.Container

import com.globo.clappr.base.BaseObject
import com.globo.clappr.components.Container
import com.globo.clappr.plugin.Plugin

open class ContainerPlugin(val container: Container) : Plugin(container) {
    companion object {
        const val name = "containerplugin"
    }
}