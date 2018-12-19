package io.clappr.player.app.plugin.util

import io.clappr.player.base.Options
import io.clappr.player.components.Playback
import io.clappr.player.components.PlaybackSupportCheck


internal class FakePlayback(source: String, mimeType: String? = null, options: Options = Options()) : Playback(source, mimeType, options, name, supportsSource) {
    companion object {
        const val name: String = "fakePlayback"
        val supportsSource: PlaybackSupportCheck = { _, _ -> true }
    }
}