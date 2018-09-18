package io.clappr.player.plugin

object PluginConfig {
    fun register() {
        // TODO - Add default plugins
        Loader.registerPlugin(PosterPlugin::class)
        Loader.registerPlugin(LoadingPlugin::class)
        Loader.registerPlugin(MediaControl::class)
    }
}