package io.clappr.player.plugin


import io.clappr.player.components.PlaybackEntry
import io.clappr.player.playback.ExoPlayerPlayback
import io.clappr.player.playback.NoOpPlayback

object PlaybackConfig {
    fun register() {
        Loader.registerPlayback(PlaybackEntry(
                name = NoOpPlayback.name,
                supportsSource = { source, mimeType -> NoOpPlayback.supportsSource(source, mimeType) },
                factory = { source, mimeType, options -> NoOpPlayback(source, mimeType, options) }))
        Loader.registerPlayback(PlaybackEntry(
                name = ExoPlayerPlayback.name,
                supportsSource = { source, mimeType -> ExoPlayerPlayback.supportsSource(source, mimeType) },
                factory = { source, mimeType, options -> ExoPlayerPlayback(source, mimeType, options) }))
    }
}
