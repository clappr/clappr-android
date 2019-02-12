package io.clappr.player.playback


import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.source.MediaSourceEventListener
import io.clappr.player.BuildConfig
import io.clappr.player.base.BaseObject
import io.clappr.player.base.Event
import io.clappr.player.base.Options
import io.clappr.player.bitrate.BitrateHistory
import io.clappr.player.shadows.SubtitleViewShadow
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication
import org.robolectric.shadows.ShadowLog
import kotlin.test.assertEquals


@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23), shadows = [ShadowLog::class, SubtitleViewShadow::class])
class ExoPlayerPlaybackTest {

    private lateinit var mockedExoPlayerPlayBack: MockedExoPlayerPlayback

    lateinit var bitrateHistory: BitrateHistory

    @Before
    fun setUp() {
        BaseObject.applicationContext = ShadowApplication.getInstance().applicationContext
        MockitoAnnotations.initMocks(this)

        bitrateHistory = BitrateHistory()
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

        mockedExoPlayerPlayBack.play()

        mockedExoPlayerPlayBack.exoplayerBitrateLogger?.onLoadCompleted(null, null, addBitrateMediaLoadData(10))
        mockedExoPlayerPlayBack.exoplayerBitrateLogger?.onLoadCompleted(null, null, addBitrateMediaLoadData(20))
        mockedExoPlayerPlayBack.exoplayerBitrateLogger?.onLoadCompleted(null, null, addBitrateMediaLoadData(40))

        assertEquals(expectedBitrate, mockedExoPlayerPlayBack.bitrate)
    }

    @Test
    fun shouldTriggerEventDidUpdateBitrate() {
        val listenObject = BaseObject()
        val exoPlayback = mockedExoPlayerPlayBack
        var didUpdateBitrateCalled = false

        listenObject.listenTo(exoPlayback, Event.DID_UPDATE_BITRATE.value) { didUpdateBitrateCalled = true }

        exoPlayback.play()

        exoPlayback.exoplayerBitrateLogger?.onLoadCompleted(null, null, addBitrateMediaLoadData(10))

        Assert.assertTrue("Did update bitrate not triggered", didUpdateBitrateCalled)
    }

    @Test
    fun shouldListeningOnDidUpdateDifferentBitrate() {
        val listenObject = BaseObject()
        val exoPlayback = mockedExoPlayerPlayBack

        var numberOfTriggers = 0
        listenObject.listenTo(exoPlayback, Event.DID_UPDATE_BITRATE.value) { numberOfTriggers++ }

        exoPlayback.play()

        exoPlayback.exoplayerBitrateLogger?.onLoadCompleted(null, null, addBitrateMediaLoadData(10))
        exoPlayback.exoplayerBitrateLogger?.onLoadCompleted(null, null, addBitrateMediaLoadData(40))

        Assert.assertEquals("trigger", 2, numberOfTriggers)
    }

    @Test
    fun shouldStopListeningOnDestroy2() {
        val listenObject = BaseObject()
        val exoPlayback = mockedExoPlayerPlayBack


        var numberOfTriggers = 0
        listenObject.listenTo(exoPlayback, Event.DID_UPDATE_BITRATE.value) { numberOfTriggers++ }

        exoPlayback.play()

        exoPlayback.exoplayerBitrateLogger?.onLoadCompleted(null, null, addBitrateMediaLoadData(10))
        exoPlayback.exoplayerBitrateLogger?.onLoadCompleted(null, null, addBitrateMediaLoadData(10))

        Assert.assertEquals("trigger", 1, numberOfTriggers)
    }

    @Test
    fun shouldReturnLastReportedBitrateAfterOnLoadCompletede() {
        val listenObject = BaseObject()
        val exoPlayback = mockedExoPlayerPlayBack
        var didUpdateBitrateCalled = false

        listenObject.listenTo(exoPlayback, Event.DID_UPDATE_BITRATE.value) { didUpdateBitrateCalled = true }

        Assert.assertFalse("Did update bitrate triggered", didUpdateBitrateCalled)
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