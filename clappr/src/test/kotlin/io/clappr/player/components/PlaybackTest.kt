package io.clappr.player.components

import android.annotation.SuppressLint
import androidx.test.core.app.ApplicationProvider
import io.clappr.player.base.BaseObject
import io.clappr.player.base.ClapprOption.*
import io.clappr.player.base.Event.*
import io.clappr.player.base.InternalEvent.*
import io.clappr.player.base.Options
import io.clappr.player.components.AudioLanguage.*
import io.clappr.player.components.MediaOptionType.AUDIO
import io.clappr.player.components.MediaOptionType.SUBTITLE
import io.clappr.player.playback.NoOpPlayback
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
open class PlaybackTest {

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

        val hasSelectedMediaOption = selectedMediaOptions.isNotEmpty()
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
        val playback = SomePlayback("valid-source.mp4", Options())
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
        val playback = SomePlayback("valid-source.mp4", Options())

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
        val playback = SomePlayback("valid-source.mp4", Options())

        var willDestroyCalled = false
        var didDestroyCalled = false
        listenObject.listenTo(playback, WILL_DESTROY.value) { willDestroyCalled = true }
        listenObject.listenTo(playback, DID_DESTROY.value) { didDestroyCalled = true }

        playback.destroy()

        assertTrue("Will destroy not triggered", willDestroyCalled)
        assertTrue("Did destroy not triggered", didDestroyCalled)
    }

    @Test
    fun shouldReturnAllAvailableMediaOptionSubtitle() {
        checkAvailableMedia(SUBTITLE)
    }

    @Test
    fun shouldReturnAllAvailableMediaOptionAudio() {
        checkAvailableMedia(AUDIO)
    }

    private fun checkAvailableMedia(mediaOptionType: MediaOptionType) {
        val playback = SomePlayback("valid-source.mp4", Options())
        val quantity = 10
        val mediaOptionList = insertMedia(playback, mediaOptionType, quantity)

        val addedMediaOptionList = playback.availableMediaOptions(mediaOptionType)
        assertEquals(mediaOptionList.size, addedMediaOptionList.size)
        for (i in 0 until quantity) {
            assertEquals(mediaOptionList[i], addedMediaOptionList[i])
        }

        assertTrue(playback.hasAnyMediaOptionAvailable)
    }

    private fun insertMedia(
        playback: Playback, mediaOptionType: MediaOptionType, quantity: Int
    ): MutableList<MediaOption> {
        val mediaOptionList: MutableList<MediaOption> = ArrayList()
        for (i in 1..quantity) {
            val mediaOption = MediaOption("Name $i", mediaOptionType)
            playback.addAvailableMediaOption(mediaOption)
            mediaOptionList.add(mediaOption)
        }

        return mediaOptionList
    }

    @Test
    fun shouldSetSelectedMediaOptionAudio() {
        val playback = SomePlayback("valid-source.mp4")
        val mediaOptions = insertMedia(playback, AUDIO, 3)

        var mediaOptionsUpdateTriggered = false
        var didSelectAudioTriggered = false

        playback.on(MEDIA_OPTIONS_UPDATE.value) { mediaOptionsUpdateTriggered = true }
        playback.on(DID_SELECT_AUDIO.value) { didSelectAudioTriggered = true }

        mediaOptions.forEach {
            playback.setSelectedMediaOption(it)

            assertEquals(it, playback.selectedMediaOption(AUDIO))
            assertTrue(mediaOptionsUpdateTriggered)
            assertTrue(didSelectAudioTriggered)

            mediaOptionsUpdateTriggered = false
            didSelectAudioTriggered = false
        }
    }

    @Test
    fun shouldSetSelectedMediaOptionSubtitle() {
        val playback = SomePlayback("valid-source.mp4")
        val mediaOptions = insertMedia(playback, SUBTITLE, 3)

        var mediaOptionsUpdateTriggered = false
        var didSelectSubtitleTriggered = false

        playback.on(MEDIA_OPTIONS_UPDATE.value) { mediaOptionsUpdateTriggered = true }
        playback.on(DID_SELECT_SUBTITLE.value) { didSelectSubtitleTriggered = true }

        mediaOptions.forEach {
            playback.setSelectedMediaOption(it)

            assertEquals(it, playback.selectedMediaOption(SUBTITLE))
            assertTrue(mediaOptionsUpdateTriggered)
            assertTrue(didSelectSubtitleTriggered)

            mediaOptionsUpdateTriggered = false
            didSelectSubtitleTriggered = false
        }
    }

    @Test
    fun shouldReturnNoOneSelectedMediaOption() {
        val playback = SomePlayback("valid-source.mp4", Options())

        playback.setSelectedMediaOption(MediaOption("Name", SUBTITLE))
        val mediaSelectedAudio = playback.selectedMediaOption(AUDIO)

        assertNull(mediaSelectedAudio)
    }

    @Test
    fun shouldTriggerMediaOptionsUpdateEvents() {
        val playback = SomePlayback("valid-source.mp4", Options())

        val listenObject = BaseObject()

        var mediaOptionsUpdateCalled = false

        listenObject.listenTo(playback, MEDIA_OPTIONS_UPDATE.value) {
            mediaOptionsUpdateCalled = true
        }

        playback.setSelectedMediaOption(MediaOption("Name", SUBTITLE))

        assertTrue("Media_Options_Update was not called", mediaOptionsUpdateCalled)
    }

    @Test
    fun shouldNotSelectedMediaOptionWithEmptySelectedMediaOptions() {
        val mediaOptionJson = ""
        val options = Options(options = hashMapOf(SELECTED_MEDIA_OPTIONS.value to mediaOptionJson))

        setupPlaybackWithMediaOptions(options).run {
            assertNoSelectedMediaOption(this)
        }
    }

    @Test
    fun shouldNotSelectedMediaOptionWithInvalidSelectedMediaOptions() {
        val mediaOptionJson = "error"
        val options = Options(options = hashMapOf(SELECTED_MEDIA_OPTIONS.value to mediaOptionJson))

        setupPlaybackWithMediaOptions(options).run {
            assertNoSelectedMediaOption(this)
        }
    }

    @Test
    fun shouldNotSelectedMediaOptionWithInvalidArraySelectedMediaOptions() {
        val mediaOptionJson = """{"invalid_array_name":[{"name":"por","type":"AUDIO"}]}"""
        val options = Options(options = hashMapOf(SELECTED_MEDIA_OPTIONS.value to mediaOptionJson))

        setupPlaybackWithMediaOptions(options).run {
            assertNoSelectedMediaOption(this)
        }
    }

    @Test
    fun shouldNotSelectedMediaOptionWithInvalidNameInSelectedMediaOptions() {
        val mediaOptionJson = convertMediaOptionsToJson(AUDIO.name, "invalid_name")
        val options = Options(options = hashMapOf(SELECTED_MEDIA_OPTIONS.value to mediaOptionJson))

        setupPlaybackWithMediaOptions(options).run {
            assertNoSelectedMediaOption(this)
        }
    }

    @Test
    fun shouldNotSelectedMediaOptionWithEmptyNameInSelectedMediaOptions() {
        val mediaOptionJson = convertMediaOptionsToJson(AUDIO.name, "")
        val options = Options(options = hashMapOf(SELECTED_MEDIA_OPTIONS.value to mediaOptionJson))

        setupPlaybackWithMediaOptions(options).run {
            assertNoSelectedMediaOption(this)
        }
    }

    @Test
    fun shouldNotSelectedMediaOptionWithInvalidTypeInSelectedMediaOptions() {
        val mediaOptionJson = convertMediaOptionsToJson("invalid_type", "por")
        val options = Options(options = hashMapOf(SELECTED_MEDIA_OPTIONS.value to mediaOptionJson))

        setupPlaybackWithMediaOptions(options).run {
            assertNoSelectedMediaOption(this)
        }
    }

    @Test
    fun shouldSelectUpperCasePortugueseAudioFromSelectedMediaOptions() {

        val jsonMediaOptionName = PORTUGUESE.value
        val jsonMediaOptionType = AUDIO
        val validJsonWithUpperCase =
            convertMediaOptionsToJson(jsonMediaOptionType.name, jsonMediaOptionName.toUpperCase())

        val options =
            Options(options = hashMapOf(SELECTED_MEDIA_OPTIONS.value to validJsonWithUpperCase))

        setupPlaybackWithMediaOptions(options).run {
            assertSelectedMediaOption(this, jsonMediaOptionType, jsonMediaOptionName)
        }
    }

    @Test
    fun shouldSelectUpperCaseSubtitleFromSelectedMediaOptions() {
        val jsonMediaOptionName = SubtitleLanguage.PORTUGUESE.value
        val jsonMediaOptionType = SUBTITLE
        val validJsonWithUpperCase =
            convertMediaOptionsToJson(jsonMediaOptionType.name, jsonMediaOptionName.toUpperCase())
        val options =
            Options(options = hashMapOf(SELECTED_MEDIA_OPTIONS.value to validJsonWithUpperCase))

        setupPlaybackWithMediaOptions(options).run {
            assertSelectedMediaOption(this, jsonMediaOptionType, jsonMediaOptionName)
        }
    }

    @Test
    fun `shouldPrioritizeDefaultAudioWhenHasBothDefaultAudioAndSelectedMediaOptions`() {
        val jsonMediaOptionName = AudioLanguage.ORIGINAL.value
        val jsonMediaOptionType = AUDIO
        val validJson = convertMediaOptionsToJson(jsonMediaOptionType.name, jsonMediaOptionName)

        val options = Options(
            options = hashMapOf(
                SELECTED_MEDIA_OPTIONS.value to validJson,
                DEFAULT_AUDIO.value to "por"
            )
        )

        setupPlaybackWithMediaOptions(options).run {
            assertSelectedMediaOption(this, jsonMediaOptionType, "por")
        }
    }

    @Test
    fun `shouldPrioritizeDefaultSubtitleWhenHasBothDefaultSubtitleAndSelectedMediaOptions`() {
        val jsonMediaOptionName = AudioLanguage.PORTUGUESE.value
        val jsonMediaOptionType = SUBTITLE
        val validJson = convertMediaOptionsToJson(jsonMediaOptionType.name, jsonMediaOptionName)

        val options = Options(
            options = hashMapOf(
                SELECTED_MEDIA_OPTIONS.value to validJson,
                DEFAULT_SUBTITLE.value to "off"
            )
        )

        setupPlaybackWithMediaOptions(options).run {
            assertSelectedMediaOption(this, jsonMediaOptionType, "off")
        }
    }

    @Test
    fun shouldSelectAudioAndSubtitleFromSelectedMediaOptions() {
        val jsonMediaOptionNameAudio = PORTUGUESE.value
        val jsonMediaOptionTypeAudio = AUDIO
        val jsonMediaOptionNameSubtitle = SubtitleLanguage.PORTUGUESE.value
        val jsonMediaOptionTypeSubtitle = SUBTITLE
        val validJson = convertMediaOptionsToJson(
            jsonMediaOptionTypeAudio.name, jsonMediaOptionNameAudio,
            jsonMediaOptionTypeSubtitle.name, jsonMediaOptionNameSubtitle
        )

        val options = Options(options = hashMapOf(SELECTED_MEDIA_OPTIONS.value to validJson))

        setupPlaybackWithMediaOptions(options).run {
            assertSelectedMediaOption(this, jsonMediaOptionTypeAudio, jsonMediaOptionNameAudio)
            assertSelectedMediaOption(
                this,
                jsonMediaOptionTypeSubtitle,
                jsonMediaOptionNameSubtitle
            )
        }
    }

    private fun assertSelectedMediaOption(
        playback: SomePlayback, expectedType: MediaOptionType, expectedValue: String
    ) {
        val optionSelected = playback.selectedMediaOption(expectedType)
        assertEquals(expectedValue, optionSelected?.name)
        assertEquals(expectedType.name, optionSelected?.type?.name)
    }

    private fun assertNoSelectedMediaOption(playback: SomePlayback) {
        assertFalse(playback.hasSelectedMediaOption)
        assertNull(playback.selectedMediaOption(AUDIO))
        assertNull(playback.selectedMediaOption(SUBTITLE))
    }

    private fun convertMediaOptionsToJson(
        jsonMediaOptionTypeAudio: String? = null, jsonMediaOptionNameAudio: String? = null,
        jsonMediaOptionTypeSubtitle: String? = null, jsonMediaOptionNameSubtitle: String? = null
    ): String {
        val mediaOptionsArrayJson = "media_option"
        val mediaOptionsNameJson = "name"
        val mediaOptionsTypeJson = "type"

        val result = JSONObject()
        val jsonArray = JSONArray()

        jsonMediaOptionTypeAudio?.let {
            val jsonObject = JSONObject()
            jsonObject.put(mediaOptionsNameJson, jsonMediaOptionNameAudio)
            jsonObject.put(mediaOptionsTypeJson, jsonMediaOptionTypeAudio)
            jsonArray.put(jsonObject)
        }

        jsonMediaOptionTypeSubtitle?.let {
            val jsonObject = JSONObject()
            jsonObject.put(mediaOptionsNameJson, jsonMediaOptionNameSubtitle)
            jsonObject.put(mediaOptionsTypeJson, jsonMediaOptionTypeSubtitle)
            jsonArray.put(jsonObject)
        }

        result.put(mediaOptionsArrayJson, jsonArray)
        return result.toString()
    }

    @Test
    fun shouldTriggerUpdateOptionOnSetOptions() {
        val playback = SomePlayback("valid-source.mp4", Options())

        var callbackWasCalled = false
        playback.on(DID_UPDATE_OPTIONS.value) { callbackWasCalled = true }

        playback.options = Options(source = "new_source")

        assertTrue("should trigger DID_UPDATE_OPTIONS on set options", callbackWasCalled)
    }

    @Test
    fun shouldNotSeekToLivePositionByDefault() {
        val playback = SomePlayback("valid-source.mp4", Options())
        assertFalse(playback.seekToLivePosition())
    }

    @Test
    fun shouldNotSentMediaOptionSelectedWhenOptionIsUpdated() {
        var didSelectedMediaOption = false
        val playback = SomePlayback("valid-source.mp4", Options())
        val option = MediaOption(ORIGINAL.value, AUDIO)

        playback.on(MEDIA_OPTIONS_SELECTED.value) {
            didSelectedMediaOption = true
        }

        playback.setSelectedMediaOption(option)

        assertFalse(didSelectedMediaOption)
    }

    private fun setupPlaybackWithMediaOptions(options: Options): SomePlayback {
        val playback = SomePlayback("valid-source.mp4", options)

        with(playback) {
            addAvailableMediaOption(MediaOption(ORIGINAL.value, AUDIO))
            addAvailableMediaOption(MediaOption(PORTUGUESE.value, AUDIO))
            addAvailableMediaOption(MediaOption(ENGLISH.value, AUDIO))
            addAvailableMediaOption(SUBTITLE_OFF)
            addAvailableMediaOption(MediaOption(PORTUGUESE.value, SUBTITLE))

            setupInitialMediasFromClapprOptions()
        }

        return playback
    }

    @Test
    fun shouldTriggerEventAndChangeTrackWhenAudioIsSet() {
        val playback = SomePlayback("valid-source.mp4", Options())

        playback.availableAudios += listOf("eng", "por")

        var didSelectAudioWasTriggered = false
        playback.on(DID_SELECT_AUDIO.value) { didSelectAudioWasTriggered = true }

        playback.selectedAudio = "eng"

        assertTrue(didSelectAudioWasTriggered)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldNotTriggerEventAndChangeTrackWhenUnavailableAudioIsSet() {
        val playback = SomePlayback("valid-source.mp4", Options())

        playback.availableAudios += listOf("eng", "por")

        playback.selectedAudio = "fr"
    }

    @Test
    fun shouldTriggerEventAndChangeTrackWhenSubtitleIsSet() {
        val playback = SomePlayback("valid-source.mp4", Options())

        playback.availableSubtitles += listOf("eng", "por")

        var didSelectSubtitleWasTriggered = false
        playback.on(DID_SELECT_SUBTITLE.value) { didSelectSubtitleWasTriggered = true }

        playback.selectedSubtitle = "eng"

        assertTrue(didSelectSubtitleWasTriggered)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldNotTriggerEventAndChangeTrackWhenUnavailableSubtitleIsSet() {
        val playback = SomePlayback("valid-source.mp4", Options())

        playback.availableSubtitles += listOf("eng", "por")

        playback.selectedSubtitle = "fr"
    }
}
