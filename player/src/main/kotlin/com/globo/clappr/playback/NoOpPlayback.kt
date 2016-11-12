package com.globo.clappr.playback

import com.globo.clappr.base.Options
import com.globo.clappr.components.Playback
import com.globo.clappr.components.PlaybackSupportInterface

open class NoOpPlayback(source: String, mimeType: String?, options: Options) : Playback(source, mimeType, options) {
    companion object: PlaybackSupportInterface {
        override fun supportsSource(source: String, mimeType: String?): Boolean {
            return true
        }

        override val name: String?
            get() = "no_op"
    }
}