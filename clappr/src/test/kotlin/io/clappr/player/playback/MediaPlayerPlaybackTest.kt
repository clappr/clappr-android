package com.globo.clappr.playback

import android.media.MediaPlayer
import android.os.Bundle
import io.clappr.player.BuildConfig
import io.clappr.player.base.BaseObject
import io.clappr.player.base.Callback
import io.clappr.player.base.Event
import io.clappr.player.base.Options
import io.clappr.player.components.Playback
import io.clappr.player.playback.MediaPlayerPlayback
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.shadows.ShadowApplication
import org.robolectric.shadows.ShadowMediaPlayer
import org.robolectric.shadows.ShadowSurfaceView
import org.robolectric.shadows.util.DataSource
import org.robolectric.util.Scheduler
import java.io.IOException

@Implements(MediaPlayer::class)
open class MediaPlayerTestShadow : ShadowMediaPlayer() {
    private var bufferingUpdateListener: MediaPlayer.OnBufferingUpdateListener? = null

    @Implementation
    fun setOnBufferingUpdateListener(listener: MediaPlayer.OnBufferingUpdateListener) {
        bufferingUpdateListener = listener
    }

    fun invokeBufferingUpdateListener(player: MediaPlayer, percentage: Int) {
        bufferingUpdateListener?.onBufferingUpdate(player, percentage)
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23), shadows = arrayOf(MediaPlayerTestShadow::class))

open class MediaPlayerPlaybackTest {
    lateinit var mediaPlayerPlayback : MediaPlayerPlayback
    lateinit var scheduler: Scheduler
    lateinit var validMedia: ShadowMediaPlayer.MediaInfo

    @Before
    fun setup() {
        BaseObject.context = ShadowApplication.getInstance().applicationContext

        ShadowMediaPlayer.setCreateListener { mediaPlayer, shadowMediaPlayer ->
            // Disabling invalid state emulation as it is not compatible with releases newer than KitKat (MediaTimeProvider)
            shadowMediaPlayer.invalidStateBehavior = ShadowMediaPlayer.InvalidStateBehavior.SILENT
        }

        scheduler = Robolectric.getForegroundThreadScheduler()

        validMedia = ShadowMediaPlayer.MediaInfo(1000,0)
        ShadowMediaPlayer.addMediaInfo(DataSource.toDataSource("valid"), validMedia)

        mediaPlayerPlayback = MediaPlayerPlayback(source = "valid", options = Options())
        createSurface(mediaPlayerPlayback)
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
    fun shouldOnlyPlayOnValidState() {
        val mpp : MediaPlayerPlayback = MediaPlayerPlayback(source = "valid")

        var callbackCalled = false
        mpp.on(Event.WILL_PLAY.value, Callback.wrap { bundle: Bundle? -> callbackCalled = true })
        mpp.on(Event.PLAYING.value, Callback.wrap { bundle: Bundle? -> callbackCalled = true })

        var result = mpp.play()
        assertFalse("play allowed in invalid state", result)
        assertFalse("play callback called in invalid state", callbackCalled)

        result = mediaPlayerPlayback.play()
        assertTrue("play not allowed in valid state", result)
    }

    @Test
    fun shouldOnlyAllowPlayInValidState() {
        val mpp : MediaPlayerPlayback = MediaPlayerPlayback(source = "valid")

        assertFalse("play allowed in invalid state", mpp.canPlay)
        assertTrue("play not allowed in valid state", mediaPlayerPlayback.canPlay)
    }

    @Test
    fun shouldTransitionToPlayingWhenPlay() {
        mediaPlayerPlayback.play()

        assertEquals("should transition to PLAYING", Playback.State.PLAYING, mediaPlayerPlayback.state)
    }

    @Test
    fun shouldTriggerPlayEventsWhenPlay() {
        var willPlayCount = 0
        var playingCount = 0
        mediaPlayerPlayback.on(Event.WILL_PLAY.value, Callback.wrap { bundle: Bundle? ->
            willPlayCount += 1
            assertEquals("playing trigerred", 0, playingCount)
        })
        mediaPlayerPlayback.on(Event.PLAYING.value, Callback.wrap { bundle: Bundle? -> playingCount += 1 })

        mediaPlayerPlayback.play()

        scheduler.advanceBy(200)
        assertEquals("will play triggered", 1, willPlayCount)
        assertEquals("playing not triggered", 1, playingCount)
    }

    @Test
    fun shouldTriggerEventOnCompletion() {
        var callbackCalled = false
        mediaPlayerPlayback.on(Event.DID_COMPLETE.value, Callback.wrap { bundle: Bundle? -> callbackCalled = true })

        mediaPlayerPlayback.play()
        assertFalse("complete called", callbackCalled)
        assertEquals("not playing", Playback.State.PLAYING, mediaPlayerPlayback.state)

        scheduler.advanceBy(50)
        assertFalse("complete called", callbackCalled)
        assertEquals("not playing", Playback.State.PLAYING, mediaPlayerPlayback.state)

        scheduler.advanceBy(950)
        assertTrue("complete not called", callbackCalled)
    }

    @Test
    fun shouldStopAllInteractionsOnError() {
        var callbackCalled = false
        mediaPlayerPlayback.on(Event.ERROR.value, Callback.wrap { bundle: Bundle? -> callbackCalled = true })

        validMedia.scheduleErrorAtOffset(400, -1, -2)

        assertTrue("play not allowed", mediaPlayerPlayback.canPlay)
        assertTrue("seek not allowed", mediaPlayerPlayback.canSeek)
        assertTrue("pause not allowed", mediaPlayerPlayback.canPause)
        assertFalse("stop allowed before play", mediaPlayerPlayback.canStop)

        mediaPlayerPlayback.play()
        assertFalse("error callback called", callbackCalled)
        assertTrue("play not allowed", mediaPlayerPlayback.canPlay)
        assertTrue("stop not allowed", mediaPlayerPlayback.canStop)
        assertTrue("seek not allowed", mediaPlayerPlayback.canSeek)
        assertTrue("pause not allowed", mediaPlayerPlayback.canPause)

        scheduler.advanceBy(200)
        assertFalse("error callback called", callbackCalled)
        assertTrue("play not allowed", mediaPlayerPlayback.canPlay)
        assertTrue("stop not allowed", mediaPlayerPlayback.canStop)
        assertTrue("seek not allowed", mediaPlayerPlayback.canSeek)
        assertTrue("pause not allowed", mediaPlayerPlayback.canPause)

        scheduler.advanceBy(200)
        assertTrue("error callback not called", callbackCalled)
        assertFalse("play allowed", mediaPlayerPlayback.canPlay)
        assertFalse("play method allowed", mediaPlayerPlayback.play())
        assertFalse("stop allowed", mediaPlayerPlayback.canStop)
        assertFalse("stop method allowed", mediaPlayerPlayback.stop())
        assertFalse("seek allowed", mediaPlayerPlayback.canSeek)
        assertFalse("seek method allowed", mediaPlayerPlayback.seek(0))
        assertFalse("pause allowed", mediaPlayerPlayback.canPause)
        assertFalse("pause method allowed", mediaPlayerPlayback.pause())
    }

    @Test
    fun shouldTriggerStalledEvents() {
        var stalledCallbackCalled = false
        mediaPlayerPlayback.on(Event.STALLED.value, Callback.wrap { bundle: Bundle? -> stalledCallbackCalled = true })
        var playingCallbackCalled = false
        mediaPlayerPlayback.on(Event.PLAYING.value, Callback.wrap { bundle: Bundle? -> playingCallbackCalled = true })

        validMedia.scheduleBufferUnderrunAtOffset(150, 100)

        mediaPlayerPlayback.play()
        assertFalse("stalled callback called", stalledCallbackCalled)

        scheduler.advanceBy(100)
        assertFalse("stalled callback called", stalledCallbackCalled)

        playingCallbackCalled = false
        scheduler.advanceBy(50)
        assertTrue("stalled callback not called", stalledCallbackCalled)
        assertFalse("playing callback called", playingCallbackCalled)

        scheduler.advanceBy(100)
        assertTrue("playing callback not called", playingCallbackCalled)
    }

    @Test
    fun shouldRetunrConsistentDurationAndPostion() {
        val mpp = MediaPlayerPlayback(source = "")
        assertEquals("valid duration", Double.NaN, mpp.duration, 0.0)
        assertEquals("valid position", Double.NaN, mpp.position, 0.0)


        scheduler.advanceBy(100)
        assertEquals("valid duration", Double.NaN, mediaPlayerPlayback.duration, 0.0)
        assertEquals("valid position", Double.NaN, mediaPlayerPlayback.position, 0.0)

        mediaPlayerPlayback.play()
        assertEquals("valid duration", 1.0, mediaPlayerPlayback.duration, 0.0)
        assertEquals("valid position", 0.0, mediaPlayerPlayback.position, 0.0)

        scheduler.advanceBy(500)
        assertEquals("valid duration", 1.0, mediaPlayerPlayback.duration, 0.0)
        assertEquals("valid position", 0.5, mediaPlayerPlayback.position, 0.0)

        scheduler.advanceBy(499)
        assertEquals("valid duration", 1.0, mediaPlayerPlayback.duration, 0.0)
        assertEquals("valid position", 0.999, mediaPlayerPlayback.position, 0.0)

        scheduler.advanceBy(1)
        assertEquals("valid duration", Double.NaN, mediaPlayerPlayback.duration, 0.0)
        assertEquals("valid position", Double.NaN, mediaPlayerPlayback.position, 0.0)

        scheduler.advanceBy(1)
        assertEquals("valid duration", Double.NaN, mediaPlayerPlayback.duration, 0.0)
        assertEquals("valid position", Double.NaN, mediaPlayerPlayback.position, 0.0)
    }

    @Test
    fun shouldHandleAutoplay() {
        val mpp = MediaPlayerPlayback(source = "valid", options = Options())
        createSurface(mpp)
        mpp.render()

        assertEquals("not playing", Playback.State.PLAYING, mpp.state)
    }

    @Test
    fun shouldHandleBufferingUpdate() {
        var bufferPercentage = Double.NaN
        mediaPlayerPlayback.on(Event.BUFFER_UPDATE.value, Callback.wrap { bundle: Bundle? -> bufferPercentage = bundle!!.getDouble("percentage") })

        validMedia.scheduleEventAtOffset(100, { mp, smp -> (smp as MediaPlayerTestShadow).invokeBufferingUpdateListener(mp, 10)})

        assertEquals("buffer update triggered", Double.NaN, bufferPercentage, 0.0)

        mediaPlayerPlayback.play()

        scheduler.advanceBy(99)
        assertEquals("buffer update triggered", Double.NaN, bufferPercentage, 0.0)

        scheduler.advanceBy(1)
        assertEquals("buffer update triggered", 10.0, bufferPercentage, 0.0)
    }
}