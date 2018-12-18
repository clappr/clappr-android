package io.clappr.player.playback

import io.clappr.player.base.Options
import io.clappr.player.components.Playback
import io.clappr.player.components.PlaybackSupportInterface

class NoOpPlayback(source: String, mimeType: String? = null, options: Options = Options()) : Playback(source, mimeType, options) {
    companion object: PlaybackSupportInterface {
        override val name: String = "no_op"

        override fun supportsSource(source: String, mimeType: String?): Boolean {
            return true
        }
    }

    override fun supportsSource(source: String, mimeType: String?): Boolean = true
}