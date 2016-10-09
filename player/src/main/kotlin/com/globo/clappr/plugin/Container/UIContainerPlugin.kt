package com.globo.clappr.plugin.Container

import com.globo.clappr.base.BaseObject
import com.globo.clappr.components.Container
import com.globo.clappr.plugin.UIPlugin

open class UIContainerPlugin(val container: Container) : UIPlugin(container) {
    companion object {
        const val name = "uicontainerplugin"
    }
}