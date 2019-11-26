package io.clappr.player.playback

import androidx.test.core.app.ApplicationProvider
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.C.TRACK_TYPE_AUDIO
import com.google.android.exoplayer2.C.TRACK_TYPE_TEXT
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.Player.*
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSourceEventListener
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import io.clappr.player.base.BaseObject
import io.clappr.player.base.ClapprOption
import io.clappr.player.base.Event.*
import io.clappr.player.base.EventData
import io.clappr.player.base.InternalEvent.DID_FIND_AUDIO
import io.clappr.player.base.InternalEvent.DID_FIND_SUBTITLE
import io.clappr.player.base.InternalEventData.FOUND_AUDIOS
import io.clappr.player.base.InternalEventData.FOUND_SUBTITLES
import io.clappr.player.base.Options
import io.clappr.player.bitrate.BitrateHistory
import io.clappr.player.components.AudioLanguage
import io.clappr.player.components.Playback.State
import io.clappr.player.components.SubtitleLanguage
import io.clappr.player.shadows.SimpleExoplayerShadow
import io.clappr.player.shadows.SubtitleViewShadow
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog
import java.io.IOException
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.test.assertEquals
import kotlin.test.fail

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23], shadows = [ShadowLog::class, SubtitleViewShadow::class])
class ExoPlayerPlaybackTest {

    private lateinit var exoPlayerPlayBack: ExoPlayerPlayback
    private lateinit var bitrateHistory: BitrateHistory
    private lateinit var listenObject: BaseObject
    private var timeInNano: Long = 0L

    @MockK(relaxed = true)
    private lateinit var mockBitrateHandler: ExoPlayerBitrateHandler

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        BaseObject.applicationContext = ApplicationProvider.getApplicationContext()

        timeInNano = System.nanoTime()
        bitrateHistory = BitrateHistory { timeInNano }
        listenObject = BaseObject()

        exoPlayerPlayBack = ExoPlayerPlayback(
            source = "aSource",
            options = Options()
        )
    }

    @Test
    fun `Should trigger WILL_SEEK with position in seconds`() {
        var position = -1.0
        listenObject.listenTo(exoPlayerPlayBack, WILL_SEEK.value) {
            position = it?.getDouble("position") ?: -1.0
        }
        exoPlayerPlayBack.seek(13)

        assertEquals(13.0, position)
    }

    @Test
    fun `should trigger PLAYING after seeking while on PLAYING state`() {
        playbackOnPlayingState()

        var playingWasCalled = false
        listenObject.listenTo(exoPlayerPlayBack, PLAYING.value) {
            playingWasCalled = true
        }
        listenObject.listenTo(exoPlayerPlayBack, DID_PAUSE.value) {
            fail("DID_PAUSE should not have been called")
        }
        listenObject.listenTo(exoPlayerPlayBack, STALLING.value) {
            fail("STALLING should not have been called")
        }

        exoPlayerPlayBack.seek(10)

        assertEquals(playingWasCalled, true)
    }

    @Test
    fun `should trigger DID_PAUSE after seeking while on PAUSED state`() {
        playbackOnPausedState()

        var wasCalled = false
        listenObject.listenTo(exoPlayerPlayBack, DID_PAUSE.value) {
            wasCalled = true
        }
        listenObject.listenTo(exoPlayerPlayBack, PLAYING.value) {
            fail("PLAYING should not have been called")
        }
        listenObject.listenTo(exoPlayerPlayBack, STALLING.value) {
            fail("STALLING should not have been called")
        }

        exoPlayerPlayBack.seek(10)

        assertEquals(wasCalled, true)
    }

    @Test
    fun `should trigger STALLING after seeking while on STALLING state`() {
        playbackOnStallingState()

        var wasCalled = false
        listenObject.listenTo(exoPlayerPlayBack, STALLING.value) {
            wasCalled = true
        }

        listenObject.listenTo(exoPlayerPlayBack, PLAYING.value) {
            fail("PLAYING should not have been called")
        }
        listenObject.listenTo(exoPlayerPlayBack, DID_PAUSE.value) {
            fail("DID_PAUSE should not have been called")
        }

        exoPlayerPlayBack.seek(10)

        assertEquals(wasCalled, true)
    }

    private fun playbackOnPlayingState() {
        exoPlayerPlayBack = ExoPlayerPlayback("supported-source.mp4")
        exoPlayerPlayBack.eventsListener.onPlayerStateChanged(true, STATE_READY)
        assertEquals(State.PLAYING, exoPlayerPlayBack.state)
    }

    private fun playbackOnPausedState() {
        exoPlayerPlayBack = ExoPlayerPlayback("supported-source.mp4")
        exoPlayerPlayBack.eventsListener.onPlayerStateChanged(true, STATE_READY)
        exoPlayerPlayBack.eventsListener.onPlayerStateChanged(false, STATE_READY)
        assertEquals(State.PAUSED, exoPlayerPlayBack.state)
    }

    private fun playbackOnStallingState() {
        exoPlayerPlayBack = ExoPlayerPlayback("supported-source.mp4")
        exoPlayerPlayBack.eventsListener.onPlayerStateChanged(true, STATE_READY)
        exoPlayerPlayBack.eventsListener.onPlayerStateChanged(false, STATE_BUFFERING)
        assertEquals(State.STALLING, exoPlayerPlayBack.state)
    }

    @Test
    fun `Should trigger WILL_SEEK when seekToLivePosition() is called`() {
        var willSeekWasCalled = false
        listenObject.listenTo(exoPlayerPlayBack, WILL_SEEK.value) {
            willSeekWasCalled = true
        }
        exoPlayerPlayBack.seekToLivePosition()
        assertTrue(
            "WILL_SEEK event wasn't triggered when seekToLivePosition() method was called",
            willSeekWasCalled
        )
    }

    @Test
    fun `Should trigger DID_SEEK when seekToLivePosition() is called`() {
        var didSeekWasCalled = false
        listenObject.listenTo(exoPlayerPlayBack, DID_SEEK.value) {
            didSeekWasCalled = true
        }
        exoPlayerPlayBack.seekToLivePosition()
        assertTrue(
            "DID_SEEK event wasn't triggered when seekToLivePosition() method was called",
            didSeekWasCalled
        )
    }

    @Test
    fun `Should trigger DID_UPDATE_POSITION when seek to live position is called`() {
        var didUpdatePositionWasCalled = false
        listenObject.listenTo(exoPlayerPlayBack, DID_UPDATE_POSITION.value) {
            didUpdatePositionWasCalled = true
        }
        exoPlayerPlayBack.seekToLivePosition()
        assertTrue(
            "DID_UPDATE_POSITION event wasn't triggered when seekToLivePosition() method was called",
            didUpdatePositionWasCalled
        )
    }

    @Test
    fun `Should trigger WILL_SEEK with DVR window duration, discounted buffer sync time, when seek to live position is called`() {

        exoPlayerPlayBack = ExoPlayerPlayback(source = "bla.mp4")
        exoPlayerPlayBack.setPlayer(playerWithDVRAndDuration(120_000))

        var position = 0.0
        listenObject.listenTo(exoPlayerPlayBack, WILL_SEEK.value) {
            position = it?.getDouble("position") ?: 0.0
        }

        exoPlayerPlayBack.seekToLivePosition()

        assertEquals(100.0, position)
    }

    @Test
    fun `Should trigger DID_UPDATE_BITRATE when bitrate updates`() {
        val expectedBitrate = 40L
        var actualBitrate = 0L

        listenObject.listenTo(exoPlayerPlayBack, DID_UPDATE_BITRATE.value) {
            actualBitrate = it?.getLong(EventData.BITRATE.value) ?: 0L

        }

        exoPlayerPlayBack.getBitrateLogger().analyticsListener.onLoadCompleted(null, null, mediaLoadData(40L))

        assertEquals(expectedBitrate, actualBitrate)
    }

    @Test
    fun `Should get average bitrate from bitrateLogger`() {
        exoPlayerPlayBack.setBitrateLogger(mockBitrateHandler)


        every { mockBitrateHandler.averageBitrate } returns 33L

        assertEquals(33L, exoPlayerPlayBack.avgBitrate)
    }

    @Test
    fun `Should reset bitrate history after stopping`() {
        exoPlayerPlayBack.setBitrateLogger(mockBitrateHandler)

        exoPlayerPlayBack.stop()

        verify {
            mockBitrateHandler.reset()
        }
    }

    @Test
    @Config(shadows = [SimpleExoplayerShadow::class])
    fun `Should be REPEAT_MODE_OFF when no option is passed`() {
        val source = "supported-source.mp4"

        exoPlayerPlayBack = ExoPlayerPlayback(source = source, options = Options())
        exoPlayerPlayBack.load(source = source)

        assertEquals(REPEAT_MODE_OFF, SimpleExoplayerShadow.staticRepeatMode)
    }

    @Test
    @Config(shadows = [SimpleExoplayerShadow::class])
    fun `Should be REPEAT_MODE_OFF when invalid option is passed`() {
        val source = "supported-source.mp4"
        val options = Options(options = hashMapOf("loop" to "asda"))

        exoPlayerPlayBack = ExoPlayerPlayback(source = source, options = options)
        exoPlayerPlayBack.load(source = source)

        assertEquals(REPEAT_MODE_OFF, SimpleExoplayerShadow.staticRepeatMode)
    }

    @Test
    @Config(shadows = [SimpleExoplayerShadow::class])
    fun `Should be REPEAT_MODE_OFF when loop false is passed`() {
        val source = "supported-source.mp4"
        val options = Options(options = hashMapOf("loop" to false))

        exoPlayerPlayBack = ExoPlayerPlayback(source = source, options = options)
        exoPlayerPlayBack.load(source = source)

        assertEquals(REPEAT_MODE_OFF, SimpleExoplayerShadow.staticRepeatMode)
    }

    @Test
    @Config(shadows = [SimpleExoplayerShadow::class])
    fun `Should be REPEAT_MODE_ALL when loop true is passed`() {
        val source = "supported-source.mp4"
        val options = Options(options = hashMapOf("loop" to true))

        exoPlayerPlayBack = ExoPlayerPlayback(source = source, options = options)
        exoPlayerPlayBack.load(source = source)

        assertEquals(REPEAT_MODE_ONE, SimpleExoplayerShadow.staticRepeatMode)
    }

    @Test
    fun `Should trigger WILL_LOAD_SOURCE when load is called`() {
        val source = "supported-source.mp4"
        var eventCalled = false

        exoPlayerPlayBack = ExoPlayerPlayback(source = source)

        listenObject.listenTo(exoPlayerPlayBack, WILL_LOAD_SOURCE.value) {
            eventCalled = true
        }

        exoPlayerPlayBack.load(source = source)

        assertTrue(
            "WILL_LOAD_SOURCE event wasn't triggered when load method was called",
            eventCalled
        )
    }

    @Test
    fun `Should trigger DID_LOAD_SOURCE when load is called`() {
        val source = "supported-source.mp4"
        var eventCalled = false

        exoPlayerPlayBack = ExoPlayerPlayback(source = source)

        listenObject.listenTo(exoPlayerPlayBack, DID_LOAD_SOURCE.value) {
            eventCalled = true
        }

        exoPlayerPlayBack.load(source = source)

        assertTrue(
            "DID_LOAD_SOURCE event wasn't triggered when load method was called",
            eventCalled
        )
    }

    @Test
    fun `Should trigger WILL_STOP when stop is called`() {
        val source = "supported-source.mp4"
        var eventCalled = false

        exoPlayerPlayBack = ExoPlayerPlayback(source = source)

        listenObject.listenTo(exoPlayerPlayBack, WILL_STOP.value) {
            eventCalled = true
        }

        exoPlayerPlayBack.stop()

        assertTrue(
            "WILL_STOP event wasn't triggered when load method was called",
            eventCalled
        )
    }

    @Test
    fun `Should trigger DID_STOP when stop is called`() {
        val source = "supported-source.mp4"
        var eventCalled = false

        exoPlayerPlayBack = ExoPlayerPlayback(source = source)

        listenObject.listenTo(exoPlayerPlayBack, DID_STOP.value) {
            eventCalled = true
        }

        exoPlayerPlayBack.stop()

        assertTrue(
            "WILL_STOP event wasn't triggered when load method was called",
            eventCalled
        )
    }

    @Test
    fun `Should have an IDLE state after stop is called`() {
        val source = "supported-source.mp4"

        exoPlayerPlayBack = ExoPlayerPlayback(source = source)

        exoPlayerPlayBack.stop()

        assertFalse(exoPlayerPlayBack.isDvrInUse)
        assertEquals(State.IDLE, exoPlayerPlayBack.state)
        assertEquals(mutableSetOf(), exoPlayerPlayBack.availableAudios)
        assertEquals(mutableSetOf(), exoPlayerPlayBack.availableSubtitles)
        assertEquals(null, exoPlayerPlayBack.selectedAudio)
        assertEquals(SubtitleLanguage.OFF.value, exoPlayerPlayBack.selectedSubtitle)
    }

    @Test
    fun `Should trigger DID_COMPLETE when ExoPlayer reaches end state`() {
        val source = "supported-source.mp4"
        var eventCalled = false

        exoPlayerPlayBack = ExoPlayerPlayback(source = source)

        listenObject.listenTo(exoPlayerPlayBack, DID_COMPLETE.value) {
            eventCalled = true
        }

        exoPlayerPlayBack.eventsListener.onPlayerStateChanged(true, STATE_ENDED)

        assertEquals(State.IDLE, exoPlayerPlayBack.state)
        assertTrue(
            "DID_COMPLETE event wasn't triggered when load method was called",
            eventCalled
        )
    }

    @Test
    fun `Should trigger STALLING when ExoPlayer reaches buffering state`() {
        val source = "supported-source.mp4"
        var eventCalled = false

        exoPlayerPlayBack = ExoPlayerPlayback(source = source)

        listenObject.listenTo(exoPlayerPlayBack, STALLING.value) {
            eventCalled = true
        }

        exoPlayerPlayBack.eventsListener.onPlayerStateChanged(false, STATE_READY)
        exoPlayerPlayBack.eventsListener.onPlayerStateChanged(true, STATE_BUFFERING)

        assertEquals(State.STALLING, exoPlayerPlayBack.state)
        assertTrue(
            "STALLING event wasn't triggered when load method was called",
            eventCalled
        )
    }

    @Test
    fun `Should trigger READY when ExoPlayer reaches ready state`() {
        val source = "supported-source.mp4"
        var eventCalled = false

        exoPlayerPlayBack = ExoPlayerPlayback(source = source)

        listenObject.listenTo(exoPlayerPlayBack, READY.value) {
            eventCalled = true
        }

        exoPlayerPlayBack.eventsListener.onPlayerStateChanged(false, STATE_READY)

        assertEquals(State.IDLE, exoPlayerPlayBack.state)
        assertTrue(
            "READY event wasn't triggered when handleExoPlayerReadyState method was called",
            eventCalled
        )
    }

    @Test
    fun `Should trigger READY when ExoPlayer load changes`() {
        val source = "supported-source.mp4"
        var eventCalled = false

        exoPlayerPlayBack = ExoPlayerPlayback(source = source)

        listenObject.listenTo(exoPlayerPlayBack, READY.value) {
            eventCalled = true
        }

        exoPlayerPlayBack.eventsListener.onLoadingChanged(true)

        assertEquals(State.IDLE, exoPlayerPlayBack.state)
        assertTrue(
            "READY event wasn't triggered when onLoadingChanged method was called",
            eventCalled
        )
    }

    @Test
    fun `Should trigger PLAYING when ExoPlayer reaches ready state and play is ready`() {
        val source = "supported-source.mp4"
        var eventCalled = false

        exoPlayerPlayBack = ExoPlayerPlayback(source = source)

        listenObject.listenTo(exoPlayerPlayBack, PLAYING.value) {
            eventCalled = true
        }

        exoPlayerPlayBack.eventsListener.onPlayerStateChanged(false, STATE_READY)
        exoPlayerPlayBack.eventsListener.onPlayerStateChanged(true, STATE_READY)

        assertEquals(State.PLAYING, exoPlayerPlayBack.state)
        assertTrue(
            "PLAYING event wasn't triggered when handleExoPlayerReadyState method was called",
            eventCalled
        )
    }

    @Test
    fun `Should trigger DID_PAUSE when ExoPlayer reaches ready state and play is not ready`() {
        val source = "supported-source.mp4"
        var eventCalled = false

        exoPlayerPlayBack = ExoPlayerPlayback(source = source)

        listenObject.listenTo(exoPlayerPlayBack, DID_PAUSE.value) {
            eventCalled = true
        }

        exoPlayerPlayBack.eventsListener.onPlayerStateChanged(false, STATE_READY)
        exoPlayerPlayBack.eventsListener.onPlayerStateChanged(false, STATE_READY)

        assertEquals(State.PAUSED, exoPlayerPlayBack.state)
        assertTrue(
            "DID_PAUSE event wasn't triggered when handleExoPlayerReadyState method was called",
            eventCalled
        )
    }

    @Test
    fun `Should trigger ERROR event when ExoPlayer receive an error`() {
        val source = "supported-source.mp4"
        var eventCalled = false

        exoPlayerPlayBack = ExoPlayerPlayback(source = source)

        listenObject.listenTo(exoPlayerPlayBack, ERROR.value) {
            eventCalled = true
        }

        val exception = ExoPlaybackException.createForSource(IOException())
        exoPlayerPlayBack.eventsListener.onPlayerError(exception)

        assertEquals(State.ERROR, exoPlayerPlayBack.state)
        assertTrue(
            "ERROR event wasn't triggered when handleError method was called",
            eventCalled
        )
    }

    @Test
    fun `Should trigger DID_FIND_SUBTITLE when ExoPlayer load available external subtitles`() {
        val source = "supported-source.mp4"
        var eventCalled = false
        val options = Options(
            options = hashMapOf(ClapprOption.SUBTITLES.value to hashMapOf("por" to "url"))
        )

        exoPlayerPlayBack = ExoPlayerPlayback(source = source, options = options)

        listenObject.listenTo(exoPlayerPlayBack, DID_FIND_SUBTITLE.value) {
            eventCalled = true
        }

        exoPlayerPlayBack.eventsListener.onLoadingChanged(true)
        exoPlayerPlayBack.eventsListener.onPlayerStateChanged(false, STATE_READY)

        assertEquals("off", exoPlayerPlayBack.selectedSubtitle)
        assertTrue(
            "DID_FIND_SUBTITLE event wasn't triggered when setupExternalSubtitles method was called",
            eventCalled
        )
    }

    @Test
    fun `Should trigger DID_UPDATE_AUDIO when ExoPlayer receive default audio from Clappr`() {
        val source = "supported-source.mp4"
        var eventCalled = false
        val options = Options(
            options = hashMapOf(ClapprOption.DEFAULT_AUDIO.value to AudioLanguage.PORTUGUESE.value)
        )

        exoPlayerPlayBack = ExoPlayerPlayback(source = source, options = options)
        exoPlayerPlayBack.availableAudios += setOf(AudioLanguage.PORTUGUESE.value)

        listenObject.listenTo(exoPlayerPlayBack, DID_UPDATE_AUDIO.value) {
            eventCalled = true
        }

        exoPlayerPlayBack.eventsListener.onLoadingChanged(true)
        exoPlayerPlayBack.eventsListener.onPlayerStateChanged(false, STATE_READY)

        assertEquals(AudioLanguage.PORTUGUESE.value, exoPlayerPlayBack.selectedAudio)
        assertTrue(
            "DID_UPDATE_AUDIO event wasn't triggered when setupInitialMediasFromClapprOptions method was called",
            eventCalled
        )
    }

    @Test
    fun `Should trigger DID_UPDATE_SUBTITLE when ExoPlayer receive default subtitle from Clappr`() {
        val source = "supported-source.mp4"
        var eventCalled = false
        val options = Options(
            options = hashMapOf(ClapprOption.DEFAULT_SUBTITLE.value to AudioLanguage.PORTUGUESE.value)
        )

        exoPlayerPlayBack = ExoPlayerPlayback(source = source, options = options)
        exoPlayerPlayBack.availableSubtitles += setOf(AudioLanguage.PORTUGUESE.value)

        listenObject.listenTo(exoPlayerPlayBack, DID_UPDATE_SUBTITLE.value) {
            eventCalled = true
        }

        exoPlayerPlayBack.eventsListener.onLoadingChanged(true)
        exoPlayerPlayBack.eventsListener.onPlayerStateChanged(false, STATE_READY)

        assertEquals(AudioLanguage.PORTUGUESE.value, exoPlayerPlayBack.selectedSubtitle)
        assertTrue(
            "DID_UPDATE_SUBTITLE event wasn't triggered when setupInitialMediasFromClapprOptions method was called",
            eventCalled
        )
    }

    @Test
    fun `Should trigger DID_FIND_AUDIO when ExoPlayer load default audios`() {
        val source = "supported-source.mp4"
        var foundAudios: List<String>? = null

        val trackSelector = buildAvailableTracks(
            TRACK_TYPE_AUDIO to listOf(
                AudioLanguage.PORTUGUESE.value,
                AudioLanguage.ENGLISH.value
            )
        ) as DefaultTrackSelector

        exoPlayerPlayBack = ExoPlayerPlayback(
            source = source,
            createDefaultTrackSelector = { trackSelector }
        )

        exoPlayerPlayBack.load(source = source)

        listenObject.listenTo(exoPlayerPlayBack, DID_FIND_AUDIO.value) {
            foundAudios = it!!.getStringArrayList(FOUND_AUDIOS.value)
        }

        exoPlayerPlayBack.eventsListener.onLoadingChanged(true)
        exoPlayerPlayBack.eventsListener.onPlayerStateChanged(false, STATE_READY)

        val expectedAudios = listOf(
            AudioLanguage.PORTUGUESE.value,
            AudioLanguage.ENGLISH.value
        )

        assertEquals(expectedAudios, foundAudios)
    }

    @Test
    fun `Should trigger DID_FIND_SUBTITLE when ExoPlayer load default subtitles`() {
        val source = "supported-source.mp4"
        var foundSubtitles: List<String>? = null

        val trackSelector = buildAvailableTracks(
            TRACK_TYPE_TEXT to listOf(
                SubtitleLanguage.OFF.value,
                SubtitleLanguage.PORTUGUESE.value
            )
        ) as DefaultTrackSelector

        exoPlayerPlayBack = ExoPlayerPlayback(
            source = source,
            createDefaultTrackSelector = { trackSelector }
        )

        exoPlayerPlayBack.load(source = source)

        listenObject.listenTo(exoPlayerPlayBack, DID_FIND_SUBTITLE.value) {
            foundSubtitles = it!!.getStringArrayList(FOUND_SUBTITLES.value)
        }

        exoPlayerPlayBack.eventsListener.onLoadingChanged(true)
        exoPlayerPlayBack.eventsListener.onPlayerStateChanged(false, STATE_READY)

        val expectedSubtitles = listOf(
            SubtitleLanguage.OFF.value,
            SubtitleLanguage.PORTUGUESE.value
        )

        assertEquals(expectedSubtitles, foundSubtitles)
    }

    @Test
    fun `duration should not discount sync buffer time VOD`() {
        val source = "supported-source.mp4"

        exoPlayerPlayBack = ExoPlayerPlayback(source = source)
        exoPlayerPlayBack.setPlayer(playerWithVODDuration(120_000))

        assertEquals(120.0, exoPlayerPlayBack.duration)
    }

    @Test
    fun `duration should discount sync buffer time of 20s for live videos with DVR`() {
        val source = "supported-source.mp4"

        exoPlayerPlayBack = ExoPlayerPlayback(source = source)
        exoPlayerPlayBack.setPlayer(playerWithDVRAndDuration(120_000))

        assertEquals(100.0, exoPlayerPlayBack.duration)
    }

    private fun playerWithDVRAndDuration(millis: Long) = mockk<SimpleExoPlayer>(relaxed = true).apply {
        every { isCurrentWindowDynamic } returns true
        every { isCurrentWindowSeekable } returns true
        every { duration } returns millis
    }

    private fun playerWithVODDuration(millis: Long) = mockk<SimpleExoPlayer>().apply {
        every { isCurrentWindowDynamic } returns false
        every { duration } returns millis
    }

    private fun ExoPlayerPlayback.setPlayer(player: SimpleExoPlayer) {
        ExoPlayerPlayback::class.java.declaredFields.first { it.name == "player" }.apply {
            isAccessible = true
            set(this@setPlayer, player)
        }
    }

    private fun ExoPlayerPlayback.setBitrateLogger(bitrateHandler: ExoPlayerBitrateHandler) {
        ExoPlayerPlayback::class.java.declaredFields.first { it.name == "bitrateHandler" }.apply {
            isAccessible = true
            set(this@setBitrateLogger, bitrateHandler)
        }
    }

    private fun ExoPlayerPlayback.getBitrateLogger(): ExoPlayerBitrateHandler =
        ExoPlayerPlayback::class.memberProperties.first { it.name == "bitrateHandler" }.run {
            isAccessible = true
            get(this@getBitrateLogger) as ExoPlayerBitrateHandler
        }

    private fun mediaLoadData(bitrate: Long)
            : MediaSourceEventListener.MediaLoadData {
        val format = Format.createVideoSampleFormat(
            null, null, null, bitrate.toInt(),
            0, 0, 0, 0f, listOf<ByteArray>(), null
        )
        return MediaSourceEventListener.MediaLoadData(
            0, C.TRACK_TYPE_DEFAULT, format,
            0, null, 0L, 0L
        )
    }
}
