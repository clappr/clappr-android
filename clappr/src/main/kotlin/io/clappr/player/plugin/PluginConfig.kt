package io.clappr.player.plugin

object PluginConfig {
    fun register() {
        Loader.registerPlugin(PosterPlugin::class)
        Loader.registerPlugin(LoadingPlugin::class)
        Loader.registerPlugin(MediaControl::class)
    }
}