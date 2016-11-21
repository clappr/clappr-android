package com.globo.clappr.playback

import android.app.Activity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.FrameLayout
import com.globo.clappr.BuildConfig
import com.globo.clappr.base.BaseObject
import com.globo.clappr.base.Callback
import com.globo.clappr.base.ClapprEvent
import com.globo.clappr.base.Event
import com.globo.clappr.base.Options
import com.globo.clappr.components.Playback
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.junit.Ignore
import org.objectweb.asm.tree.analysis.Frame
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication
import org.robolectric.shadows.ShadowMediaPlayer
import org.robolectric.shadows.ShadowSurfaceView
import org.robolectric.shadows.util.DataSource
import org.robolectric.util.ActivityController
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23))
open class MediaPlayerPlaybackTest {
    lateinit var mediaPlayerPlayback : MediaPlayerPlayback

    @Before
    fun setup() {
        BaseObject.context = ShadowApplication.getInstance().applicationContext

        ShadowMediaPlayer.addMediaInfo(DataSource.toDataSource("valid"), ShadowMediaPlayer.MediaInfo())
        mediaPlayerPlayback = MediaPlayerPlayback(source = "valid")
        createSurface(mediaPlayerPlayback)
        mediaPlayerPlayback.render()
    }

    private fun createSurface(mpp: MediaPlayerPlayback) {
        val ssv = Shadows.shadowOf(mpp.view) as ShadowSurfaceView
        val fsh = ssv.fakeSurfaceHolder
        for (callback in fsh.callbacks) callback.surfaceCreated(fsh)
    }

    @Test
    fun shouldTransitionToIdleWithValidMedia() {
        val mpp : MediaPlayerPlayback = MediaPlayerPlayback(source = "valid")
        assertEquals("should not transition from NONE", Playback.State.NONE, mpp.state)
        createSurface(mpp)
        assertEquals("should transition to IDLE", Playback.State.IDLE, mpp.state)
    }

    @Test
    fun shouldTrhrowWithInvalidMedia() {
        ShadowMediaPlayer.addException(DataSource.toDataSource("io_invalid"), IOException())
        val mpp : MediaPlayerPlayback = MediaPlayerPlayback(source = "io_invalid")
        assertEquals("should transition to ERROR", mpp.state, Playback.State.ERROR)
    }

    @Test
    fun shouldTrhrowWithUnsupportedMedia() {
        ShadowMediaPlayer.addException(DataSource.toDataSource("runtime_invalid"), RuntimeException())
        val mpp : MediaPlayerPlayback = MediaPlayerPlayback(source = "runtime_invalid")
        assertEquals("should transition to ERROR", mpp.state, Playback.State.ERROR)
    }

    @Test
    fun shouldTransitionToPlayingWhenPlay() {
        mediaPlayerPlayback.play()

        assertEquals("should transition to PLAYING", Playback.State.PLAYING, mediaPlayerPlayback.state)
    }

    @Test()
    fun shouldTriggerPlayEventsWhenPlay() {
        var willPlayCount = 0
        var playingCount = 0
        mediaPlayerPlayback.on(Event.WILL_PLAY.value, Callback.wrap { bundle: Bundle? ->
            willPlayCount += 1
            assertEquals("playing trigerred", 0, playingCount)
        })
        mediaPlayerPlayback.on(Event.PLAYING.value, Callback.wrap { bundle: Bundle? -> playingCount += 1 })

        mediaPlayerPlayback.play()

        assertEquals("will play triggered", 1, willPlayCount)
        assertEquals("playing not triggered", 1, playingCount)
    }
}
