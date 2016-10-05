package com.globo.clappr.plugin

import android.util.Log
import com.globo.clappr.Player
import com.globo.clappr.base.UIObject
import com.globo.clappr.plugin.Container.ContainerPlugin
import com.globo.clappr.plugin.Container.UIContainerPlugin
import com.globo.clappr.plugin.Core.CorePlugin
import com.globo.clappr.plugin.Core.UICorePlugin
import com.globo.clappr.plugin.Playback.PlaybackPlugin
import kotlin.reflect.KClass
import kotlin.reflect.primaryConstructor

class Loader {
    val defaultPlugins = arrayOf(
            CorePlugin::class,
            UICorePlugin::class,
            ContainerPlugin::class,
            UIContainerPlugin::class,
            PlaybackPlugin::class)

    val externalPlugins : MutableList<KClass<Plugin>> = mutableListOf()

    val loadedPlugins: MutableMap<String, Plugin> = mutableMapOf<String, Plugin>()

    init {
        for (pluginClass in defaultPlugins) {
            loadPlugin(pluginClass)
        }
        for (pluginClass in externalPlugins) {
            loadPlugin(pluginClass)
        }

        System.out.println("Plugin(s) loaded:" + loadedPlugins.size)
        for ((name, plugin) in loadedPlugins) {
            System.out.println("  " + name + " - " + plugin )
        }
    }

    private fun loadPlugin(pluginClass: KClass<out Plugin>) {
        var plugin: Plugin? = null

        var constructor = pluginClass.primaryConstructor
        try {
            plugin = constructor?.call() as? Plugin
        } catch (e: Exception) {
        }

        if (plugin != null) {
            loadedPlugins.put(plugin.name, plugin)
        } else {
            Log.e("Clappr.Loader", "Invalid Plugin: " + pluginClass.simpleName)
        }
    }
}

