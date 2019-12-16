package io.clappr.player.playback

import androidx.test.core.app.ApplicationProvider
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.source.MediaSourceEventListener
import io.clappr.player.base.BaseObject
import io.clappr.player.base.Event
import io.clappr.player.base.EventData
import io.clappr.player.base.Options
import io.clappr.player.bitrate.BitrateHistory
import io.clappr.player.components.Playback
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class ExoPlayerBitrateLoggerTest {

    private lateinit var playback: DummyPlayback
    private lateinit var bitrateHistory: BitrateHistory
    private lateinit var listenObject: BaseObject
    private var timeInNano: Long = 0L
    private lateinit var bitrateEventsListener: ExoPlayerBitrateLogger

    @Before
    fun setUp() {
        BaseObject.applicationContext = ApplicationProvider.getApplicationContext()

        timeInNano = System.nanoTime()
        bitrateHistory = BitrateHistory { timeInNano }
        listenObject = BaseObject()

        playback = DummyPlayback()

        bitrateEventsListener = ExoPlayerBitrateLogger(playback, bitrateHistory = bitrateHistory)

    }

    @Test
    fun `Should return last reported bitrate`() {
        val expectedBitrate = 40L

        bitrateEventsListener.onLoadCompleted(null, null, mediaLoadData(10))
        bitrateEventsListener.onLoadCompleted(null, null, mediaLoadData(20))
        bitrateEventsListener.onLoadCompleted(null, null, mediaLoadData(expectedBitrate))

        assertEquals(expectedBitrate, bitrateEventsListener.lastBitrate)
    }

    @Test
    fun `Should trigger DID_UPDATE_BITRATE for default track`() {
        val expectedBitrate = 40L
        var actualBitrate = 0L

        listenObject.listenTo(playback, Event.DID_UPDATE_BITRATE.value) {
            actualBitrate = it?.getLong(EventData.BITRATE.value) ?: 0L

        }
        bitrateEventsListener.onLoadCompleted(null, null, mediaLoadData(expectedBitrate,  C.TRACK_TYPE_DEFAULT))

        assertEquals(expectedBitrate, actualBitrate)
    }

    @Test
    fun `Should trigger DID_UPDATE_BITRATE when TRACK_TYPE_VIDEO`() {
        var didUpdateBitrateCalled = false
        val bitrate = 40L

        listenObject.listenTo(playback, Event.DID_UPDATE_BITRATE.value) {
            didUpdateBitrateCalled = true
        }
        bitrateEventsListener.onLoadCompleted(null, null, mediaLoadData(bitrate, C.TRACK_TYPE_VIDEO))

        assertTrue(didUpdateBitrateCalled)
    }

    @Test
    fun `Should trigger DID_UPDATE_BITRATE when TRACK_TYPE_AUDIO`() {
        val expectedBitrate = 128_000L
        var actualBitrate = 0L

        listenObject.listenTo(playback, Event.DID_UPDATE_BITRATE.value) {
            actualBitrate = it?.getLong(EventData.BITRATE.value) ?: 0L
        }
        bitrateEventsListener.onLoadCompleted(null, null, mediaLoadData(expectedBitrate, C.TRACK_TYPE_AUDIO))

        assertEquals(expectedBitrate, actualBitrate)
    }

    @Test
    fun `Should add video and audio bitrates when both are known`() {

        bitrateEventsListener.onLoadCompleted(null, null, mediaLoadData(3_000_000, C.TRACK_TYPE_VIDEO))
        bitrateEventsListener.onLoadCompleted(null, null, mediaLoadData(128_000, C.TRACK_TYPE_AUDIO))

        assertEquals(3_128_000, bitrateEventsListener.lastBitrate)
    }

    @Test
    fun `Should not accept audio bitrate less than 0 `() {

        bitrateEventsListener.onLoadCompleted(null, null, mediaLoadData(3_000_000, C.TRACK_TYPE_VIDEO))
        bitrateEventsListener.onLoadCompleted(null, null, mediaLoadData(-1, C.TRACK_TYPE_AUDIO))

        assertEquals(3_000_000, bitrateEventsListener.lastBitrate)
    }

    @Test
    fun `Should trigger DID_UPDATE_BITRATE with new sum of audio and video bitrate whenever video bitrate changes`() {
        var actualBitrate = 0L

        listenObject.listenTo(playback, Event.DID_UPDATE_BITRATE.value) {
            actualBitrate = it?.getLong(EventData.BITRATE.value) ?: 0L
        }

        bitrateEventsListener.onLoadCompleted(null, null, mediaLoadData(3_000_000, C.TRACK_TYPE_VIDEO))
        bitrateEventsListener.onLoadCompleted(null, null, mediaLoadData(128_000, C.TRACK_TYPE_AUDIO))

        bitrateEventsListener.onLoadCompleted(null, null, mediaLoadData(2_000_000, C.TRACK_TYPE_VIDEO))

        assertEquals(2_128_000, actualBitrate)
    }

    @Test
    fun `Should trigger DID_UPDATE_BITRATE with new sum of audio and video bitrate whenever audio bitrate changes`() {
        var actualBitrate = 0L

        listenObject.listenTo(playback, Event.DID_UPDATE_BITRATE.value) {
            actualBitrate = it?.getLong(EventData.BITRATE.value) ?: 0L
        }

        bitrateEventsListener.onLoadCompleted(null, null, mediaLoadData(3_000_000, C.TRACK_TYPE_VIDEO))
        bitrateEventsListener.onLoadCompleted(null, null, mediaLoadData(128_000, C.TRACK_TYPE_AUDIO))

        bitrateEventsListener.onLoadCompleted(null, null, mediaLoadData(320_000, C.TRACK_TYPE_AUDIO))

        assertEquals(3_320_000, actualBitrate)
    }

    @Test
    fun `Should listening DID_UPDATE_BITRATE on different bitrates`() {
        var numberOfTriggers = 0

        listenObject.listenTo(playback, Event.DID_UPDATE_BITRATE.value) { numberOfTriggers++ }
        bitrateEventsListener.onLoadCompleted(null, null, mediaLoadData(10))
        bitrateEventsListener.onLoadCompleted(null, null, mediaLoadData(40))

        assertEquals(2, numberOfTriggers)
    }

    @Test
    fun `Should trigger DID_UPDATE_BITRATE only for different bitrate`() {
        val bitrate = 10L
        var numberOfTriggers = 0

        listenObject.listenTo(playback, Event.DID_UPDATE_BITRATE.value) { numberOfTriggers++ }

        bitrateEventsListener.onLoadCompleted(null, null, mediaLoadData(bitrate))
        bitrateEventsListener.onLoadCompleted(null, null, mediaLoadData(bitrate))

        assertEquals(1, numberOfTriggers)
    }

    @Test
    fun `Should not trigger DID_UPDATE_BITRATE when track type is different from TRACK_TYPE_DEFAULT or TRACK_TYPE_VIDEO`() {
        var didUpdateBitrateCalled = false
        val bitrate = 40L

        listenObject.listenTo(playback, Event.DID_UPDATE_BITRATE.value) {
            didUpdateBitrateCalled = true
        }
        bitrateEventsListener.onLoadCompleted(null, null, mediaLoadData(bitrate, C.TRACK_TYPE_UNKNOWN))

        assertFalse(didUpdateBitrateCalled)
    }

    @Test
    fun `Should not trigger DID_UPDATE_BITRATE when media load data is null`() {
        val mediaLoadData = null
        var didUpdateBitrateCalled = false

        listenObject.listenTo(playback, Event.DID_UPDATE_BITRATE.value) {
            didUpdateBitrateCalled = true
        }
        bitrateEventsListener.onLoadCompleted(null, null, mediaLoadData)

        assertFalse(didUpdateBitrateCalled)
    }

    @Test
    fun `Should handle wrong time interval exception on add bitrate on history`() {
        timeInNano = -1

        bitrateEventsListener.onLoadCompleted(null, null, mediaLoadData(20L))
    }

    @Test
    fun `Should not trigger DID_UPDATE_BITRATE when video format is null`() {
        val videoFormatMock = null
        val mediaLoadData = MediaSourceEventListener.MediaLoadData(
            0, C.TRACK_TYPE_DEFAULT, videoFormatMock, 0,
            null, 0L, 0L
        )
        var didUpdateBitrateCalled = false

        listenObject.listenTo(playback, Event.DID_UPDATE_BITRATE.value) {
            didUpdateBitrateCalled = true
        }
        bitrateEventsListener.onLoadCompleted(null, null, mediaLoadData)

        assertFalse(didUpdateBitrateCalled)
    }

    @Test
    fun `Should clear averageBitrate`() {
        bitrateEventsListener.onLoadCompleted(null, null, mediaLoadData(3_000_000, C.TRACK_TYPE_VIDEO))
        bitrateEventsListener.onLoadCompleted(null, null, mediaLoadData(128_000, C.TRACK_TYPE_AUDIO))

        bitrateEventsListener.clearHistory()

        assertEquals(0, bitrateEventsListener.averageBitrate())
    }

    private fun mediaLoadData(bitrate: Long, trackType: Int = C.TRACK_TYPE_DEFAULT)
            : MediaSourceEventListener.MediaLoadData {
        val videoFormatMock = Format.createVideoSampleFormat(
            null, null, null, bitrate.toInt(),
            0, 0, 0, 0f, listOf<ByteArray>(), null
        )
        return MediaSourceEventListener.MediaLoadData(
            0, trackType, videoFormatMock,
            0, null, 0L, 0L
        )
    }

    class DummyPlayback : Playback("some-source", null, Options(), "ExoPlayerBitrateLoggerTest-Playback", ExoPlayerPlayback.supportsSource)
}