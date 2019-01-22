package io.clappr.player.plugin.container

import android.content.Context
import io.clappr.player.base.BaseObject
import io.clappr.player.base.EventInterface
import io.clappr.player.base.NamedType
import io.clappr.player.components.Container
import io.clappr.player.plugin.Plugin

open class ContainerPlugin(
        val container: Container, open val base: BaseObject = BaseObject(),
        override val name: String = Companion.name) : Plugin, EventInterface by base {
    companion object : NamedType {
        override val name: String = "containerplugin"
    }

    val applicationContext: Context
        get() = BaseObject.applicationContext

}