package com.globo.clappr.playback

import android.os.Bundle
import com.globo.clappr.BuildConfig
import com.globo.clappr.base.BaseObject
import com.globo.clappr.base.Callback
import com.globo.clappr.base.ClapprEvent
import com.globo.clappr.components.Playback
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication
import org.robolectric.shadows.ShadowMediaPlayer
import org.robolectric.shadows.util.DataSource
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23))
open class MediaPlayerPlaybackTest {
    var numberOfCalls = 0
    val callback = Callback.wrap { bundle: Bundle? ->
        numberOfCalls += 1
    }

    @Before
    fun setup() {
        BaseObject.context = ShadowApplication.getInstance().applicationContext

        ShadowMediaPlayer.addMediaInfo(DataSource.toDataSource("valid"), ShadowMediaPlayer.MediaInfo(10000, -1))
        ShadowMediaPlayer.addException(DataSource.toDataSource("io_invalid"), IOException())
        ShadowMediaPlayer.addException(DataSource.toDataSource("runtime_invalid"), RuntimeException())

        numberOfCalls = 0
    }

    @Test
    fun shouldTransitionToIdleWithValidMedia() {
        val mpp : MediaPlayerPlayback = MediaPlayerPlayback(source = "valid")
        assertEquals("should transition to IDLE", Playback.State.IDLE, mpp.state)
    }

    @Test(expected = IOException::class)
    fun shouldTrhrowWithInvalidMedia() {
        val mpp : MediaPlayerPlayback = MediaPlayerPlayback(source = "io_invalid")
    }

    @Test(expected = RuntimeException::class)
    fun shouldTrhrowWithUnsupportedMedia() {
        val mpp : MediaPlayerPlayback = MediaPlayerPlayback(source = "runtime_invalid")
    }

    @Test
    fun shouldTransitionToPlayingWhenPlay() {
        var smp : ShadowMediaPlayer? = null
        ShadowMediaPlayer.setCreateListener { mediaPlayer, shadowMediaPlayer -> smp = shadowMediaPlayer }

        val mpp : MediaPlayerPlayback = MediaPlayerPlayback(source = "valid")
        mpp.play()

        smp?.invokePreparedListener()

        assertEquals("should transition to PLAYING", Playback.State.PLAYING, mpp.state)
    }

    @Test
    fun shouldTriggerPlayEventsWhenPlay() {
        var smp : ShadowMediaPlayer? = null
        ShadowMediaPlayer.setCreateListener { mediaPlayer, shadowMediaPlayer -> smp = shadowMediaPlayer }

        val mpp : MediaPlayerPlayback = MediaPlayerPlayback(source = "valid")
        mpp.on(ClapprEvent.WILL_PLAY.value, callback)
        mpp.on(ClapprEvent.PLAYING.value, callback)
        mpp.play()

        assertEquals("will play triggered", 1, numberOfCalls)

        smp?.invokePreparedListener()

        assertEquals("will play triggered", 2, numberOfCalls)
    }

}