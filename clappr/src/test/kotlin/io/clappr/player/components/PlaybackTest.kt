package io.clappr.player.components

import android.annotation.SuppressLint
import androidx.test.core.app.ApplicationProvider
import io.clappr.player.base.BaseObject
import io.clappr.player.base.ClapprOption
import io.clappr.player.base.ClapprOption.START_AT
import io.clappr.player.base.Event.*
import io.clappr.player.base.EventData
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
    fun shouldTriggerDidUpdateAudioAndMediaOptionsUpdateWhenAudioIsChanged() {
        val playback = SomePlayback("valid-source.mp4")

        val languages = (1..3).map { "Name $it" }
        playback.availableAudios += languages

        var updatedAudio: String? = null
        var mediaOptionsUpdateTriggered = false

        playback.on(DID_UPDATE_AUDIO.value) {
            updatedAudio = it?.getString(EventData.UPDATED_AUDIO.value)
        }
        playback.on(MEDIA_OPTIONS_UPDATE.value) { mediaOptionsUpdateTriggered = true }

        languages.forEach {
            playback.selectedAudio = it

            assertEquals(it, updatedAudio)
            assertTrue(mediaOptionsUpdateTriggered)

            updatedAudio = null
            mediaOptionsUpdateTriggered = false
        }
    }

    @Test
    fun shouldTriggerDidUpdateSubtitleAndMediaOptionsUpdateWhenSubtitleIsChanged() {
        val playback = SomePlayback("valid-source.mp4")

        val languages = (1..3).map { "Name $it" }
        playback.availableSubtitles += languages

        var updatedSubtitle: String? = null
        var mediaOptionsUpdateTriggered = false

        playback.on(DID_UPDATE_SUBTITLE.value) {
            updatedSubtitle = it?.getString(EventData.UPDATED_SUBTITLE.value)
        }
        playback.on(MEDIA_OPTIONS_UPDATE.value) { mediaOptionsUpdateTriggered = true }

        languages.forEach {
            playback.selectedSubtitle = it

            assertEquals(it, updatedSubtitle)
            assertTrue(mediaOptionsUpdateTriggered)

            updatedSubtitle = null
            mediaOptionsUpdateTriggered = false
        }
    }

    @Test
    fun shouldReturnNoOneSelectedMediaOption() {
        val playback = SomePlayback("valid-source.mp4")

        playback.availableSubtitles += "Name"

        playback.selectedSubtitle = "Name"

        val selectedAudio = playback.selectedAudio

        assertEquals(null, selectedAudio)
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

    @Test(expected = IllegalArgumentException::class)
    fun shouldNotTriggerEventAndChangeTrackWhenUnavailableAudioIsSet() {
        val playback = SomePlayback("valid-source.mp4")

        playback.availableAudios += setOf(
            AudioLanguage.ENGLISH.value,
            AudioLanguage.PORTUGUESE.value
        )

        playback.selectedAudio = "fr"
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldNotTriggerEventAndChangeTrackWhenUnavailableSubtitleIsSet() {
        val playback = SomePlayback("valid-source.mp4")
        playback.selectedSubtitle = "fr"
    }

    @Test
    fun shouldStartSetWithUnset() {
        val playback = SomePlayback("valid-source.mp4")

        assertEquals(emptySet<String>(), playback.availableSubtitles)
    }

    @Test
    fun shouldUpdateSelectedAudioWhenDefaultAudioIsPassed() {
        val options = Options(
            options = hashMapOf(
                ClapprOption.DEFAULT_AUDIO.value to AudioLanguage.PORTUGUESE.value
            )
        )

        val playback = SomePlayback("valid-source.mp4", options)

        playback.availableAudios += setOf(
            AudioLanguage.ENGLISH.value,
            AudioLanguage.PORTUGUESE.value
        )

        playback.setupInitialMediasFromClapprOptions()

        assertEquals(AudioLanguage.PORTUGUESE.value, playback.selectedAudio)
    }

    @Test
    fun shouldUpdateSelectedSubtitleWhenDefaultSubtitleIsPassed() {
        val options = Options(
            options = hashMapOf(
                ClapprOption.DEFAULT_SUBTITLE.value to SubtitleLanguage.PORTUGUESE.value
            )
        )

        val playback = SomePlayback("valid-source.mp4", options)

        playback.availableSubtitles += setOf(
            SubtitleLanguage.OFF.value,
            SubtitleLanguage.PORTUGUESE.value
        )

        playback.setupInitialMediasFromClapprOptions()

        assertEquals(SubtitleLanguage.PORTUGUESE.value, playback.selectedSubtitle)
    }

    @Test
    fun shouldUpdateSelectedAudioAndSelectedSubtitleWhenSelectedMediaOptionsIsPassed() {
        val options = Options(
            options = hashMapOf(
                ClapprOption.SELECTED_MEDIA_OPTIONS.value to """{"media_option":[{"name":"por","type":"SUBTITLE"},{"name":"por","type":"AUDIO"}]}}"""
            )
        )

        val playback = SomePlayback("valid-source.mp4", options)

        playback.availableAudios += setOf(
            AudioLanguage.ENGLISH.value,
            AudioLanguage.PORTUGUESE.value
        )

        playback.availableSubtitles += setOf(
            SubtitleLanguage.OFF.value,
            SubtitleLanguage.PORTUGUESE.value
        )

        playback.setupInitialMediasFromClapprOptions()

        assertEquals(AudioLanguage.PORTUGUESE.value, playback.selectedAudio)
        assertEquals(SubtitleLanguage.PORTUGUESE.value, playback.selectedSubtitle)
    }

    @Test
    fun shouldPreferDefaultSubtitleWhenBothOptionsArePassed() {
        val options = Options(
            options = hashMapOf(
                ClapprOption.DEFAULT_SUBTITLE.value to SubtitleLanguage.PORTUGUESE.value,
                ClapprOption.SELECTED_MEDIA_OPTIONS.value to """{"media_option":[{"name":"eng","type":"SUBTITLE"}]}}"""
            )
        )

        val playback = SomePlayback("valid-source.mp4", options)

        playback.availableSubtitles += setOf(
            SubtitleLanguage.OFF.value,
            SubtitleLanguage.PORTUGUESE.value,
            "eng"
        )

        playback.setupInitialMediasFromClapprOptions()

        assertEquals(SubtitleLanguage.PORTUGUESE.value, playback.selectedSubtitle)
    }

    @Test
    fun shouldPreferDefaultAudioWhenBothOptionsArePassed() {
        val options = Options(
            options = hashMapOf(
                ClapprOption.DEFAULT_AUDIO.value to AudioLanguage.PORTUGUESE.value,
                ClapprOption.SELECTED_MEDIA_OPTIONS.value to """{"media_option":[{"name":"eng","type":"AUDIO"}]}}"""
            )
        )

        val playback = SomePlayback("valid-source.mp4", options)

        playback.availableAudios += setOf(
            AudioLanguage.ORIGINAL.value,
            AudioLanguage.PORTUGUESE.value,
            "eng"
        )

        playback.setupInitialMediasFromClapprOptions()

        assertEquals(AudioLanguage.PORTUGUESE.value, playback.selectedAudio)
    }
}
