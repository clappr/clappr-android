package com.globo.clappr.plugin

import com.globo.clappr.base.BaseObject
import com.globo.clappr.base.NamedType
import com.globo.clappr.components.Playback
import kotlin.reflect.*

class Loader(extraPlugins: List<KClass<out Plugin>> = emptyList()) {
    companion object {
        @JvmStatic val registeredPlugins = mutableMapOf<String, KClass<out Plugin>>()
        @JvmStatic val registeredPlaybacks= mutableListOf<KClass<out Playback>>()

        @JvmStatic
        fun registerPlugin(pluginClass: KClass<out Plugin>): Boolean {
            var pluginName = (pluginClass.companionObjectInstance as? NamedType)?.name
            pluginName?.let {
                if (pluginName.isNotEmpty()) {
                    registeredPlugins.put(pluginName, pluginClass)
                    return true
                }
            }
            return false
        }

    }

    val externalPlugins = mutableListOf<KClass<out Plugin>>()

    val availablePlugins = mutableMapOf<String, KClass<out Plugin>>()

    init {
        for (pluginClass in registeredPlugins.values) {
            addPlugin(pluginClass)
        }

        externalPlugins.addAll(extraPlugins.filter { !(it.companionObjectInstance as? NamedType)?.name.isNullOrEmpty() })
        for (pluginClass in externalPlugins) {
            addPlugin(pluginClass)
        }
    }

    fun loadPlugins(context: BaseObject) : List<Plugin> {
        val loadedPlugins = mutableListOf<Plugin>()
        availablePlugins.values.forEach {
            val plugin = loadPlugin(context, it)
            plugin?.let { loadedPlugins.add(plugin) }
        }
        return loadedPlugins.toList()
    }

    private fun addPlugin(pluginClass: KClass<out Plugin>) {
        var name : String? = (pluginClass.companionObjectInstance as? NamedType)?.name
        name?.let {
            if (name.isNotEmpty()) {
                availablePlugins.put(name, pluginClass)
            }
        }
    }

    private fun loadPlugin(component: BaseObject, pluginClass: KClass<out Plugin>) : Plugin? {
        var plugin: Plugin? = null

        var constructor = pluginClass.primaryConstructor
        try {
            plugin = constructor?.call(component) as? Plugin
        } catch (e: Exception) {
        }

        return plugin
    }
}

