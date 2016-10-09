package com.globo.clappr.plugin

import android.util.Log
import com.globo.clappr.base.BaseObject
import com.globo.clappr.components.Container
import com.globo.clappr.components.Core
import com.globo.clappr.components.Playback
import com.globo.clappr.plugin.Container.ContainerPlugin
import com.globo.clappr.plugin.Container.UIContainerPlugin
import com.globo.clappr.plugin.Core.CorePlugin
import com.globo.clappr.plugin.Core.UICorePlugin
import com.globo.clappr.plugin.Playback.PlaybackPlugin
import kotlin.reflect.*
import kotlin.reflect.jvm.javaField

class Loader(extraPlugins: List<KClass<out Plugin>> = listOf<KClass<out Plugin>>()) {
    val defaultPlugins = arrayOf(
            CorePlugin::class,
            UICorePlugin::class,
            ContainerPlugin::class,
            UIContainerPlugin::class,
            PlaybackPlugin::class)

    val externalPlugins = mutableListOf<KClass<out Plugin>>()

    val availablePlugins = mutableMapOf<String, KClass<out Plugin>>()

    init {
        for (pluginClass in defaultPlugins) {
            addPlugin(pluginClass)
        }

        externalPlugins.addAll(extraPlugins)
        for (pluginClass in externalPlugins) {
            addPlugin(pluginClass)
        }
    }

    fun loadPlugins(context: BaseObject) : List<Plugin> {
        val loadedPlugins : MutableList<Plugin> = mutableListOf<Plugin>()
        availablePlugins.values.forEach {
            val plugin = loadPlugin(context, it)
            if (plugin != null) {
                loadedPlugins.add(plugin)
            }
        }
        return loadedPlugins.toList()
    }

    private fun addPlugin(pluginClass: KClass<out Plugin>) {
        var name : String? = null
        val companion = pluginClass.companionObject
        companion?.let {
            for (property in companion.memberProperties) {
                if (property.name == "name") {
                    val field = property.javaField
                    field?.let {
                        name = field.get(null) as? String
                    }
                }
            }
        }
        if (!name.isNullOrEmpty()) {
            availablePlugins.put(name!!, pluginClass)
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

