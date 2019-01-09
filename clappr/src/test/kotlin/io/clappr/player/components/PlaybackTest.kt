package io.clappr.player.base

import io.clappr.player.BuildConfig
import io.clappr.player.components.*
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
import org.robolectric.shadows.ShadowApplication
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23))
open class PlaybackTest {


    class SomePlayback(source: String, options: Options = Options(),
                       private val aMediaType: MediaType = MediaType.UNKNOWN) : Playback(source, null, options, name = name, supportsSource = supportsSource) {
        companion object {
            const val name = ""

            const val validSource = "valid-source.mp4"

            val supportsSource: PlaybackSupportCheck = { source, _ -> source == validSource }

            private val entry = PlaybackEntry(
                    name = name,
                    supportsSource = supportsSource,
                    factory = { source, _, options -> SomePlayback(source, options) })
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

        var selectedMediaOptionsJson: String? = null
        val hasSelectedMediaOption = selectedMediaOptionList.isNotEmpty()
    }

    @Before
    fun setup() {
        BaseObject.applicationContext = ShadowApplication.getInstance().applicationContext
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
            it.trigger(Event.READY.value)
        }

        assertFalse("Should not call start at when option is not passed", playback.startAtWasCalled)
    }

    @Test
    fun shouldNotCallStartAtWhenVideoIsLive() {
        val option = Options().also {
            it[ClapprOption.START_AT.value] = 30
        }

        val playback = SomePlayback("valid-source.mp4", option, Playback.MediaType.LIVE).also {
            it.render()
            it.trigger(Event.READY.value)
        }

        assertFalse("Should not call start at for live videos", playback.startAtWasCalled)
    }

    private fun testPlaybackStartAt(startAtValue: Any, shouldAssertStartAtValue: Boolean, mediaType: Playback.MediaType = Playback.MediaType.UNKNOWN) {
        val option = Options().also { it[ClapprOption.START_AT.value] = startAtValue }
        val playback = SomePlayback("valid-source.mp4", option, mediaType)

        playback.render()
        playback.trigger(Event.READY.value)

        assertEquals("startAt should be called when start at is set", shouldAssertStartAtValue, playback.startAtWasCalled)
        if (shouldAssertStartAtValue) {
            assertEquals("startAt value in seconds ", (startAtValue as Number).toInt() , playback.startAtValueInSeconds)
        }
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

    @Test
    fun shouldReturnAllAvailableMediaOptionSubtitle() {
        checkAvailableMedia(MediaOptionType.SUBTITLE)
    }

    @Test
    fun shouldReturnAllAvailableMediaOptionAudio() {
        checkAvailableMedia(MediaOptionType.AUDIO)
    }

    private fun checkAvailableMedia(mediaOptionType: MediaOptionType){
        val playback = SomePlayback("valid-source.mp4", Options())
        val quantity = 10
        val mediaOptionList = insertMedia(playback, mediaOptionType, quantity)

        val addedMediaOptionList = playback.availableMediaOptions(mediaOptionType)
        assertEquals(mediaOptionList.size, addedMediaOptionList.size)
        for (i in 0..quantity-1) {
            assertEquals(mediaOptionList[i], addedMediaOptionList[i])
        }

        assertTrue(playback.hasMediaOptionAvailable)
    }

    private fun insertMedia(playback: Playback, mediaOptionType: MediaOptionType, quantity: Int): MutableList<MediaOption> {
        val mediaOptionList: MutableList<MediaOption> = ArrayList()
        for (i in 1..quantity) {
            val mediaOption = MediaOption("Name $i", mediaOptionType, i, null)
            playback.addAvailableMediaOption(mediaOption)
            mediaOptionList.add(mediaOption)
        }

        return mediaOptionList
    }

    @Test
    fun shouldSetSelectedMediaOptionAudio() {
        testSetSelectedMediaOption(MediaOptionType.AUDIO)
    }

    @Test
    fun shouldSetSelectedMediaOptionSubtitle() {
        testSetSelectedMediaOption(MediaOptionType.SUBTITLE)
    }

    private fun testSetSelectedMediaOption(mediaOptionType: MediaOptionType) {
        val playback = SomePlayback("valid-source.mp4")
        val mediaOptionList = insertMedia(playback, mediaOptionType, 3)

        mediaOptionList.forEach {
            playback.setSelectedMediaOption(it)
            assertEquals(it, playback.selectedMediaOption(mediaOptionType))
        }
    }

    @Test
    fun shouldReturnNoOneSelectedMediaOption(){
        val playback = SomePlayback("valid-source.mp4", Options())

        playback.setSelectedMediaOption(MediaOption("Name", MediaOptionType.SUBTITLE, "name", null))
        val mediaSelectedAudio = playback.selectedMediaOption(MediaOptionType.AUDIO)

        assertNull(mediaSelectedAudio)
    }

    @Test
    fun shouldTriggerMediaOptionsUpdateEvents() {
        val playback = SomePlayback("valid-source.mp4", Options())

        val listenObject = BaseObject()

        var mediaOptionsUpdateCalled = false

        listenObject.listenTo(playback, InternalEvent.MEDIA_OPTIONS_UPDATE.value, Callback.wrap { mediaOptionsUpdateCalled = true })

        playback.setSelectedMediaOption(MediaOption("Name", MediaOptionType.SUBTITLE, "name", null))

        assertTrue("Media_Options_Update was not called", mediaOptionsUpdateCalled)
    }

    @Test
    fun shouldNotSelectedMediaOptionWithEmptySelectedMediaOptions() {
        setupPlaybackWithMediaOptions("").run {
            assertNoSelectedMediaOption(this)
        }
    }

    @Test
    fun shouldNotSelectedMediaOptionWithInvalidSelectedMediaOptions() {
        setupPlaybackWithMediaOptions("error").run {
            assertNoSelectedMediaOption(this)
        }
    }

    @Test
    fun shouldNotSelectedMediaOptionWithInvalidArraySelectedMediaOptions() {
        setupPlaybackWithMediaOptions("{\"invalid_array_name\":[{\"name\":\"por\",\"type\":\"AUDIO\"}]}").run {
            assertNoSelectedMediaOption(this)
        }
    }

    @Test
    fun shouldNotSelectedMediaOptionWithInvalidNameInSelectedMediaOptions() {
        setupPlaybackWithMediaOptions(convertMediaOptionsToJson(MediaOptionType.AUDIO.name, "invalid_name")).run {
            assertNoSelectedMediaOption(this)
        }
    }

    @Test
    fun shouldNotSelectedMediaOptionWithEmptyNameInSelectedMediaOptions() {
        setupPlaybackWithMediaOptions(convertMediaOptionsToJson(MediaOptionType.AUDIO.name, "")).run {
            assertNoSelectedMediaOption(this)
        }
    }

    @Test
    fun shouldNotSelectedMediaOptionWithInvalidTypeInSelectedMediaOptions() {
        setupPlaybackWithMediaOptions(convertMediaOptionsToJson("invalid_type", "por")).run {
            assertNoSelectedMediaOption(this)
        }
    }

    @Test
    fun shouldSelectPortugueseAudioFromSelectedMediaOptions() {
        val jsonMediaOptionName = AudioLanguage.PORTUGUESE.value
        val jsonMediaOptionType = MediaOptionType.AUDIO
        val validJson = convertMediaOptionsToJson(jsonMediaOptionType.name, jsonMediaOptionName)

        setupPlaybackWithMediaOptions(validJson).run {
            assertSelectedMediaOption(this, MediaOptionType.AUDIO, jsonMediaOptionName, validJson)
        }
    }

    @Test
    fun shouldSelectUpperCasePortugueseAudioFromSelectedMediaOptions() {
        val jsonMediaOptionName = AudioLanguage.PORTUGUESE.value
        val jsonMediaOptionType = MediaOptionType.AUDIO
        val validJsonWithUpperCase = convertMediaOptionsToJson(jsonMediaOptionType.name, jsonMediaOptionName.toUpperCase())
        val expectedJson = convertMediaOptionsToJson(jsonMediaOptionType.name, jsonMediaOptionName)

        setupPlaybackWithMediaOptions(validJsonWithUpperCase).run {
            assertSelectedMediaOption(this, jsonMediaOptionType, jsonMediaOptionName, expectedJson)
        }
    }

    @Test
    fun shouldSelectEnglishAudioFromSelectedMediaOptions() {
        val jsonMediaOptionName = AudioLanguage.ENGLISH.value
        val jsonMediaOptionType = MediaOptionType.AUDIO
        val validJson = convertMediaOptionsToJson(jsonMediaOptionType.name, jsonMediaOptionName)

        setupPlaybackWithMediaOptions(validJson).run {
            assertSelectedMediaOption(this, jsonMediaOptionType, jsonMediaOptionName, validJson)
        }
    }

    @Test
    fun shouldSelectOriginalAudioFromSelectedMediaOptions() {
        val jsonMediaOptionName = AudioLanguage.ORIGINAL.value
        val jsonMediaOptionType = MediaOptionType.AUDIO
        val validJson = convertMediaOptionsToJson(jsonMediaOptionType.name, jsonMediaOptionName)

        setupPlaybackWithMediaOptions(validJson).run {
            assertSelectedMediaOption(this, jsonMediaOptionType, jsonMediaOptionName, validJson)
        }
    }

    @Test
    fun shouldSelectSubtitleFromSelectedMediaOptions() {
        val jsonMediaOptionName = SubtitleLanguage.PORTUGUESE.value
        val jsonMediaOptionType = MediaOptionType.SUBTITLE
        val validJson = convertMediaOptionsToJson(jsonMediaOptionType.name, jsonMediaOptionName)

        setupPlaybackWithMediaOptions(validJson).run {
            assertSelectedMediaOption(this, jsonMediaOptionType, jsonMediaOptionName, validJson)
        }
    }

    @Test
    fun shouldSelectUpperCaseSubtitleFromSelectedMediaOptions() {
        val jsonMediaOptionName = SubtitleLanguage.PORTUGUESE.value
        val jsonMediaOptionType = MediaOptionType.SUBTITLE
        val validJsonWithUpperCase = convertMediaOptionsToJson(jsonMediaOptionType.name, jsonMediaOptionName.toUpperCase())
        val expectedJson = convertMediaOptionsToJson(jsonMediaOptionType.name, jsonMediaOptionName)

        setupPlaybackWithMediaOptions(validJsonWithUpperCase).run {
            assertSelectedMediaOption(this, jsonMediaOptionType, jsonMediaOptionName, expectedJson)
        }
    }

    @Test
    fun shouldSelectOffSubtitleFromSelectedMediaOptions() {
        val jsonMediaOptionName = SubtitleLanguage.OFF.value
        val jsonMediaOptionType = MediaOptionType.SUBTITLE
        val validJson = convertMediaOptionsToJson(jsonMediaOptionType.name, jsonMediaOptionName)

        setupPlaybackWithMediaOptions(validJson).run {
            assertSelectedMediaOption(this, jsonMediaOptionType, jsonMediaOptionName, validJson)
        }
    }

    @Test
    fun shouldSelectAudioAndSubtitleFromSelectedMediaOptions() {
        val jsonMediaOptionNameAudio = AudioLanguage.PORTUGUESE.value
        val jsonMediaOptionTypeAudio = MediaOptionType.AUDIO
        val jsonMediaOptionNameSubtitle = SubtitleLanguage.PORTUGUESE.value
        val jsonMediaOptionTypeSubtitle = MediaOptionType.SUBTITLE
        val validJson = convertMediaOptionsToJson(jsonMediaOptionTypeAudio.name, jsonMediaOptionNameAudio,
                jsonMediaOptionTypeSubtitle.name, jsonMediaOptionNameSubtitle)

        setupPlaybackWithMediaOptions(validJson).run {
            assertSelectedMediaOption(this, jsonMediaOptionTypeAudio, jsonMediaOptionNameAudio, validJson)
            assertSelectedMediaOption(this, jsonMediaOptionTypeSubtitle, jsonMediaOptionNameSubtitle, validJson)
        }
    }

    private fun setupPlaybackWithMediaOptions(mediaOptionJson: String): SomePlayback {
        val options = Options(options = hashMapOf(ClapprOption.SELECTED_MEDIA_OPTIONS.value to mediaOptionJson))
        val playback = SomePlayback("valid-source.mp4", options)

        playback.on(Event.MEDIA_OPTIONS_SELECTED.value, Callback.wrap {
            playback.selectedMediaOptionsJson = it?.getString(EventData.MEDIA_OPTIONS_SELECTED_RESPONSE.value) ?: ""
        })

        playback.addAvailableMediaOption(MediaOption(AudioLanguage.ORIGINAL.value, MediaOptionType.AUDIO, null, null))
        playback.addAvailableMediaOption(MediaOption(AudioLanguage.PORTUGUESE.value, MediaOptionType.AUDIO, null, null))
        playback.addAvailableMediaOption(MediaOption(AudioLanguage.ENGLISH.value, MediaOptionType.AUDIO, null, null))

        playback.addAvailableMediaOption(SUBTITLE_OFF)
        playback.addAvailableMediaOption(MediaOption(SubtitleLanguage.PORTUGUESE.value, MediaOptionType.SUBTITLE, null, null))

        playback.setupInitialMediasFromClapprOptions()

        return playback
    }

    private fun assertSelectedMediaOption(playback: SomePlayback, expectedType: MediaOptionType, expectedValue: String,
                                          expectedJson: String) {
        val optionSelected = playback.selectedMediaOption(expectedType)
        assertEquals(expectedValue, optionSelected?.name)
        assertEquals(expectedType.name, optionSelected?.type?.name)
        assertEquals(expectedJson, playback.selectedMediaOptionsJson)
    }

    private fun assertNoSelectedMediaOption(playback: SomePlayback) {
        assertFalse(playback.hasSelectedMediaOption)
        assertNull(playback.selectedMediaOption(MediaOptionType.AUDIO))
        assertNull(playback.selectedMediaOption(MediaOptionType.SUBTITLE))
        assertNull(playback.selectedMediaOptionsJson)
    }

    private fun convertMediaOptionsToJson(jsonMediaOptionTypeAudio:String? = null, jsonMediaOptionNameAudio: String? = null,
                                          jsonMediaOptionTypeSubtitle: String? = null, jsonMediaOptionNameSubtitle: String? = null): String {
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
        playback.on(InternalEvent.DID_UPDATE_OPTIONS.value, Callback.wrap { callbackWasCalled = true })

        playback.options = Options(source = "new_source")

        assertTrue("should trigger DID_UPDATE_OPTIONS on set options", callbackWasCalled)
    }

    @Test
    fun shouldNotSeekToLivePositionByDefault(){
        val playback = SomePlayback("valid-source.mp4", Options())
        assertFalse(playback.seekToLivePosition())
    }
}
