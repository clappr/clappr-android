package io.clappr.player.base

import io.clappr.player.BuildConfig
import io.clappr.player.components.Playback
import io.clappr.player.components.PlaybackSupportInterface
import io.clappr.player.playback.NoOpPlayback
import org.junit.Assert.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowApplication
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23))
open class PlaybackTest {

    class SomePlayback(source: String, options: Options = Options()): Playback(source, null, options) {
        companion object: PlaybackSupportInterface {
            val validSource = "valid-source.mp4"
            override val name = ""

            @JvmStatic
            override fun supportsSource(source: String, mimeType: String?): Boolean {
                return source == validSource
            }
        }

        var playWasCalled = false

        override fun play(): Boolean {
            playWasCalled = true
            return super.play()
        }
    }

    @Before
    fun setup() {
      BaseObject.context = ShadowApplication.getInstance().applicationContext
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldThrowWhenInstantiatingWithInvalidSource() {
        val playback = object: Playback("some-invalid-source.mp4") {}
    }

    @Test
    fun loadCallShouldReturnFalseForUnsupportedSource() {
        val playback = SomePlayback("valid-source.mp4")
        assertFalse("load call should return false for an unsupported source", playback.load(""))
    }

    @Test
    fun loadCallShouldReturnTrueForSupportedSource() {
        val playback = NoOpPlayback("supported-source.mp4")
        assertTrue("load call should return true for a supported source", playback.load(""))
    }

    @Test
    fun shouldCallPlayWhenOptionsHaveAutoplayOn() {
        val playback = SomePlayback("valid-source.mp4", Options(autoPlay = true))
        playback.render()
        assertTrue("play should be called when autoplay is on", playback.playWasCalled)
    }

    @Test
    fun shouldNotCallPlayWhenOptionsHaveAutoplayOff() {
        val playback = SomePlayback("valid-source.mp4", Options(autoPlay = false))
        playback.render()
        assertFalse("play should not be called when autoplay is off", playback.playWasCalled)
    }

    @Test
    fun shouldStopListeningOnDestroy() {
        val triggerObject = BaseObject()
        val playback = SomePlayback("valid-source.mp4", Options())

        var numberOfTriggers = 0
        playback.listenTo(triggerObject, "playbackTest", Callback.wrap { numberOfTriggers++ })

        triggerObject.trigger("playbackTest")
        assertEquals("no trigger", 1, numberOfTriggers)

        playback.destroy()
        triggerObject.trigger("playbackTest")
        assertEquals("trigger", 1, numberOfTriggers)
    }

    @Test @Ignore
    fun shouldTriggerEventsOnDestroy() {
        val listenObject = BaseObject()
        val playback = SomePlayback("valid-source.mp4", Options())

        var willDestroyCalled = false
        var didDestroyCalled = false
        listenObject.listenTo(playback, InternalEvent.WILL_DESTROY.value, Callback.wrap { willDestroyCalled = true })
        listenObject.listenTo(playback, InternalEvent.DID_DESTROY.value, Callback.wrap { didDestroyCalled = true })

        playback.destroy()

        assertTrue("Will destroy not triggered", willDestroyCalled)
        assertTrue("Did destroy not triggered", didDestroyCalled)
    }
}
