package io.clappr.player.plugin

import io.clappr.player.plugin.control.*

object PluginConfig {
    fun register() {
        Loader.registerPlugin(PluginEntry.Container(name = PosterPlugin.name, factory = { context -> PosterPlugin(context) }))
        Loader.registerPlugin(PluginEntry.Container(name = LoadingPlugin.name, factory = { context -> LoadingPlugin(context) }))
        Loader.registerPlugin(PluginEntry.Core(name = MediaControl.name, factory = { context -> MediaControl(context) }))
        Loader.registerPlugin(PluginEntry.Core(name = FullscreenButton.name, factory = { context -> FullscreenButton(context) }))
        Loader.registerPlugin(PluginEntry.Core(name = TimeIndicatorPlugin.name, factory = { context -> TimeIndicatorPlugin(context) }))
        Loader.registerPlugin(PluginEntry.Core(name = PlayButton.name, factory = { context -> PlayButton(context) }))
        Loader.registerPlugin(PluginEntry.Core(name = SeekbarPlugin.name, factory = { context -> SeekbarPlugin(context) }))
    }
}