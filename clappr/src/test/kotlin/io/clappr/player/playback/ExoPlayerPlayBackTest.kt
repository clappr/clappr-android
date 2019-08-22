package io.clappr.player.playback

import androidx.test.core.app.ApplicationProvider
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSourceEventListener
import io.clappr.player.base.BaseObject
import io.clappr.player.base.ClapprOption.DEFAULT_AUDIO
import io.clappr.player.base.ClapprOption.DEFAULT_SUBTITLE
import io.clappr.player.base.Event.*
import io.clappr.player.base.EventData.BITRATE
import io.clappr.player.base.Options
import io.clappr.player.bitrate.BitrateHistory
import io.clappr.player.shadows.SimpleExoplayerShadow
import io.clappr.player.shadows.SubtitleViewShadow
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23], shadows = [ShadowLog::class, SubtitleViewShadow::class])
class ExoPlayerPlaybackTest {

    private lateinit var exoPlayerPlayBack: ExoPlayerPlayback
    private lateinit var bitrateHistory: BitrateHistory
    private lateinit var listenObject: BaseObject
    private var timeInNano: Long = 0L

    @Before
    fun setUp() {
        BaseObject.applicationContext = ApplicationProvider.getApplicationContext()

        timeInNano = System.nanoTime()
        bitrateHistory = BitrateHistory { timeInNano }
        listenObject = BaseObject()
        exoPlayerPlayBack = ExoPlayerPlayback(source = "aSource", options = Options(), bitrateHistory = bitrateHistory)
    }

    @Test
    fun `Should trigger WILL_SEEK when seekToLivePosition() is called`() {
        var willSeekWasCalled = false
        listenObject.listenTo(exoPlayerPlayBack, WILL_SEEK.value) {
            willSeekWasCalled = true
        }
        exoPlayerPlayBack.seekToLivePosition()
        assertTrue("WILL_SEEK event wasn't triggered when seekToLivePosition() method was called", willSeekWasCalled)
    }

    @Test
    fun `Should trigger DID_SEEK when seekToLivePosition() is called`() {
        var didSeekWasCalled = false
        listenObject.listenTo(exoPlayerPlayBack, DID_SEEK.value) {
            didSeekWasCalled = true
        }
        exoPlayerPlayBack.seekToLivePosition()
        assertTrue("DID_SEEK event wasn't triggered when seekToLivePosition() method was called", didSeekWasCalled)
    }

    @Test
    fun `Should trigger DID_UPDATE_POSITION when seek to live position is called`() {
        var didUpdatePositionWasCalled = false
        listenObject.listenTo(exoPlayerPlayBack, DID_UPDATE_POSITION.value) {
            didUpdatePositionWasCalled = true
        }
        exoPlayerPlayBack.seekToLivePosition()
        assertTrue("DID_UPDATE_POSITION event wasn't triggered when seekToLivePosition() method was called", didUpdatePositionWasCalled)
    }

    @Test
    fun `Should return zero bitrate when history is empty`() {
        assertEquals(0, exoPlayerPlayBack.bitrate)
    }

    @Test
    fun `Should return zero average bitrate when history is empty`() {
        assertEquals(0, exoPlayerPlayBack.avgBitrate)
    }

    @Test
    fun `Should return last reported bitrate`() {
        val expectedBitrate = 40L

        exoPlayerPlayBack.ExoplayerBitrateLogger().onLoadCompleted(null, null, addBitrateMediaLoadData(10))
        exoPlayerPlayBack.ExoplayerBitrateLogger().onLoadCompleted(null, null, addBitrateMediaLoadData(20))
        exoPlayerPlayBack.ExoplayerBitrateLogger().onLoadCompleted(null, null, addBitrateMediaLoadData(expectedBitrate))

        assertEquals(expectedBitrate, exoPlayerPlayBack.bitrate)
    }

    @Test
    fun `Should trigger DID_UPDATE_BITRATE`() {
        val expectedBitrate = 40L
        var actualBitrate = 0L

        val exoPlayerPlayback = ExoPlayerPlayback(source = "aSource", options = Options(), bitrateHistory = bitrateHistory)

        listenObject.listenTo(exoPlayerPlayback, DID_UPDATE_BITRATE.value) {
            actualBitrate = it?.getLong(BITRATE.value) ?: 0L

        }
        exoPlayerPlayback.ExoplayerBitrateLogger().onLoadCompleted(null, null, addBitrateMediaLoadData(expectedBitrate))

        assertEquals(expectedBitrate, actualBitrate)
    }

    @Test
    fun `Should listening DID_UPDATE_BITRATE on different bitrates`() {
        var numberOfTriggers = 0

        listenObject.listenTo(exoPlayerPlayBack, DID_UPDATE_BITRATE.value) { numberOfTriggers++ }
        exoPlayerPlayBack.ExoplayerBitrateLogger().onLoadCompleted(null, null, addBitrateMediaLoadData(10))
        exoPlayerPlayBack.ExoplayerBitrateLogger().onLoadCompleted(null, null, addBitrateMediaLoadData(40))

        assertEquals(2, numberOfTriggers)
    }

    @Test
    fun `Should trigger DID_UPDATE_BITRATE only for different bitrate`() {
        val bitrate = 10L
        var numberOfTriggers = 0

        listenObject.listenTo(exoPlayerPlayBack, DID_UPDATE_BITRATE.value) { numberOfTriggers++ }
        exoPlayerPlayBack.ExoplayerBitrateLogger().onLoadCompleted(null, null, addBitrateMediaLoadData(bitrate))
        exoPlayerPlayBack.ExoplayerBitrateLogger().onLoadCompleted(null, null, addBitrateMediaLoadData(bitrate))

        assertEquals(1, numberOfTriggers)
    }

    @Test
    fun `Should not trigger DID_UPDATE_BITRATE when track type is different from TRACK_TYPE_DEFAULT or TRACK_TYPE_VIDEO`() {
        var didUpdateBitrateCalled = false
        val bitrate = 40L

        listenObject.listenTo(
            exoPlayerPlayBack,
            DID_UPDATE_BITRATE.value
        ) { didUpdateBitrateCalled = true }
        exoPlayerPlayBack.ExoplayerBitrateLogger().onLoadCompleted(null, null, addBitrateMediaLoadData(bitrate, C.TRACK_TYPE_AUDIO))

        assertFalse(didUpdateBitrateCalled)
    }

    @Test
    fun `Should not trigger DID_UPDATE_BITRATE when media load data is null`() {
        val mediaLoadData = null
        var didUpdateBitrateCalled = false

        listenObject.listenTo(
            exoPlayerPlayBack,
            DID_UPDATE_BITRATE.value
        ) { didUpdateBitrateCalled = true }
        exoPlayerPlayBack.ExoplayerBitrateLogger().onLoadCompleted(null, null, mediaLoadData)

        assertFalse(didUpdateBitrateCalled)
    }

    @Test
    fun `Should handle wrong time interval exception on add bitrate on history`() {
        timeInNano = -1

        exoPlayerPlayBack.ExoplayerBitrateLogger().onLoadCompleted(null, null, addBitrateMediaLoadData(20L))
    }

    @Test
    fun `Should not trigger DID_UPDATE_BITRATE when video format is null`() {
        val videoFormatMock = null
        val mediaLoadData = MediaSourceEventListener.MediaLoadData(0, C.TRACK_TYPE_DEFAULT, videoFormatMock, 0,
                null, 0L, 0L)
        var didUpdateBitrateCalled = false

        listenObject.listenTo(
            exoPlayerPlayBack,
            DID_UPDATE_BITRATE.value
        ) { didUpdateBitrateCalled = true }
        exoPlayerPlayBack.ExoplayerBitrateLogger().onLoadCompleted(null, null, mediaLoadData)

        assertFalse(didUpdateBitrateCalled)
    }

    @Test
    fun `should trigger DID_UPDATE_BITRATE when TRACK_TYPE_DEFAULT`() {
        var didUpdateBitrateCalled = false
        val bitrate = 40L

        listenObject.listenTo(
            exoPlayerPlayBack,
            DID_UPDATE_BITRATE.value
        ) { didUpdateBitrateCalled = true }
        exoPlayerPlayBack.ExoplayerBitrateLogger().onLoadCompleted(null, null, addBitrateMediaLoadData(bitrate, C.TRACK_TYPE_DEFAULT))

        assertTrue(didUpdateBitrateCalled)
    }

    @Test
    fun `Should trigger DID_UPDATE_BITRATE when TRACK_TYPE_VIDEO`() {
        var didUpdateBitrateCalled = false
        val bitrate = 40L

        listenObject.listenTo(
            exoPlayerPlayBack,
            DID_UPDATE_BITRATE.value
        ) { didUpdateBitrateCalled = true }
        exoPlayerPlayBack.ExoplayerBitrateLogger().onLoadCompleted(null, null, addBitrateMediaLoadData(bitrate, C.TRACK_TYPE_VIDEO))

        assertTrue(didUpdateBitrateCalled)
    }

    @Test
    fun `Should return average bitrate`() {
        val expectedAverageBitrate = 109L

        bitrateHistory.addBitrate(90, 2)
        bitrateHistory.addBitrate(100, 17)
        bitrateHistory.addBitrate(110, 31)

        assertEquals(expectedAverageBitrate, exoPlayerPlayBack.avgBitrate)
    }

    @Test
    fun `Should reset bitrate history after stopping`() {
        bitrateHistory.addBitrate(90, 2)
        bitrateHistory.addBitrate(100, 17)
        bitrateHistory.addBitrate(110, 31)

        exoPlayerPlayBack.stop()

        assertEquals(0, exoPlayerPlayBack.bitrate)
    }

    @Test
    fun `Should reset average bitrate history after stopping`() {
        bitrateHistory.addBitrate(90, 2)
        bitrateHistory.addBitrate(100, 17)
        bitrateHistory.addBitrate(110, 31)

        exoPlayerPlayBack.stop()

        assertEquals(0, exoPlayerPlayBack.avgBitrate)
    }

    @Test
    @Config(shadows = [SimpleExoplayerShadow::class])
    fun `Should be REPEAT_MODE_OFF when no option is passed`() {
        val source = "supported-source.mp4"

        exoPlayerPlayBack = ExoPlayerPlayback(source = source, options = Options())
        exoPlayerPlayBack.load(source = source)

        assertEquals(Player.REPEAT_MODE_OFF, SimpleExoplayerShadow.staticRepeatMode)
    }

    @Test
    @Config(shadows = [SimpleExoplayerShadow::class])
    fun `Should be REPEAT_MODE_OFF when invalid option is passed`() {
        val source = "supported-source.mp4"
        val options = Options(options = hashMapOf("loop" to "asda"))

        exoPlayerPlayBack = ExoPlayerPlayback(source = source, options = options)
        exoPlayerPlayBack.load(source = source)

        assertEquals(Player.REPEAT_MODE_OFF, SimpleExoplayerShadow.staticRepeatMode)
    }

    @Test
    @Config(shadows = [SimpleExoplayerShadow::class])
    fun `Should be REPEAT_MODE_OFF when loop false is passed`() {
        val source = "supported-source.mp4"
        val options = Options(options = hashMapOf("loop" to false))

        exoPlayerPlayBack = ExoPlayerPlayback(source = source, options = options)
        exoPlayerPlayBack.load(source = source)

        assertEquals(Player.REPEAT_MODE_OFF, SimpleExoplayerShadow.staticRepeatMode)
    }

    @Test
    @Config(shadows = [SimpleExoplayerShadow::class])
    fun `Should be REPEAT_MODE_ALL when loop true is passed`() {
        val source = "supported-source.mp4"
        val options = Options(options = hashMapOf("loop" to true))

        exoPlayerPlayBack = ExoPlayerPlayback(source = source, options = options)
        exoPlayerPlayBack.load(source = source)

        assertEquals(Player.REPEAT_MODE_ONE, SimpleExoplayerShadow.staticRepeatMode)
    }

    @Test
    fun `Should set selectedAudio when has DEFAULT_AUDIO option`() {
        val source = "supported-source.mp4"
        val options = Options(options = hashMapOf(DEFAULT_AUDIO.value to "por"))

        exoPlayerPlayBack = ExoPlayerPlayback(source = source, options = options)
        exoPlayerPlayBack.load(source = source)

        exoPlayerPlayBack.setupInitialMediaFromOptions()

        assertEquals("por", exoPlayerPlayBack.selectedAudio)
    }

    @Test
    fun `Should set selectedSubtitle when has DEFAULT_SUBTITLE option`() {
        val source = "supported-source.mp4"
        val options = Options(options = hashMapOf(DEFAULT_SUBTITLE.value to "por"))

        exoPlayerPlayBack = ExoPlayerPlayback(source = source, options = options)
        exoPlayerPlayBack.load(source = source)

        exoPlayerPlayBack.setupInitialMediaFromOptions()

        assertEquals("por", exoPlayerPlayBack.selectedSubtitle)
    }

    private fun addBitrateMediaLoadData(bitrate: Long, trackType: Int = C.TRACK_TYPE_DEFAULT): MediaSourceEventListener.MediaLoadData {
        val videoFormatMock = Format.createVideoSampleFormat(null, null, null, bitrate.toInt(),
                0, 0, 0, 0f, listOf<ByteArray>(), null)

        return MediaSourceEventListener.MediaLoadData(0, trackType, videoFormatMock,
                0, null, 0L, 0L)
    }
}