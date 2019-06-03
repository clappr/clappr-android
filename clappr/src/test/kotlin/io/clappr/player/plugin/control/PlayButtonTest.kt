package io.clappr.player.plugin.control

import android.view.View
import androidx.test.core.app.ApplicationProvider
import io.clappr.player.base.BaseObject
import io.clappr.player.base.Event
import io.clappr.player.base.InternalEvent
import io.clappr.player.base.Options
import io.clappr.player.components.Container
import io.clappr.player.components.Core
import io.clappr.player.components.Playback
import io.clappr.player.components.PlaybackSupportCheck
import io.clappr.player.plugin.assertHiddenView
import io.clappr.player.plugin.assertVisibleView
import io.clappr.player.plugin.setupViewHidden
import io.clappr.player.plugin.setupViewVisible
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class PlayButtonTest {

    private lateinit var core: Core
    private lateinit var container: Container

    private lateinit var playButton: PlayButton

    @Before
    fun setup() {
        BaseObject.applicationContext = ApplicationProvider.getApplicationContext()

        container = Container(Options())
        core = Core(Options())

        playButton = PlayButton(core)

        core.activeContainer = container
        container.playback = FakePlayback()
    }

    @Test
    fun shouldStopListeningOldPlaybackWhenDidChangePlaybackEventIsTriggered() {
        val oldPlayback = container.playback

        assertEquals(View.GONE, playButton.view.visibility)

        val newPlayback = FakePlayback()
        container.playback = newPlayback

        newPlayback.playbackState = Playback.State.IDLE
        oldPlayback?.trigger(Event.DID_COMPLETE.value)

        assertEquals(View.GONE, playButton.view.visibility)
    }

    @Test
    fun shouldStopListeningOldPlaybackAfterDestroy() {
        setupViewHidden(playButton)

        val oldPlayback = container.playback

        playButton.destroy()

        oldPlayback?.trigger(Event.PLAYING.value)

        assertHiddenView(playButton)
    }

    @Test
    fun shouldPlayButtonBeHiddenWhenStallingOnRender() {
        setupViewVisible(playButton)
        setupFakePlayback(state = Playback.State.STALLING)

        playButton.render()

        assertHiddenView(playButton)
    }

    @Test
    fun shouldPlayButtonBeHiddenWhileStallingWhenEventStallingIsTriggered() {
        setupViewVisible(playButton)
        setupFakePlayback(state = Playback.State.STALLING)

        core.activePlayback?.trigger(Event.STALLING.value)

        assertHiddenView(playButton)
    }

    @Test
    fun shouldPauseIconBeVisibleWhilePlayingAndCanPauseOnRender() {
        setupViewHidden(playButton)
        setupFakePlayback(state = Playback.State.PLAYING, canPause = true)

        playButton.render()

        assertVisibleView(playButton)
        assertTrue(playButton.view.isSelected)
    }

    @Test
    fun shouldPauseIconBeVisibleWhilePlayingAndCanPauseWhenEventPlayingIsTriggered() {
        setupViewHidden(playButton)
        setupFakePlayback(state = Playback.State.PLAYING, canPause = true)

        core.activePlayback?.trigger(Event.PLAYING.value)

        assertVisibleView(playButton)
        assertTrue(playButton.view.isSelected)
    }

    @Test
    fun shouldHidePlayButtonWhilePlayingAndCanNotPauseOnRender() {
        setupViewVisible(playButton)
        setupFakePlayback(state = Playback.State.PLAYING, canPause = false)

        playButton.render()

        assertHiddenView(playButton)
    }

    @Test
    fun shouldPlayIconBeVisibleWhileNotPlayingOrStallingOnRender() {
        setupViewHidden(playButton)
        playButton.view.isSelected = true
        setupFakePlayback(state = Playback.State.PAUSED)

        playButton.render()

        assertVisibleView(playButton)
        assertFalse(playButton.view.isSelected)
    }

    @Test
    fun shouldPlayIconBeVisibleWhileNotPlayingOrStallingWhenEventDidPauseIsTriggered() {
        assertPlayIconWhenPlaybackIsNotPlayingOrStallingAndEventIsTriggered(Event.DID_PAUSE.value)
    }

    @Test
    fun shouldPlayIconBeVisibleWhileNotPlayingOrStallingWhenEventDidStopIsTriggered() {
        assertPlayIconWhenPlaybackIsNotPlayingOrStallingAndEventIsTriggered(Event.DID_STOP.value)
    }

    @Test
    fun shouldPlayIconBeVisibleWhileNotPlayingOrStallingWhenEventDidCompleteIsTriggered() {
        assertPlayIconWhenPlaybackIsNotPlayingOrStallingAndEventIsTriggered(Event.DID_COMPLETE.value)
    }

    @Test
    fun shouldTriggerDidTouchMediaControlWhenPlayButtonIsClicked() {
        var eventTriggered = false
        core.on(InternalEvent.DID_TOUCH_MEDIA_CONTROL.value) { eventTriggered = true }

        playButton.onClick()

        assertTrue(eventTriggered)
    }

    @Test
    fun shouldCallPlaybackPauseWhenPlayButtonIsClickedAndStateIsPlayingAndCanPause() {
        setupFakePlayback(state = Playback.State.PLAYING, canPause = true)

        playButton.onClick()

        val playbackWasPaused = (container.playback as FakePlayback).pauseWasCalled
        assertTrue(playbackWasPaused)
    }

    @Test
    fun shouldCallPlaybackStopWhenPlayButtonIsClickedAndStateIsPlayingAndCanNotPause() {
        setupFakePlayback(state = Playback.State.PLAYING, canPause = false)

        playButton.onClick()

        val playbackWasStopped = (container.playback as FakePlayback).stopWasCalled
        assertTrue(playbackWasStopped)
    }

    @Test
    fun shouldCallPlaybackPlayWhenPlayButtonIsClickedAndStateIsNotPlayingAndCanPlay() {
        setupFakePlayback(state = Playback.State.PAUSED, canPlay = true)

        playButton.onClick()

        val playbackWasPlayed = (container.playback as FakePlayback).playWasCalled
        assertTrue(playbackWasPlayed)
    }

    private fun assertPlayIconWhenPlaybackIsNotPlayingOrStallingAndEventIsTriggered(event: String) {
        setupViewHidden(playButton)
        playButton.view.isSelected = true
        setupFakePlayback(state = Playback.State.PAUSED)

        core.activePlayback?.trigger(event)

        assertVisibleView(playButton)
        assertFalse(playButton.view.isSelected)
    }

    private fun setupFakePlayback(state: Playback.State = Playback.State.PLAYING,
                                  canPause: Boolean = false,
                                  canPlay: Boolean = false) {
        (container.playback as FakePlayback).apply {
            playbackState = state
            playbackCanPause = canPause
            playbackCanPlay = canPlay
        }
    }

    class FakePlayback(source: String = "aSource", mimeType: String? = null, options: Options = Options()) : Playback(source, mimeType, options, name, supportsSource) {
        companion object {
            const val name = "fakePlayback"
            val supportsSource: PlaybackSupportCheck = { _, _ -> true }
        }

        var playbackState = State.PLAYING
        var playbackCanPause = false
        var playbackCanPlay = false

        var pauseWasCalled = false
        var stopWasCalled = false
        var playWasCalled = false

        override val state: State
            get() = playbackState

        override val canPause: Boolean
            get() = playbackCanPause

        override val canPlay: Boolean
            get() = playbackCanPlay

        override fun pause(): Boolean {
            pauseWasCalled = true
            return super.pause()
        }

        override fun stop(): Boolean {
            stopWasCalled = true
            return super.stop()
        }

        override fun play(): Boolean {
            playWasCalled = true
            return super.play()
        }
    }
}