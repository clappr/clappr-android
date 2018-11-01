package io.clappr.player.plugin.control

import android.view.View
import io.clappr.player.BuildConfig
import io.clappr.player.base.*
import io.clappr.player.components.Container
import io.clappr.player.components.Core
import io.clappr.player.components.Playback
import io.clappr.player.components.PlaybackSupportInterface
import io.clappr.player.plugin.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [23])
class PlayButtonTest {

    private lateinit var core: Core
    private lateinit var container: Container

    private lateinit var playButton: PlayButton

    @Before
    fun setup() {
        BaseObject.context = ShadowApplication.getInstance().applicationContext

        container = Container(Loader(), Options())
        core = Core(Loader(), Options())

        playButton = PlayButton(core)

        core.activeContainer = container
        container.playback = FakePlayback()
    }

    @Test
    fun shouldNotHavePlaybackListenersWhenInit() {
        playButton = PlayButton(core)

        assertTrue(playButton.playbackListenerIds.size == 0,
                "Playback listeners should not be registered")
    }

    @Test
    fun shouldBindPlaybackListenersWhenDidChangeActivePlaybackEventIsTriggered() {
        playButton.playbackListenerIds.clear()

        container.playback = FakePlayback()

        assertTrue(playButton.playbackListenerIds.size > 0,
                "Playback listeners should be registered")
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
    fun shouldRemoveAllPlaybackListenersBeforeBindNewOnesWhenDidChangeActivePlaybackEventIsTriggered() {
        val expectedAmountOfListener = 5

        container.playback = FakePlayback()

        assertEquals(expectedAmountOfListener, playButton.playbackListenerIds.size)
    }

    @Test
    fun shouldRemoveAllPlaybackListenersWhenPluginIsDestroyed() {
        playButton.destroy()

        assertTrue(playButton.playbackListenerIds.size == 0,
                "Playback listeners should not be registered")
    }

    @Test
    fun shouldPlayButtonBeHiddenWhenStalledOnRender() {
        setupViewVisible(playButton)
        setupFakePlayback(state = Playback.State.STALLED)

        playButton.render()

        assertHiddenView(playButton)
    }

    @Test
    fun shouldPlayButtonBeHiddenWhileStallingWhenEventStalledIsTriggered() {
        setupViewVisible(playButton)
        setupFakePlayback(state = Playback.State.STALLED)

        core.activePlayback?.trigger(Event.STALLED.value)

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
    fun shouldPlayIconBeVisibleWhileNotPlayingOrStalledOnRender() {
        setupViewHidden(playButton)
        playButton.view.isSelected = true
        setupFakePlayback(state = Playback.State.PAUSED)

        playButton.render()

        assertVisibleView(playButton)
        assertFalse(playButton.view.isSelected)
    }

    @Test
    fun shouldPlayIconBeVisibleWhileNotPlayingOrStalledWhenEventDidPauseIsTriggered() {
        assertPlayIconWhenPlaybackIsNotPlayingOrStalledAndEventIsTriggered(Event.DID_PAUSE.value)
    }

    @Test
    fun shouldPlayIconBeVisibleWhileNotPlayingOrStalledWhenEventDidStopIsTriggered() {
        assertPlayIconWhenPlaybackIsNotPlayingOrStalledAndEventIsTriggered(Event.DID_STOP.value)
    }

    @Test
    fun shouldPlayIconBeVisibleWhileNotPlayingOrStalledWhenEventDidCompleteIsTriggered() {
        assertPlayIconWhenPlaybackIsNotPlayingOrStalledAndEventIsTriggered(Event.DID_COMPLETE.value)
    }

    @Test
    fun shouldTriggerDidTouchMediaControlWhenPlayButtonIsClicked() {
        var eventTriggered = false
        core.on(InternalEvent.DID_TOUCH_MEDIA_CONTROL.value, Callback.wrap { eventTriggered = true })

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

    private fun assertPlayIconWhenPlaybackIsNotPlayingOrStalledAndEventIsTriggered(event: String) {
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

    class FakePlayback(source: String = "aSource", mimeType: String? = null, options: Options = Options()) : Playback(source, mimeType, options) {
        companion object : PlaybackSupportInterface {
            override val name: String = "fakePlayback"
            override fun supportsSource(source: String, mimeType: String?) = true
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