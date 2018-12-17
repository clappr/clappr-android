package io.clappr.player.plugin.core

import android.content.Context
import io.clappr.player.base.BaseObject
import io.clappr.player.base.EventInterface
import io.clappr.player.base.NamedType
import io.clappr.player.components.Core
import io.clappr.player.plugin.Plugin

open class CorePlugin (val core: Core, open val base: BaseObject = BaseObject()) : Plugin, EventInterface by base {
    companion object: NamedType {
        override val name = "coreplugin"
    }

    val applicationContext: Context
        get() = BaseObject.applicationContext
}