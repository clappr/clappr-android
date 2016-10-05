package com.globo.clappr.plugin.Container

import com.globo.clappr.components.Container
import com.globo.clappr.plugin.UIPlugin

open class UIContainerPlugin() : UIPlugin() {
    override val name = "uicontainerplugin"

    var container : Container? = null
}