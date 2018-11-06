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
                       private val aMediaType: MediaType = MediaType.UNKNOWN) : Playback(source, null, options) {
        companion object : PlaybackSupportInterface {
            val validSource = "valid-source.mp4"
            override val name = ""

            @JvmStatic
            override fun supportsSource(source: String, mimeType: String?): Boolean {
                return source == validSource
            }
        }

        var playWasCalled = false
        var seekWasCalled = false
        var seekValueInSeconds: Int = 0

        override val mediaType: MediaType
            get() = aMediaType

        override fun play(): Boolean {
            playWasCalled = true
            return super.play()
        }

        override fun seek(seconds: Int): Boolean {
            seekWasCalled = true
            seekValueInSeconds = seconds
            return super.seek(seconds)
        }

        val hasSelectedMediaOption = selectedMediaOptionList.isNotEmpty()
    }

    @Before
    fun setup() {
        BaseObject.context = ShadowApplication.getInstance().applicationContext
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
    fun shouldCallSeekWhenOptionsHaveIntValueStartAt() {
        testPlaybackSeek(80, shouldAssertSeekValue = true)
    }

    @Test
    fun shouldCallSeekWhenOptionsHaveFloatValueStartAt() {
        testPlaybackSeek(70.0f, shouldAssertSeekValue = true)
    }

    @Test
    fun shouldCallSeekWhenOptionsHaveDoubleValueStartAt() {
        testPlaybackSeek(70.0, shouldAssertSeekValue = true)
    }

    @Test
    fun shouldCallSeekWhenOptionsHaveNotANumberValueStartAt() {
        testPlaybackSeek("fail", shouldAssertSeekValue = false)
    }

    @Test
    fun shouldNotCallStartAtWhenVideoIsLive(){
        val option = Options().also {
            it[ClapprOption.START_AT.value] = 30
        }

        val playback = SomePlayback("valid-source.mp4", option, Playback.MediaType.LIVE).also {
            it.render()
        }

        assertFalse("Should not call start at for live videos", playback.seekWasCalled)
    }

    private fun testPlaybackSeek(startAtValue: Any, shouldAssertSeekValue: Boolean, mediaType: Playback.MediaType = Playback.MediaType.UNKNOWN) {
        val option = Options()
        option.put(ClapprOption.START_AT.value, startAtValue)

        var willSeekBeCalled = false

        val playback = SomePlayback("valid-source.mp4", option, mediaType)
        playback.render()
        playback.once(Event.READY.value, Callback.wrap {
            willSeekBeCalled = shouldAssertSeekValue
        }, playback)

        playback.trigger(Event.READY.value)

        assertEquals("seek should be called when start at is set", willSeekBeCalled, playback.seekWasCalled)
        if (shouldAssertSeekValue) {
            assertEquals("seek value in seconds ", (startAtValue as Number).toInt() , playback.seekValueInSeconds)
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
    fun shouldResetAvailableMediaOptions() {
        val playback = SomePlayback("valid-source.mp4", Options())
        val random = Random()
        val randomNumber = random.nextInt(10)
        insertMedia(playback, MediaOptionType.SUBTITLE, randomNumber)
        insertMedia(playback, MediaOptionType.AUDIO, random.nextInt(10))
        playback.resetAvailableMediaOptions()

        assertTrue(playback.availableMediaOptions(MediaOptionType.SUBTITLE).isEmpty())
        assertTrue(playback.availableMediaOptions(MediaOptionType.AUDIO).isEmpty())
        assertFalse(playback.hasMediaOptionAvailable)
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
            assertSelectedMediaOption(this, MediaOptionType.AUDIO, jsonMediaOptionName)
        }
    }

    @Test
    fun shouldSelectUpperCasePortugueseAudioFromSelectedMediaOptions() {
        val jsonMediaOptionName = AudioLanguage.PORTUGUESE.value
        val jsonMediaOptionType = MediaOptionType.AUDIO
        val validJson = convertMediaOptionsToJson(jsonMediaOptionType.name, jsonMediaOptionName.toUpperCase())

        setupPlaybackWithMediaOptions(validJson).run {
            assertSelectedMediaOption(this, jsonMediaOptionType, jsonMediaOptionName)
        }
    }

    @Test
    fun shouldSelectEnglishAudioFromSelectedMediaOptions() {
        val jsonMediaOptionName = AudioLanguage.ENGLISH.value
        val jsonMediaOptionType = MediaOptionType.AUDIO
        val validJson = convertMediaOptionsToJson(jsonMediaOptionType.name, jsonMediaOptionName)

        setupPlaybackWithMediaOptions(validJson).run {
            assertSelectedMediaOption(this, jsonMediaOptionType, jsonMediaOptionName)
        }
    }

    @Test
    fun shouldSelectOriginalAudioFromSelectedMediaOptions() {
        val jsonMediaOptionName = AudioLanguage.ORIGINAL.value
        val jsonMediaOptionType = MediaOptionType.AUDIO
        val validJson = convertMediaOptionsToJson(jsonMediaOptionType.name, jsonMediaOptionName)

        setupPlaybackWithMediaOptions(validJson).run {
            assertSelectedMediaOption(this, jsonMediaOptionType, jsonMediaOptionName)
        }
    }

    @Test
    fun shouldSelectSubtitleFromSelectedMediaOptions() {
        val jsonMediaOptionName = SubtitleLanguage.PORTUGUESE.value
        val jsonMediaOptionType = MediaOptionType.SUBTITLE
        val validJson = convertMediaOptionsToJson(jsonMediaOptionType.name, jsonMediaOptionName)

        setupPlaybackWithMediaOptions(validJson).run {
            assertSelectedMediaOption(this, jsonMediaOptionType, jsonMediaOptionName)
        }
    }

    @Test
    fun shouldSelectUpperCaseSubtitleFromSelectedMediaOptions() {
        val jsonMediaOptionName = SubtitleLanguage.PORTUGUESE.value
        val jsonMediaOptionType = MediaOptionType.SUBTITLE
        val validJson = convertMediaOptionsToJson(jsonMediaOptionType.name, jsonMediaOptionName.toUpperCase())

        setupPlaybackWithMediaOptions(validJson).run {
            assertSelectedMediaOption(this, jsonMediaOptionType, jsonMediaOptionName)
        }
    }

    @Test
    fun shouldSelectOffSubtitleFromSelectedMediaOptions() {
        val jsonMediaOptionName = SubtitleLanguage.OFF.value
        val jsonMediaOptionType = MediaOptionType.SUBTITLE
        val validJson = convertMediaOptionsToJson(jsonMediaOptionType.name, jsonMediaOptionName)

        setupPlaybackWithMediaOptions(validJson).run {
            assertSelectedMediaOption(this, jsonMediaOptionType, jsonMediaOptionName)
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
            assertSelectedMediaOption(this, jsonMediaOptionTypeAudio, jsonMediaOptionNameAudio)
            assertSelectedMediaOption(this, jsonMediaOptionTypeSubtitle, jsonMediaOptionNameSubtitle)
        }
    }

    private fun setupPlaybackWithMediaOptions(mediaOptionJson: String): SomePlayback {
        val options = Options(options = hashMapOf(ClapprOption.SELECTED_MEDIA_OPTIONS.value to mediaOptionJson))
        val playback = SomePlayback("valid-source.mp4", options)

        playback.addAvailableMediaOption(MediaOption(AudioLanguage.ORIGINAL.value, MediaOptionType.AUDIO, null, null))
        playback.addAvailableMediaOption(MediaOption(AudioLanguage.PORTUGUESE.value, MediaOptionType.AUDIO, null, null))
        playback.addAvailableMediaOption(MediaOption(AudioLanguage.ENGLISH.value, MediaOptionType.AUDIO, null, null))

        playback.addAvailableMediaOption(SUBTITLE_OFF)
        playback.addAvailableMediaOption(MediaOption(SubtitleLanguage.PORTUGUESE.value, MediaOptionType.SUBTITLE, null, null))

        playback.setupInitialMediasFromClapprOptions()

        return playback
    }

    private fun assertSelectedMediaOption(playback: Playback, expectedType: MediaOptionType, expectedValue: String) {
        val optionSelected = playback.selectedMediaOption(expectedType)
        assertEquals(expectedValue, optionSelected?.name)
        assertEquals(expectedType.name, optionSelected?.type?.name)
    }

    private fun assertNoSelectedMediaOption(playback: SomePlayback) {
        assertFalse(playback.hasSelectedMediaOption)
        assertNull(playback.selectedMediaOption(MediaOptionType.AUDIO))
        assertNull(playback.selectedMediaOption(MediaOptionType.SUBTITLE))
        assertEquals(convertMediaOptionsToJson(), playback.convertSelectedMediaOptionsToJson())
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
