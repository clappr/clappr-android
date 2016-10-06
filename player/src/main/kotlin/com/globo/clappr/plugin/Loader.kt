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

    fun setupCorePlugins(core: Core): List<Plugin> {
        return setupPlugins(core, getCorePlugins())
    }

    fun setupContainerPlugins(container: Container) : List<Plugin> {
        return setupPlugins(container, getContainerPlugins())
    }

    fun setupPlaybackPlugins(playback: Playback) : List<Plugin> {
        return setupPlugins(playback, getPlaybackPlugins())
    }

    private fun setupPlugins(context: BaseObject, plugins: List<Plugin>) : List<Plugin> {
        plugins.forEach { it.setup(context) }
        return plugins
    }

    fun getCorePlugins() : List<Plugin> {
        return loadedPlugins.values.filter { (it is CorePlugin) || (it is UICorePlugin) }
    }

    fun getContainerPlugins() : List<Plugin> {
        return loadedPlugins.values.filter { (it is ContainerPlugin) || (it is UIContainerPlugin) }
    }

    fun getPlaybackPlugins() : List<Plugin> {
        return loadedPlugins.values.filter { it is PlaybackPlugin }
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

