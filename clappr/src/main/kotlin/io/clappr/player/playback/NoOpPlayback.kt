package io.clappr.player.playback

import io.clappr.player.base.Options
import io.clappr.player.components.Playback
import io.clappr.player.components.PlaybackEntry
import io.clappr.player.components.PlaybackSupportCheck

class NoOpPlayback(source: String, mimeType: String? = null, options: Options = Options()) : Playback(source, mimeType, options, name = name, supportsSource = supportsSource) {
    companion object {
        const val name = "no_op"
        val supportsSource: PlaybackSupportCheck = { _, _ -> true }
        val entry = PlaybackEntry(
                name = name,
                supportsSource = supportsSource,
                factory = { source, mimeType, options -> NoOpPlayback(source, mimeType, options) })
    }
}