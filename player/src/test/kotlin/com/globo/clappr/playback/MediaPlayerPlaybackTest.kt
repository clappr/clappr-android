package com.globo.clappr.playback

import android.os.Bundle
import android.widget.FrameLayout
import com.globo.clappr.BuildConfig
import com.globo.clappr.base.BaseObject
import com.globo.clappr.base.Callback
import com.globo.clappr.base.ClapprEvent
import com.globo.clappr.components.Playback
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.junit.Ignore
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication
import org.robolectric.shadows.ShadowMediaPlayer
import org.robolectric.shadows.util.DataSource
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23))
open class MediaPlayerPlaybackTest {
    @Before
    fun setup() {
        BaseObject.context = ShadowApplication.getInstance().applicationContext

        ShadowMediaPlayer.addMediaInfo(DataSource.toDataSource("valid"), ShadowMediaPlayer.MediaInfo(10000, -1))
        ShadowMediaPlayer.addException(DataSource.toDataSource("io_invalid"), IOException())
        ShadowMediaPlayer.addException(DataSource.toDataSource("runtime_invalid"), RuntimeException())
    }

    @Ignore("need to mock Surface lifecycle") @Test
    fun shouldTransitionToIdleWithValidMedia() {
        val mpp : MediaPlayerPlayback = MediaPlayerPlayback(source = "valid")
        mpp.view = FrameLayout(BaseObject.context)
        mpp.render()
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
        mpp.view = FrameLayout(BaseObject.context)
        mpp.render()
        mpp.play()

        smp?.invokePreparedListener()

        assertEquals("should transition to PLAYING", Playback.State.PLAYING, mpp.state)
    }

    @Ignore("need to mock Surface lifecycle") @Test()
    fun shouldTriggerPlayEventsWhenPlay() {
        var smp : ShadowMediaPlayer? = null
        ShadowMediaPlayer.setCreateListener { mediaPlayer, shadowMediaPlayer -> smp = shadowMediaPlayer }

        val mpp : MediaPlayerPlayback = MediaPlayerPlayback(source = "valid")

        var willPlayCount = 0
        var playingCount = 0
        mpp.on(ClapprEvent.WILL_PLAY.value, Callback.wrap { bundle: Bundle? -> willPlayCount += 1 })
        mpp.on(ClapprEvent.PLAYING.value, Callback.wrap { bundle: Bundle? -> playingCount += 1 })
        mpp.view = FrameLayout(BaseObject.context)
        mpp.render()
        mpp.play()

        assertEquals("will play triggered", 1, willPlayCount)

        smp?.invokePreparedListener()

        assertEquals("will play triggered", 1, willPlayCount)
        assertEquals("will play triggered", 1, playingCount)
    }

}