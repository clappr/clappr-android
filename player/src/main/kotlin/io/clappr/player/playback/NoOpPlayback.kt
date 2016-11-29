package io.clappr.player.playback

import com.globo.clappr.base.Options
import com.globo.clappr.components.Playback
import com.globo.clappr.components.PlaybackSupportInterface

open class NoOpPlayback(source: String, mimeType: String? = null, options: Options = Options()) : Playback(source, mimeType, options) {
    companion object: PlaybackSupportInterface {
        override val name: String = "no_op"

        override fun supportsSource(source: String, mimeType: String?): Boolean {
            return true
        }
    }
}