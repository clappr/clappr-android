package com.globo.clappr.plugin

import android.util.Log
import com.globo.clappr.components.Container
import com.globo.clappr.components.Core
import com.globo.clappr.components.Playback
import com.globo.clappr.plugin.Container.ContainerPlugin
import com.globo.clappr.plugin.Container.UIContainerPlugin
import com.globo.clappr.plugin.Core.CorePlugin
import com.globo.clappr.plugin.Core.UICorePlugin
import com.globo.clappr.plugin.Playback.PlaybackPlugin
import kotlin.reflect.KClass
import kotlin.reflect.primaryConstructor

class Loader(extraPlugins: List<KClass<Plugin>> = listOf<KClass<Plugin>>()) {
    val defaultPlugins = arrayOf(
            CorePlugin::class,
            UICorePlugin::class,
            ContainerPlugin::class,
            UIContainerPlugin::class,
            PlaybackPlugin::class)

    val externalPlugins = mutableListOf<KClass<Plugin>>()

    val loadedPlugins = mutableMapOf<String, Plugin>()

    init {
        for (pluginClass in defaultPlugins) {
            loadPlugin(pluginClass)
        }

        externalPlugins.addAll(extraPlugins)
        for (pluginClass in externalPlugins) {
            loadPlugin(pluginClass)
        }
    }

    fun setupCorePlugins(core: Core) : List<Plugin> {
        val corePlugins = mutableListOf<Plugin>()
        for ((name, plugin) in loadedPlugins) {
            if ( (plugin is CorePlugin) || (plugin is UICorePlugin) ) {
                (plugin as? CorePlugin)?.core = core
                (plugin as? UICorePlugin)?.core = core
                corePlugins.add(plugin)
            }
        }
        return corePlugins.toList()
    }

    fun setupContainerPlugins(container: Container) : List<Plugin> {
        val containerPlugins = mutableListOf<Plugin>()
        for ((name, plugin) in loadedPlugins) {
            if ( (plugin is ContainerPlugin) || (plugin is UIContainerPlugin) ) {
                (plugin as? ContainerPlugin)?.container = container
                (plugin as? UIContainerPlugin)?.container = container
                containerPlugins.add(plugin)
            }
        }
        return containerPlugins.toList()
    }


    fun setupPlaybackPlugins(playback: Playback) : List<Plugin> {
        val playbackPlugins = mutableListOf<Plugin>()
        for ((name, plugin) in loadedPlugins) {
            val playbackPlugin = plugin as? PlaybackPlugin
            playbackPlugin?.let {
                playbackPlugin.playback = playback
                playbackPlugins.add(plugin)
            }
        }
        return playbackPlugins.toList()
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

