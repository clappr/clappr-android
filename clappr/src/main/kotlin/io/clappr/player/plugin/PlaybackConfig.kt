package io.clappr.player.plugin


import io.clappr.player.playback.ExoPlayerPlayback
import io.clappr.player.playback.NoOpPlayback

object PlaybackConfig {
    fun register() {
        Loader.registerPlayback(NoOpPlayback.entry)
        Loader.registerPlayback(ExoPlayerPlayback.entry)
    }
}
