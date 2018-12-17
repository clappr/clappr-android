package io.clappr.player.plugin.container

import io.clappr.player.base.NamedType
import io.clappr.player.base.UIObject
import io.clappr.player.components.Container
import io.clappr.player.plugin.UIPlugin

open class UIContainerPlugin(container: Container, override val base: UIObject = UIObject()) : UIPlugin, ContainerPlugin(container, base) {
    companion object: NamedType {
        override val name = "uicontainerplugin"
    }

    override var visibility = UIPlugin.Visibility.HIDDEN

    override val uiObject: UIObject
        get() = base
}