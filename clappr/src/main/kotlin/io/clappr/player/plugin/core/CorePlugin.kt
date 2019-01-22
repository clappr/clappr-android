package io.clappr.player.plugin.core

import android.content.Context
import io.clappr.player.base.BaseObject
import io.clappr.player.base.EventInterface
import io.clappr.player.base.NamedType
import io.clappr.player.components.Core
import io.clappr.player.plugin.Plugin

open class CorePlugin(
        val core: Core, open val base: BaseObject = BaseObject(), override val name: String = Companion.name) :
        Plugin, EventInterface by base {
    companion object : NamedType {
        override val name: String = "coreplugin"
    }

    val applicationContext: Context
        get() = BaseObject.applicationContext
}