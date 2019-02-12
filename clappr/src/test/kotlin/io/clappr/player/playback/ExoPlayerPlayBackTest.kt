package io.clappr.player.playback

import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.source.MediaSourceEventListener
import io.clappr.player.BuildConfig
import io.clappr.player.base.BaseObject
import io.clappr.player.base.Event
import io.clappr.player.base.EventData
import io.clappr.player.base.Options
import io.clappr.player.bitrate.BitrateHistory
import io.clappr.player.shadows.SubtitleViewShadow
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication
import org.robolectric.shadows.ShadowLog

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23), shadows = [ShadowLog::class, SubtitleViewShadow::class])
class ExoPlayerPlaybackTest {

    private lateinit var mockedExoPlayerPlayBack: MockedExoPlayerPlayback

    lateinit var bitrateHistory: BitrateHistory
    lateinit var listenObject: BaseObject

    @Before
    fun setUp() {
        BaseObject.applicationContext = ShadowApplication.getInstance().applicationContext
        MockitoAnnotations.initMocks(this)

        bitrateHistory = BitrateHistory()
        listenObject = BaseObject()
        mockedExoPlayerPlayBack = MockedExoPlayerPlayback(source = "aSource", options = Options(), bitrateHistory = bitrateHistory)
    }

    @Test
    fun shouldReturnZeroBitrateWhenHistoryIsEmpty() {
        assertEquals(0, mockedExoPlayerPlayBack.bitrate)
    }

    @Test
    fun shouldReturnZeroAverageBitrateWhenHistoryIsEmpty() {
        assertEquals(0, mockedExoPlayerPlayBack.avgBitrate)
    }

    @Test
    fun shouldReturnLastReportedBitrate() {
        val expectedBitrate = 40

        mockedExoPlayerPlayBack.exoplayerBitrateLogger?.onLoadCompleted(null, null, addBitrateMediaLoadData(10))
        mockedExoPlayerPlayBack.exoplayerBitrateLogger?.onLoadCompleted(null, null, addBitrateMediaLoadData(20))
        mockedExoPlayerPlayBack.exoplayerBitrateLogger?.onLoadCompleted(null, null, addBitrateMediaLoadData(expectedBitrate))

        assertEquals(expectedBitrate, mockedExoPlayerPlayBack.bitrate)
    }

    @Test
    fun shouldTriggerDidUpdateBitrate() {
        val expectedBitrate = 40
        var actualBitrate = 0.0

        listenObject.listenTo(mockedExoPlayerPlayBack, Event.DID_UPDATE_BITRATE.value) {
            actualBitrate = it?.getInt(EventData.BITRATE.value)?.toDouble() ?: 0.0
        }
        mockedExoPlayerPlayBack.exoplayerBitrateLogger?.onLoadCompleted(null, null, addBitrateMediaLoadData(expectedBitrate))

        assertEquals(expectedBitrate.toDouble(), actualBitrate, 0.0)
    }

    @Test
    fun shouldListeningDidUpdateOnDifferentBitrates() {
        var numberOfTriggers = 0

        listenObject.listenTo(mockedExoPlayerPlayBack, Event.DID_UPDATE_BITRATE.value) { numberOfTriggers++ }
        mockedExoPlayerPlayBack.exoplayerBitrateLogger?.onLoadCompleted(null, null, addBitrateMediaLoadData(10))
        mockedExoPlayerPlayBack.exoplayerBitrateLogger?.onLoadCompleted(null, null, addBitrateMediaLoadData(40))

        assertEquals(2, numberOfTriggers)
    }

    @Test
    fun shouldTriggerDidUpdateBitrateOnlyForDifferentBitrate() {
        val bitrate = 10
        var numberOfTriggers = 0

        listenObject.listenTo(mockedExoPlayerPlayBack, Event.DID_UPDATE_BITRATE.value) { numberOfTriggers++ }
        mockedExoPlayerPlayBack.exoplayerBitrateLogger?.onLoadCompleted(null, null, addBitrateMediaLoadData(bitrate))
        mockedExoPlayerPlayBack.exoplayerBitrateLogger?.onLoadCompleted(null, null, addBitrateMediaLoadData(bitrate))

        assertEquals(1, numberOfTriggers)
    }

    @Test
    fun shouldNotTriggerDidUpdateBitrateWhenTrackTypeDifferentDefaultOrVideo() {
        var didUpdateBitrateCalled = false
        val bitrate = 40
        val videoFormatMock = Format.createVideoSampleFormat(null, null, null, bitrate,
                0, 0, 0, 0f, listOf<ByteArray>(), null)
        val mediaLoadData = MediaSourceEventListener.MediaLoadData(0, C.TRACK_TYPE_AUDIO, videoFormatMock, 0,
                null, 0L, 0L)


        listenObject.listenTo(mockedExoPlayerPlayBack, Event.DID_UPDATE_BITRATE.value) { didUpdateBitrateCalled = true }
        mockedExoPlayerPlayBack.exoplayerBitrateLogger?.onLoadCompleted(null, null, mediaLoadData)

        assertFalse( didUpdateBitrateCalled)
    }

    @Test
    fun shouldNotTriggerDidUpdateBitrateWhenMediaLoadDataNull() {
        val mediaLoadData = null
        var didUpdateBitrateCalled = false

        listenObject.listenTo(mockedExoPlayerPlayBack, Event.DID_UPDATE_BITRATE.value) { didUpdateBitrateCalled = true }
        mockedExoPlayerPlayBack.exoplayerBitrateLogger?.onLoadCompleted(null, null, mediaLoadData)

        assertFalse(didUpdateBitrateCalled)
    }

    @Test
    fun shouldNotTriggerDidUpdateBitrateWhenVideoFormatNull() {
        val videoFormatMock = null
        val mediaLoadData = MediaSourceEventListener.MediaLoadData(0, C.TRACK_TYPE_DEFAULT, videoFormatMock, 0,
                null, 0L, 0L)
        var didUpdateBitrateCalled = false

        listenObject.listenTo(mockedExoPlayerPlayBack, Event.DID_UPDATE_BITRATE.value) { didUpdateBitrateCalled = true }
        mockedExoPlayerPlayBack.exoplayerBitrateLogger?.onLoadCompleted(null, null, mediaLoadData)

        assertFalse(didUpdateBitrateCalled)
    }

    @Test
    fun shouldTriggerDidUpdateBitrateWhenTrackTypeDefault() {
        var didUpdateBitrateCalled = false
        val bitrate = 40
        val videoFormatMock = Format.createVideoSampleFormat(null, null, null, bitrate,
                0, 0, 0, 0f, listOf<ByteArray>(), null)
        val mediaLoadData = MediaSourceEventListener.MediaLoadData(0, C.TRACK_TYPE_DEFAULT, videoFormatMock, 0,
                null, 0L, 0L)

        listenObject.listenTo(mockedExoPlayerPlayBack, Event.DID_UPDATE_BITRATE.value) { didUpdateBitrateCalled = true }
        mockedExoPlayerPlayBack.exoplayerBitrateLogger?.onLoadCompleted(null, null, mediaLoadData)

        assertTrue(didUpdateBitrateCalled)
    }

    @Test
    fun shouldTriggerDidUpdateBitrateWhenTrackTypeVideo() {
        var didUpdateBitrateCalled = false
        val bitrate = 40
        val videoFormatMock = Format.createVideoSampleFormat(null, null, null, bitrate,
                0, 0, 0, 0f, listOf<ByteArray>(), null)
        val mediaLoadData = MediaSourceEventListener.MediaLoadData(0, C.TRACK_TYPE_VIDEO, videoFormatMock, 0,
                null, 0L, 0L)

        listenObject.listenTo(mockedExoPlayerPlayBack, Event.DID_UPDATE_BITRATE.value) { didUpdateBitrateCalled = true }
        mockedExoPlayerPlayBack.exoplayerBitrateLogger?.onLoadCompleted(null, null, mediaLoadData)

        assertTrue( didUpdateBitrateCalled)
    }

    @Test
    fun shouldReturnAverageBitrate() {
        val expectedAverageBitrate = 109L

        bitrateHistory.addBitrate(90, 2)
        bitrateHistory.addBitrate(100, 17)
        bitrateHistory.addBitrate(110, 31)

        assertEquals(expectedAverageBitrate, mockedExoPlayerPlayBack.avgBitrate)
    }

    @Test
    fun shouldResetBitrateHistoryAfterStopping() {
        bitrateHistory.addBitrate(90, 2)
        bitrateHistory.addBitrate(100, 17)
        bitrateHistory.addBitrate(110, 31)

        mockedExoPlayerPlayBack.stop()

        assertEquals(0, mockedExoPlayerPlayBack.bitrate)
    }

    @Test
    fun shouldResetAverageBitrateHistoryAfterStopping() {
        bitrateHistory.addBitrate(90, 2)
        bitrateHistory.addBitrate(100, 17)
        bitrateHistory.addBitrate(110, 31)

        mockedExoPlayerPlayBack.stop()

        assertEquals(0, mockedExoPlayerPlayBack.avgBitrate)
    }

    private fun addBitrateMediaLoadData(bitrate: Int): MediaSourceEventListener.MediaLoadData {
        val videoFormatMock = Format.createVideoSampleFormat(null, null, null, bitrate,
                0, 0, 0, 0f, listOf<ByteArray>(), null)
        val mediaLoadData = MediaSourceEventListener.MediaLoadData(0, C.TRACK_TYPE_DEFAULT, videoFormatMock, 0,
                null, 0L, 0L)

        return mediaLoadData
    }

    class MockedExoPlayerPlayback(source: String, mimeType: String? = null, options: Options = Options(), bitrateHistory: BitrateHistory) :
            ExoPlayerPlayback(source = source, options = options, bitrateHistory = bitrateHistory) {

        val exoplayerBitrateLogger: ExoplayerBitrateLogger?

        init {
            configureTrackSelector()
            exoplayerBitrateLogger = trackSelector?.let { ExoplayerBitrateLogger(it) }
        }
    }
}