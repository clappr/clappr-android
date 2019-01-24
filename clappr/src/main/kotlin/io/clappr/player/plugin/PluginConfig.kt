package io.clappr.player.plugin

import io.clappr.player.plugin.control.*

object PluginConfig {
    fun register() {
        Loader.register(PosterPlugin.entry)
        Loader.register(LoadingPlugin.entry)
        Loader.register(MediaControl.entry)
        Loader.register(FullscreenButton.entry)
        Loader.register(TimeIndicatorPlugin.entry)
        Loader.register(PlayButton.entry)
        Loader.register(SeekbarPlugin.entry)
        Loader.register(VoiceControlPlugin.entry)
    }
}