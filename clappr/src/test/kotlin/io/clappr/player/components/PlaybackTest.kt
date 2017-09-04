package io.clappr.player.base

import io.clappr.player.BuildConfig
import io.clappr.player.components.*
import io.clappr.player.playback.NoOpPlayback
import org.junit.Assert.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowApplication
import org.robolectric.annotation.Config
import java.util.*
import kotlin.collections.HashMap

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23))
open class PlaybackTest {


    class SomePlayback(source: String, options: Options = Options()) : Playback(source, null, options) {
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

        override fun play(): Boolean {
            playWasCalled = true
            return super.play()
        }

        override fun seek(seconds: Int): Boolean {
            seekWasCalled = true
            return super.seek(seconds)
        }
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
        var option = Options()
        option.put(ClapprOption.START_AT.value, 80)
        val playback = SomePlayback("valid-source.mp4", option)
        playback.render()
        playback.once(Event.READY.value, Callback.wrap {
            assertTrue("seek should be called when start at is set", playback.seekWasCalled)
        })
    }

    @Test
    fun shouldCallSeekWhenOptionsHaveFloatValueStartAt() {
        var option = Options()
        option.put(ClapprOption.START_AT.value, 70.0)
        val playback = SomePlayback("valid-source.mp4", option)
        playback.render()
        playback.once(Event.READY.value, Callback.wrap {
            assertTrue("seek should be called when start at is set", playback.seekWasCalled)
        })
    }

    @Test
    fun shouldCallSeekWhenOptionsHaveNotANumberValueStartAt() {
        var option = Options()
        option.put(ClapprOption.START_AT.value, "fail")
        val playback = SomePlayback("valid-source.mp4", option)
        playback.render()
        playback.once(Event.READY.value, Callback.wrap {
            assertFalse("seek should be called when start at is set", playback.seekWasCalled)
        })
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
    fun shouldWorkWithEmptySelectedMediaOptions() {
        checkSelectedMediaOptions("")
    }

    @Test
    fun shouldWorkWithInvalidSelectedMediaOptions() {
        checkSelectedMediaOptions("error")
    }

    @Test
    fun shouldWorkWithInvalidArraySelectedMediaOptions() {
        checkSelectedMediaOptions("{\"invalid_array_name\":[{\"name\":\"Por\",\"type\":\"AUDIO\"}]}")
    }

    @Test
    fun shouldWorkWithInvalidTypeInSelectedMediaOptions() {
        checkSelectedMediaOptions("{\"media_option\":[{\"name\":\"invalid_name\",\"type\":\"AUDIO\"}]}")
    }

    @Test
    fun shouldWorkWithInvalidNameInSelectedMediaOptions() {
        checkSelectedMediaOptions("{\"media_option\":[{\"name\":\"Por\",\"type\":\"invalid_type\"}]}")
    }

    @Test
    fun shouldSelectMediaFromSelectedMediaOptions() {
        val jsonMediaOptionNameAudio = "Por"
        val jsonMediaOptionTypeAudio = "AUDIO"
        val jsonMediaOptionNameSubtitle = "por"
        val jsonMediaOptionTypeSubtitle = "SUBTITLE"
        val validJson = "{\"media_option\":[{\"name\":\"$jsonMediaOptionNameAudio\",\"type\":\"$jsonMediaOptionTypeAudio\"},{\"name\":\"$jsonMediaOptionNameSubtitle\",\"type\":\"$jsonMediaOptionTypeSubtitle\"}]}"

        val options = Options(options = hashMapOf(ClapprOption.SELECTED_MEDIA_OPTIONS.value to validJson))
        val playback = SomePlayback("valid-source.mp4", options)

        playback.addAvailableMediaOption(MediaOption(jsonMediaOptionNameAudio, MediaOptionType.AUDIO, null, null))
        playback.addAvailableMediaOption(MediaOption(jsonMediaOptionNameSubtitle, MediaOptionType.SUBTITLE, null, null))

        playback.setupInitialMediasFromClapprOptions()

        val audioSelected = playback.selectedMediaOption(MediaOptionType.AUDIO)
        assertEquals(jsonMediaOptionNameAudio, audioSelected?.name)
        assertEquals(jsonMediaOptionTypeAudio, audioSelected?.type?.name)

        val subtitleSelected = playback.selectedMediaOption(MediaOptionType.SUBTITLE)
        assertEquals(jsonMediaOptionNameSubtitle, subtitleSelected?.name)
        assertEquals(jsonMediaOptionTypeSubtitle, subtitleSelected?.type?.name)
    }

    private fun checkSelectedMediaOptions(mediaOptionJson: String) {
        try {
            val hashMap = HashMap<String, Any>()
            hashMap.put(ClapprOption.SELECTED_MEDIA_OPTIONS.value, mediaOptionJson)

            val options = Options(options = hashMap)
            val playback = SomePlayback("valid-source.mp4", options)
            playback.setupInitialMediasFromClapprOptions()
        } catch (ex: Exception) {
            fail("Exception: ${ex.message}")
        }
    }
}
