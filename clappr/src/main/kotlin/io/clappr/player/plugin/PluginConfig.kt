package io.clappr.player.plugin

import io.clappr.player.plugin.Control.MediaControl

object PluginConfig {
    fun register() {
        // TODO - Add default plugins
        Loader.registerPlugin(PosterPlugin::class)
        Loader.registerPlugin(LoadingPlugin::class)
        Loader.registerPlugin(MediaControl::class)
    }
}