package io.clappr.player.plugin

import io.clappr.player.plugin.control.*

object PluginConfig {
    fun register() {
        Loader.registerPlugin(PosterPlugin.entry)
        Loader.registerPlugin(LoadingPlugin.entry)
        Loader.registerPlugin(MediaControl.entry)
        Loader.registerPlugin(FullscreenButton.entry)
        Loader.registerPlugin(TimeIndicatorPlugin.entry)
        Loader.registerPlugin(PlayButton.entry)
        Loader.registerPlugin(SeekbarPlugin.entry)
    }
}