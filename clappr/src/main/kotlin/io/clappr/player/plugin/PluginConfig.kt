package io.clappr.player.plugin

import io.clappr.player.plugin.control.SeekbarPlugin
import io.clappr.player.plugin.control.MediaControl
import io.clappr.player.plugin.control.TimeIndicatorPlugin
import io.clappr.player.plugin.control.FullscreenButton
import io.clappr.player.plugin.control.PlayButton

object PluginConfig {
    fun register() {
        // TODO - Add default plugins
        Loader.registerPlugin(PosterPlugin::class)
        Loader.registerPlugin(LoadingPlugin::class)
        Loader.registerPlugin(MediaControl::class)
        Loader.registerPlugin(FullscreenButton::class)
        Loader.registerPlugin(TimeIndicatorPlugin::class)
        Loader.registerPlugin(PlayButton::class)
        Loader.registerPlugin(SeekbarPlugin::class)
    }
}