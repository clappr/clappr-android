package io.clappr.player.plugin.container

import io.clappr.player.base.NamedType
import io.clappr.player.components.Container
import io.clappr.player.plugin.UIPlugin

open class UIContainerPlugin(val container: Container) : UIPlugin(container) {
    companion object: NamedType {
        override val name = "uicontainerplugin"
    }
}