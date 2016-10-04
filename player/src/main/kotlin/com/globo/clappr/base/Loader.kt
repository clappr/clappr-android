package com.globo.clappr.base

import com.globo.clappr.plugin.Plugin
import kotlin.reflect.KClass

class Loader {
    val corePlugins : MutableSet<KClass<Plugin>> = mutableSetOf(Plugin::class)
}