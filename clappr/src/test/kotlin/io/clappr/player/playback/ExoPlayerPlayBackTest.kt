package io.clappr.player.playback


import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.SimpleExoPlayer
import com.nhaarman.mockito_kotlin.whenever
import io.clappr.player.BuildConfig
import io.clappr.player.base.BaseObject
import io.clappr.player.base.Options
import io.clappr.player.bitrate.BitrateHistory
import io.clappr.player.components.PlaybackEntry
import io.clappr.player.components.PlaybackSupportCheck
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


    @Before
    fun setUp() {
        BaseObject.applicationContext = ShadowApplication.getInstance().applicationContext
        MockitoAnnotations.initMocks(this)

        exoPlayerPlayBack = MockedExoPlayerPlayback(source = "aSource", options = Options(), simpleExoPlayerMock = simpleExoPlayerMock)
    }

    @Test
    fun shouldKeepOriginalBitrateValueWhenPlayerNull() {

        assertEquals(0, exoPlayerPlayBack.bitrate)
    }

    @Test
    fun shouldHaveBitrate() {
        val videoFormatMock = Format.createVideoSampleFormat(null, null, null, 10,
                0, 0, 0, 0f, listOf<ByteArray>(), null)

        val expectedBitrate = 10

        whenever(simpleExoPlayerMock.videoFormat).thenReturn(videoFormatMock)

        assertEquals(expectedBitrate, exoPlayerPlayBack.bitrate)
    }

    @Test
    fun shouldKeepOriginalBitrateValueWhenVideoFormatNull() {
        whenever(simpleExoPlayerMock.videoFormat).thenReturn(null)

        assertEquals(0, exoPlayerPlayBack.bitrate)
    }


    class MockedExoPlayerPlayback(source: String, options: Options,
                                  var simpleExoPlayerMock: SimpleExoPlayer? = null) : ExoPlayerPlayback(source = source, options = options, bitrateHistory = BitrateHistory()) {

      init {
            player = simpleExoPlayerMock
        }

        companion object {
            const val name: String = "fakeExoPlayerPlayback"

            const val validSource = "valid-source.mp4"

            val supportsSource: PlaybackSupportCheck = { source, _ -> source == validSource }

            val entry = PlaybackEntry(
                    name, supportsSource,
                    factory = { source, _, options -> MockedExoPlayerPlayback(source, options = options) })
        }
    }
}