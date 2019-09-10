package io.clappr.player.components

import android.annotation.SuppressLint
import androidx.test.core.app.ApplicationProvider
import io.clappr.player.base.BaseObject
import io.clappr.player.base.ClapprOption.*
import io.clappr.player.base.Event.*
import io.clappr.player.base.InternalEvent.*
import io.clappr.player.base.Options
import io.clappr.player.playback.NoOpPlayback
import org.junit.Assert.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class PlaybackTest {

    class SomePlayback(
        source: String, options: Options = Options(),
        private val aMediaType: MediaType = MediaType.UNKNOWN
    ) : Playback(source, null, options, name = name, supportsSource = supportsSource) {
        companion object {
            const val name = ""

            private const val validSource = "valid-source.mp4"

            val supportsSource: PlaybackSupportCheck = { source, _ -> source == validSource }
        }

        var playWasCalled = false
        var startAtWasCalled = false
        var startAtValueInSeconds: Int = 0

        override val mediaType: MediaType
            get() = aMediaType

        override fun play(): Boolean {
            playWasCalled = true
            return super.play()
        }

        override fun startAt(seconds: Int): Boolean {
            startAtWasCalled = true
            startAtValueInSeconds = seconds
            return super.startAt(seconds)
        }
    }

    @Before
    fun setup() {
        BaseObject.applicationContext = ApplicationProvider.getApplicationContext()
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldThrowWhenInstantiatingWithInvalidSource() {
        object : Playback("some-invalid-source.mp4") {}
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
        val playback = SomePlayback("valid-source.mp4")
        playback.render()
        assertTrue("play should be called when autoplay is on", playback.playWasCalled)
    }

    @Test
    fun shouldCallStartAtWhenOptionsHaveIntValueStartAt() {
        testPlaybackStartAt(80, shouldAssertStartAtValue = true)
    }

    @Test
    fun shouldCallStartAtWhenOptionsHaveFloatValueStartAt() {
        testPlaybackStartAt(70.0f, shouldAssertStartAtValue = true)
    }

    @Test
    fun shouldCallStartAtWhenOptionsHaveDoubleValueStartAt() {
        testPlaybackStartAt(70.0, shouldAssertStartAtValue = true)
    }

    @Test
    fun shouldCallStartAtWhenOptionsHaveNotANumberValueStartAt() {
        testPlaybackStartAt("fail", shouldAssertStartAtValue = false)
    }

    @Test
    fun shouldNotStartAtWhenVideoDoesNotHaveValueStartAt() {
        val playback = SomePlayback("valid-source.mp4").also {
            it.render()
            it.trigger(READY.value)
        }

        assertFalse("Should not call start at when option is not passed", playback.startAtWasCalled)
    }

    @Test
    fun shouldNotCallStartAtWhenVideoIsLive() {
        val option = Options().also {
            it[START_AT.value] = 30
        }

        val playback = SomePlayback("valid-source.mp4", option, Playback.MediaType.LIVE).also {
            it.render()
            it.trigger(READY.value)
        }

        assertFalse("Should not call start at for live videos", playback.startAtWasCalled)
    }

    private fun testPlaybackStartAt(
        startAtValue: Any,
        shouldAssertStartAtValue: Boolean,
        mediaType: Playback.MediaType = Playback.MediaType.UNKNOWN
    ) {
        val option = Options().also { it[START_AT.value] = startAtValue }
        val playback = SomePlayback("valid-source.mp4", option, mediaType)

        playback.render()
        playback.trigger(READY.value)

        assertEquals(
            "startAt should be called when start at is set",
            shouldAssertStartAtValue,
            playback.startAtWasCalled
        )
        if (shouldAssertStartAtValue) {
            assertEquals(
                "startAt value in seconds ",
                (startAtValue as Number).toInt(),
                playback.startAtValueInSeconds
            )
        }
    }

    @Test
    fun shouldStopListeningOnDestroy() {
        val triggerObject = BaseObject()
        val playback = SomePlayback("valid-source.mp4")

        var numberOfTriggers = 0
        playback.listenTo(triggerObject, "playbackTest") { numberOfTriggers++ }

        triggerObject.trigger("playbackTest")
        assertEquals("no trigger", 1, numberOfTriggers)

        playback.destroy()
        triggerObject.trigger("playbackTest")
        assertEquals("trigger", 1, numberOfTriggers)
    }

    @Test
    @Ignore
    @SuppressLint("IgnoreWithoutReason")
    fun shouldTriggerEventsOnDestroy() {
        val listenObject = BaseObject()
        val playback = SomePlayback("valid-source.mp4")

        var willDestroyCalled = false
        var didDestroyCalled = false
        listenObject.listenTo(playback, WILL_DESTROY.value) { willDestroyCalled = true }
        listenObject.listenTo(playback, DID_DESTROY.value) { didDestroyCalled = true }

        playback.destroy()

        assertTrue("Will destroy not triggered", willDestroyCalled)
        assertTrue("Did destroy not triggered", didDestroyCalled)
    }

    @Test
    fun shouldSetSelectedMediaOptionAudio() {
        val playback = SomePlayback("valid-source.mp4")

        val languages = (1..3).map { "Name $it" }
        playback.availableAudios += languages

        var didUpdateAudioTriggered = false

        playback.on(DID_UPDATE_AUDIO.value) { didUpdateAudioTriggered = true }

        languages.forEach {
            playback.selectedAudio = it

            assertEquals(it, playback.selectedAudio)
            assertTrue(didUpdateAudioTriggered)

            didUpdateAudioTriggered = false
        }
    }

    @Test
    fun shouldSetSelectedMediaOptionSubtitle() {
        val playback = SomePlayback("valid-source.mp4")

        val languages = (1..3).map { "Name $it" }
        playback.availableSubtitles += languages

        var didUpdateSubtitleTriggered = false

        playback.on(DID_UPDATE_SUBTITLE.value) { didUpdateSubtitleTriggered = true }

        languages.forEach {
            playback.selectedSubtitle = it

            assertEquals(it, playback.selectedSubtitle)
            assertTrue(didUpdateSubtitleTriggered)

            didUpdateSubtitleTriggered = false
        }
    }

    @Test
    fun shouldReturnNoOneSelectedMediaOption() {
        val playback = SomePlayback("valid-source.mp4")

        playback.availableSubtitles += "Name"

        playback.selectedSubtitle = "Name"

        val selectedAudio = playback.selectedAudio

        assertEquals(AudioLanguage.UNSET.value, selectedAudio)
    }

    @Test
    fun shouldTriggerUpdateOptionOnSetOptions() {
        val playback = SomePlayback("valid-source.mp4")

        var callbackWasCalled = false
        playback.on(DID_UPDATE_OPTIONS.value) { callbackWasCalled = true }

        playback.options = Options(source = "new_source")

        assertTrue("should trigger DID_UPDATE_OPTIONS on set options", callbackWasCalled)
    }

    @Test
    fun shouldNotSeekToLivePositionByDefault() {
        val playback = SomePlayback("valid-source.mp4")
        assertFalse(playback.seekToLivePosition())
    }

    @Test
    fun shouldNotSentDidSelectAudioWhenAudioIsUpdated() {
        var didSelectAudio = false
        val playback = SomePlayback("valid-source.mp4")
        val language = AudioLanguage.ORIGINAL.value

        playback.on(DID_SELECT_AUDIO.value) {
            didSelectAudio = true
        }

        playback.availableAudios += language

        playback.selectedAudio = language

        assertFalse(didSelectAudio)
    }

    @Test
    fun shouldTriggerEventAndChangeTrackWhenAudioIsSet() {
        val playback = SomePlayback("valid-source.mp4")

        playback.availableAudios += setOf("eng", "por")

        var didUpdateAudioWasTriggered = false
        playback.on(DID_UPDATE_AUDIO.value) { didUpdateAudioWasTriggered = true }

        playback.selectedAudio = "eng"

        assertTrue(didUpdateAudioWasTriggered)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldNotTriggerEventAndChangeTrackWhenUnavailableAudioIsSet() {
        val playback = SomePlayback("valid-source.mp4")

        playback.availableAudios += setOf("eng", "por")

        playback.selectedAudio = "fr"
    }

    @Test
    fun shouldTriggerEventAndChangeTrackWhenSubtitleIsSet() {
        val playback = SomePlayback("valid-source.mp4")

        playback.availableSubtitles += setOf("eng", "por")

        var didUpdateSubtitleWasTriggered = false
        playback.on(DID_UPDATE_SUBTITLE.value) { didUpdateSubtitleWasTriggered = true }

        playback.selectedSubtitle = "eng"

        assertTrue(didUpdateSubtitleWasTriggered)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldNotTriggerEventAndChangeTrackWhenUnavailableSubtitleIsSet() {
        val playback = SomePlayback("valid-source.mp4")

        playback.availableSubtitles += setOf("eng", "por")

        playback.selectedSubtitle = "fr"
    }

    @Test
    fun shouldStartSetWithUnset() {
        val playback = SomePlayback("valid-source.mp4")

        assertEquals(emptySet<String>(), playback.availableSubtitles)
    }
}
