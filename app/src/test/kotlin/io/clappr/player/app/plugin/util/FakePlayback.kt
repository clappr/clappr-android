package io.clappr.player.app.plugin.util

import io.clappr.player.base.Options
import io.clappr.player.components.Playback
import io.clappr.player.components.PlaybackSupportInterface


internal class FakePlayback(source: String, mimeType: String? = null, options: Options = Options()) : Playback(source, mimeType, options) {
    companion object : PlaybackSupportInterface {
        override val name: String = "fakePlayback"
        override fun supportsSource(source: String, mimeType: String?) = true
    }
}