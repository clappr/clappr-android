package io.clappr.player.plugin

import io.clappr.player.base.BaseObject
import io.clappr.player.base.Options
import io.clappr.player.components.Container
import io.clappr.player.components.Core
import io.clappr.player.components.Playback
import io.clappr.player.components.PlaybackEntry
import io.clappr.player.plugin.container.ContainerPlugin
import io.clappr.player.plugin.core.CorePlugin


typealias PluginFactory<Context, Plugin> = (Context) -> Plugin

typealias CorePluginFactory = PluginFactory<Core, CorePlugin>

typealias ContainerPluginFactory = PluginFactory<Container, ContainerPlugin>

sealed class PluginEntry(val name: String, val activeInChromelessMode: Boolean) {
    open class Core(name: String, activeInChromelessMode: Boolean = true, val factory: CorePluginFactory) : PluginEntry(name, activeInChromelessMode)

    class Container(name: String, activeInChromelessMode: Boolean = true, val factory: ContainerPluginFactory) : PluginEntry(name, activeInChromelessMode)
}

object Loader {
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
    fun register(pluginEntry: PluginEntry) = pluginEntry.takeUnless { it.name.isEmpty() }?.let {
        registeredPlugins[it.name] = it
        true
    } ?: false

    @JvmStatic
    fun register(playbackEntry: PlaybackEntry) = playbackEntry.takeUnless { it.name.isEmpty() }?.let { entry ->
        registeredPlaybacks.removeAll { it.name == entry.name }
        registeredPlaybacks.add(0, entry)
        true
    } ?: false

    @JvmStatic
    fun unregisterPlugin(name: String) =
            name.takeIf { it.isNotEmpty() && registeredPlugins.containsKey(it) }?.let {
                registeredPlugins.remove(it) != null
            } ?: false

    @JvmStatic
    fun unregisterPlayback(name: String) =
            registeredPlaybacks.run {
                val entry = find { it.name == name }
                if (entry != null) remove(entry) else false
            }

    fun <Context : BaseObject> loadPlugins(
            context: Context, externalPlugins: List<PluginEntry> = emptyList(), isChromelessMode: Boolean = false): List<Plugin> =
            mergeExternalPlugins(externalPlugins).values
                .filter { !isChromelessMode || it.activeInChromelessMode }
                .mapNotNull { loadPlugin(context, it) }

    private fun mergeExternalPlugins(plugins: List<PluginEntry>): Map<String, PluginEntry> =
            plugins.filter { it.name.isNotEmpty() }.fold(HashMap(registeredPlugins)) { resultingMap, entry ->
                resultingMap[entry.name] = entry
                resultingMap
            }

    fun loadPlayback(source: String, mimeType: String? = null, options: Options): Playback? = try {
        val playbackEntry = registeredPlaybacks.first { it.supportsSource(source, mimeType) }
        playbackEntry.factory(source, mimeType, options)
    } catch (e: Exception) {
        null
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