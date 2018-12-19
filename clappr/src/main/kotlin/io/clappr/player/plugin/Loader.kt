package io.clappr.player.plugin

import io.clappr.player.base.BaseObject
import io.clappr.player.base.Options
import io.clappr.player.components.Container
import io.clappr.player.components.Core
import io.clappr.player.components.Playback
import io.clappr.player.components.PlaybackEntry
import io.clappr.player.plugin.container.ContainerPlugin
import io.clappr.player.plugin.core.CorePlugin


typealias PluginFactory<C, P> = (C) -> P

typealias CorePluginFactory = PluginFactory<Core, CorePlugin>

typealias ContainerPluginFactory = PluginFactory<Container, ContainerPlugin>

sealed class PluginEntry(val name: String) {
    class Core(name: String, val factory: CorePluginFactory) : PluginEntry(name)

    class Container(name: String, val factory: ContainerPluginFactory) : PluginEntry(name)
}


class Loader(extraPlugins: List<PluginEntry> = emptyList(), extraPlaybacks: List<PlaybackEntry> = emptyList()) {
    companion object {
        @JvmStatic
        private val registeredPlugins = mutableMapOf<String, PluginEntry>()
        @JvmStatic
        private val registeredPlaybacks = mutableListOf<PlaybackEntry>()

        @JvmStatic
        fun clearPlaybacks() {
            registeredPlaybacks.clear()
        }

        @JvmStatic
        fun clearPlugins() {
            registeredPlugins.clear()
        }

        @JvmStatic
        fun registerPlugin(pluginEntry: PluginEntry): Boolean {
            val pluginName = pluginEntry.name
            pluginName?.let {
                if (pluginName.isNotEmpty()) {
                    registeredPlugins[pluginName] = pluginEntry
                    return true
                }
            }
            return false
        }

        @JvmStatic
        fun unregisterPlugin(name: String) =
                name.takeIf { it.isNotEmpty() && registeredPlugins.containsKey(it) }?.let {
                    registeredPlugins.remove(it) != null
                } ?: false

        @JvmStatic
        fun registerPlayback(playbackEntry: PlaybackEntry): Boolean {
            val playbackName = playbackEntry.name
            if (playbackName.isNotEmpty()) {
                registeredPlaybacks.removeAll { it.name == playbackName }
                registeredPlaybacks.add(0, playbackEntry)
                return true
            }
            return false
        }
    }

    private val externalPlugins = mutableListOf<PluginEntry>()

    private val externalPlaybacks = mutableListOf<PlaybackEntry>()

    private val availablePlugins = mutableMapOf<String, PluginEntry>()

    private val availablePlaybacks = mutableListOf<PlaybackEntry>()

    init {
        registeredPlugins.values.forEach { addPlugin(it) }

        externalPlugins.addAll(extraPlugins.filter { it.name.isNotEmpty() })
        externalPlugins.forEach { addPlugin(it) }

        registeredPlaybacks.forEach { addPlayback(it) }

        externalPlaybacks.addAll(extraPlaybacks.filter { it.name.isNotEmpty() })
        externalPlaybacks.forEach { addPlayback(it) }
    }

    fun <C : BaseObject> loadPlugins(context: C): List<Plugin> = availablePlugins.values.mapNotNull { loadPlugin(context, it) }

    fun loadPlayback(source: String, mimeType: String? = null, options: Options): Playback? {
        var playback: Playback? = null

        try {
            val playbackEntry = registeredPlaybacks.first { it.supportsSource(source, mimeType) }
            playback = playbackEntry.factory(source, mimeType, options)
        } catch (e: Exception) {
        }

        return playback
    }

    private fun addPlayback(playbackEntry: PlaybackEntry) {
        val name = playbackEntry.name
        if (!name.isEmpty()) {
            availablePlaybacks.add(playbackEntry)
        }
    }

    private fun addPlugin(pluginEntry: PluginEntry) {
        val name: String = pluginEntry.name
        if (name.isNotEmpty()) {
            availablePlugins[name] = pluginEntry
        }
    }

    private fun <C : BaseObject> loadPlugin(component: C, pluginEntry: PluginEntry): Plugin? = try {
        when (pluginEntry) {
            is PluginEntry.Core -> pluginEntry.factory(component as Core)
            is PluginEntry.Container -> pluginEntry.factory(component as Container)
        }
    } catch (e: Exception) {
        null
    }
}