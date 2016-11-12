package com.globo.clappr.plugin.container

import com.globo.clappr.base.NamedType
import com.globo.clappr.components.Container
import com.globo.clappr.plugin.UIPlugin

open class UIContainerPlugin(val container: Container) : UIPlugin(container) {
    companion object: NamedType {
        override val name = "uicontainerplugin"
    }
}