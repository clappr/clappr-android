package io.clappr.player.plugin


import io.clappr.player.playback.ExoPlayerPlayback
import io.clappr.player.playback.NoOpPlayback

object PlaybackConfig {
    fun register() {
        Loader.register(NoOpPlayback.entry)
        Loader.register(ExoPlayerPlayback.entry)
    }
}
