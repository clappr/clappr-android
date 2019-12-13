package io.clappr.player.playback

import androidx.test.core.app.ApplicationProvider
import io.clappr.player.base.BaseObject
import io.clappr.player.base.Options
import io.clappr.player.components.Playback
import io.clappr.player.playback.ExoPlayerPlayback.Companion.supportsSource
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class VideoResolutionChangeListenerTest {

    @Before
    fun setUp() {
        BaseObject.applicationContext = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun `should trigger VIDEO_RESOLUTION_CHANGED on playback`() {
        val playback = DummyPlayback()
        val listener = VideoResolutionChangeListener(playback)

        var width = 0
        var height = 0
        playback.on("didUpdateVideoResolution") {
            width = it?.getInt("width") ?: 0
            height = it?.getInt("height") ?: 0
        }

        listener.onVideoSizeChanged(null, 1280, 720, 0, 0.0f)

        assertEquals(1280, width)
        assertEquals(720, height)
    }

    class DummyPlayback : Playback("some-source", null, Options(), "ExoPlayerVideoSizeListenerTestPlayback", supportsSource)
}