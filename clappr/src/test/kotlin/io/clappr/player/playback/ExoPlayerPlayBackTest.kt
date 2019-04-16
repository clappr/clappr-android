package io.clappr.player.playback

import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSourceEventListener
import io.clappr.player.BuildConfig
import io.clappr.player.base.BaseObject
import io.clappr.player.base.Event
import io.clappr.player.base.EventData
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
import org.robolectric.shadows.ShadowApplication
import org.robolectric.shadows.ShadowLog
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23), shadows = [ShadowLog::class, SubtitleViewShadow::class])
class ExoPlayerPlaybackTest {

    private lateinit var exoPlayerPlayBack: ExoPlayerPlayback
    private lateinit var bitrateHistory: BitrateHistory
    private lateinit var listenObject: BaseObject
    private var timeInNano: Long = 0L

    @Before
    fun setUp() {
        BaseObject.applicationContext = ShadowApplication.getInstance().applicationContext

        timeInNano = System.nanoTime()
        bitrateHistory = BitrateHistory { timeInNano }
        listenObject = BaseObject()
        exoPlayerPlayBack = ExoPlayerPlayback(source = "aSource", options = Options(), bitrateHistory = bitrateHistory)

    }

    @Test
    fun shouldTriggerWillSeekWhenSeekToLivePositionIsCalled() {
        var willSeekWasCalled = false
        listenObject.listenTo(exoPlayerPlayBack, Event.WILL_SEEK.value) {
            willSeekWasCalled = true
        }
        exoPlayerPlayBack.seekToLivePosition()
        assertTrue("WILL_SEEK event wasn't triggered when seekToLivePosition() method was called", willSeekWasCalled)
    }

    @Test
    fun shouldTriggerDidSeekWhenSeekToLivePositionIsCalled() {
        var didSeekWasCalled = false
        listenObject.listenTo(exoPlayerPlayBack, Event.DID_SEEK.value) {
            didSeekWasCalled = true
        }
        exoPlayerPlayBack.seekToLivePosition()
        assertTrue("DID_SEEK event wasn't triggered when seekToLivePosition() method was called", didSeekWasCalled)
    }

    @Test
    fun shouldTriggerDidUpdatePositionWhenSeekToLivePositionIsCalled() {
        var didUpdatePositionWasCalled = false
        listenObject.listenTo(exoPlayerPlayBack, Event.DID_UPDATE_POSITION.value) { bundle ->
            didUpdatePositionWasCalled = true
        }
        exoPlayerPlayBack.seekToLivePosition()
        assertTrue("DID_UPDATE_POSITION event wasn't triggered when seekToLivePosition() method was called", didUpdatePositionWasCalled)
    }

    @Test
    fun shouldReturnZeroBitrateWhenHistoryIsEmpty() {
        assertEquals(0, exoPlayerPlayBack.bitrate)
    }

    @Test
    fun shouldReturnZeroAverageBitrateWhenHistoryIsEmpty() {
        assertEquals(0, exoPlayerPlayBack.avgBitrate)
    }

    @Test
    fun shouldReturnLastReportedBitrate() {
        val expectedBitrate = 40L

        exoPlayerPlayBack.ExoplayerBitrateLogger().onLoadCompleted(null, null, addBitrateMediaLoadData(10))
        exoPlayerPlayBack.ExoplayerBitrateLogger().onLoadCompleted(null, null, addBitrateMediaLoadData(20))
        exoPlayerPlayBack.ExoplayerBitrateLogger().onLoadCompleted(null, null, addBitrateMediaLoadData(expectedBitrate))

        assertEquals(expectedBitrate, exoPlayerPlayBack.bitrate)
    }

    @Test
    fun shouldTriggerDidUpdateBitrate() {
        val expectedBitrate = 40L
        var actualBitrate = 0L

        val exoPlayerPlayback = ExoPlayerPlayback(source = "aSource", options = Options(), bitrateHistory = bitrateHistory)

        listenObject.listenTo(exoPlayerPlayback, Event.DID_UPDATE_BITRATE.value) {
            actualBitrate = it?.getLong(EventData.BITRATE.value) ?: 0L

        }
        exoPlayerPlayback.ExoplayerBitrateLogger().onLoadCompleted(null, null, addBitrateMediaLoadData(expectedBitrate))

        assertEquals(expectedBitrate, actualBitrate)
    }

    @Test
    fun shouldListeningDidUpdateOnDifferentBitrates() {
        var numberOfTriggers = 0

        listenObject.listenTo(exoPlayerPlayBack, Event.DID_UPDATE_BITRATE.value) { numberOfTriggers++ }
        exoPlayerPlayBack.ExoplayerBitrateLogger().onLoadCompleted(null, null, addBitrateMediaLoadData(10))
        exoPlayerPlayBack.ExoplayerBitrateLogger().onLoadCompleted(null, null, addBitrateMediaLoadData(40))

        assertEquals(2, numberOfTriggers)
    }

    @Test
    fun shouldTriggerDidUpdateBitrateOnlyForDifferentBitrate() {
        val bitrate = 10L
        var numberOfTriggers = 0

        listenObject.listenTo(exoPlayerPlayBack, Event.DID_UPDATE_BITRATE.value) { numberOfTriggers++ }
        exoPlayerPlayBack.ExoplayerBitrateLogger().onLoadCompleted(null, null, addBitrateMediaLoadData(bitrate))
        exoPlayerPlayBack.ExoplayerBitrateLogger().onLoadCompleted(null, null, addBitrateMediaLoadData(bitrate))

        assertEquals(1, numberOfTriggers)
    }

    @Test
    fun shouldNotTriggerDidUpdateBitrateWhenTrackTypeDifferentDefaultOrVideo() {
        var didUpdateBitrateCalled = false
        val bitrate = 40L

        listenObject.listenTo(exoPlayerPlayBack, Event.DID_UPDATE_BITRATE.value) { didUpdateBitrateCalled = true }
        exoPlayerPlayBack.ExoplayerBitrateLogger().onLoadCompleted(null, null, addBitrateMediaLoadData(bitrate, C.TRACK_TYPE_AUDIO))

        assertFalse(didUpdateBitrateCalled)
    }

    @Test
    fun shouldNotTriggerDidUpdateBitrateWhenMediaLoadDataNull() {
        val mediaLoadData = null
        var didUpdateBitrateCalled = false

        listenObject.listenTo(exoPlayerPlayBack, Event.DID_UPDATE_BITRATE.value) { didUpdateBitrateCalled = true }
        exoPlayerPlayBack.ExoplayerBitrateLogger().onLoadCompleted(null, null, mediaLoadData)

        assertFalse(didUpdateBitrateCalled)
    }

    @Test
    fun shouldHandleWrongTimeIntervalExceptionOnAddBitrateOnHistory() {
        timeInNano = -1

        exoPlayerPlayBack.ExoplayerBitrateLogger().onLoadCompleted(null, null, addBitrateMediaLoadData(20L))
    }

    @Test
    fun shouldNotTriggerDidUpdateBitrateWhenVideoFormatNull() {
        val videoFormatMock = null
        val mediaLoadData = MediaSourceEventListener.MediaLoadData(0, C.TRACK_TYPE_DEFAULT, videoFormatMock, 0,
                null, 0L, 0L)
        var didUpdateBitrateCalled = false

        listenObject.listenTo(exoPlayerPlayBack, Event.DID_UPDATE_BITRATE.value) { didUpdateBitrateCalled = true }
        exoPlayerPlayBack.ExoplayerBitrateLogger().onLoadCompleted(null, null, mediaLoadData)

        assertFalse(didUpdateBitrateCalled)
    }

    @Test
    fun shouldTriggerDidUpdateBitrateWhenTrackTypeDefault() {
        var didUpdateBitrateCalled = false
        val bitrate = 40L

        listenObject.listenTo(exoPlayerPlayBack, Event.DID_UPDATE_BITRATE.value) { didUpdateBitrateCalled = true }
        exoPlayerPlayBack.ExoplayerBitrateLogger().onLoadCompleted(null, null, addBitrateMediaLoadData(bitrate, C.TRACK_TYPE_DEFAULT))

        assertTrue(didUpdateBitrateCalled)
    }

    @Test
    fun shouldTriggerDidUpdateBitrateWhenTrackTypeVideo() {
        var didUpdateBitrateCalled = false
        val bitrate = 40L

        listenObject.listenTo(exoPlayerPlayBack, Event.DID_UPDATE_BITRATE.value) { didUpdateBitrateCalled = true }
        exoPlayerPlayBack.ExoplayerBitrateLogger().onLoadCompleted(null, null, addBitrateMediaLoadData(bitrate, C.TRACK_TYPE_VIDEO))

        assertTrue(didUpdateBitrateCalled)
    }

    @Test
    fun shouldReturnAverageBitrate() {
        val expectedAverageBitrate = 109L

        bitrateHistory.addBitrate(90, 2)
        bitrateHistory.addBitrate(100, 17)
        bitrateHistory.addBitrate(110, 31)

        assertEquals(expectedAverageBitrate, exoPlayerPlayBack.avgBitrate)
    }

    @Test
    fun shouldResetBitrateHistoryAfterStopping() {
        bitrateHistory.addBitrate(90, 2)
        bitrateHistory.addBitrate(100, 17)
        bitrateHistory.addBitrate(110, 31)

        exoPlayerPlayBack.stop()

        assertEquals(0, exoPlayerPlayBack.bitrate)
    }

    @Test
    fun shouldResetAverageBitrateHistoryAfterStopping() {
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

        assertEquals(Player.REPEAT_MODE_ALL, SimpleExoplayerShadow.staticRepeatMode)
    }

    private fun addBitrateMediaLoadData(bitrate: Long, trackType: Int = C.TRACK_TYPE_DEFAULT): MediaSourceEventListener.MediaLoadData {
        val videoFormatMock = Format.createVideoSampleFormat(null, null, null, bitrate.toInt(),
                0, 0, 0, 0f, listOf<ByteArray>(), null)
        val mediaLoadData = MediaSourceEventListener.MediaLoadData(0, trackType, videoFormatMock, 0,
                null, 0L, 0L)

        return mediaLoadData
    }
}