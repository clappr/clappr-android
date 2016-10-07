package com.globo.clappr.plugin.Container

import com.globo.clappr.base.BaseObject
import com.globo.clappr.components.Container
import com.globo.clappr.plugin.UIPlugin

open class UIContainerPlugin() : UIPlugin() {
    override val name = "uicontainerplugin"

    var container : Container? = null
    override fun setup(component: BaseObject) {
        container = component as Container
    }

}