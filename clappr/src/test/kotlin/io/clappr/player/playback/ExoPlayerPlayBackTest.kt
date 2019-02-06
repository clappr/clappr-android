package io.clappr.player.playback


import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSourceEventListener
import io.clappr.player.BuildConfig
import io.clappr.player.base.BaseObject
import io.clappr.player.base.Options
import io.clappr.player.bitrate.BitrateHistory
import io.clappr.player.shadows.SubtitleViewShadow
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication
import org.robolectric.shadows.ShadowLog
import kotlin.test.assertEquals


@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23), shadows = [ShadowLog::class, SubtitleViewShadow::class])
class ExoPlayerPlaybackTest {

    private lateinit var exoPlayerPlayBack: ExoPlayerPlayback

    @Mock
    lateinit var simpleExoPlayerMock: SimpleExoPlayer

    lateinit var bitrateHistory: BitrateHistory

    @Before
    fun setUp() {
        BaseObject.applicationContext = ShadowApplication.getInstance().applicationContext
        MockitoAnnotations.initMocks(this)

        bitrateHistory = BitrateHistory()
        exoPlayerPlayBack = MockedExoPlayerPlayback(source = "aSource", options = Options(), bitrateHistory = bitrateHistory, simpleExoPlayerMock = simpleExoPlayerMock)
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
        val expectedBitrate = 60

        bitrateHistory.addBitrate(40)
        bitrateHistory.addBitrate(50)
        bitrateHistory.addBitrate(expectedBitrate)

        assertEquals(expectedBitrate, exoPlayerPlayBack.bitrate)
    }

    @Test
    fun shouldReturnLastReportedBitrateAfterOnLoadCompleted() {
        val expectedBitrate = 10
        val videoFormatMock = Format.createVideoSampleFormat(null, null, null, expectedBitrate,
                0, 0, 0, 0f, listOf<ByteArray>(), null)
        val mediaLoadData = MediaSourceEventListener.MediaLoadData(0, C.TRACK_TYPE_DEFAULT, videoFormatMock, 0,
                null, 0L, 0L )

        bitrateHistory.addBitrate(40)
        (exoPlayerPlayBack as MockedExoPlayerPlayback).eventListener.onLoadCompleted(null, null, mediaLoadData)

        assertEquals(expectedBitrate, exoPlayerPlayBack.bitrate)
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

    class MockedExoPlayerPlayback(source: String, options: Options, bitrateHistory: BitrateHistory,
                                      val simpleExoPlayerMock: SimpleExoPlayer? = null) : ExoPlayerPlayback(source = source, options = options, bitrateHistory = bitrateHistory) {
        val eventListener = ExoplayerEventsListener()

        init {
            player = simpleExoPlayerMock
        }
    }
}