package io.clappr.player.plugin.control

import android.view.View
import io.clappr.player.BuildConfig
import io.clappr.player.base.*
import io.clappr.player.components.Container
import io.clappr.player.components.Core
import io.clappr.player.components.Playback
import io.clappr.player.components.PlaybackSupportInterface
import io.clappr.player.plugin.Loader
import io.clappr.player.plugin.UIPlugin
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication
import org.robolectric.shadows.ShadowSystemClock
import org.robolectric.shadows.ShadowView
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [23], shadows = [ShadowSystemClock::class, ShadowView::class])
class FullscreenButtonTest {

    private lateinit var fullscreenButton: FullscreenButton
    private lateinit var core: Core
    private lateinit var container: Container

    @Before
    fun setUp() {
        BaseObject.context = ShadowApplication.getInstance().applicationContext
        container = Container(Loader(), Options())
        core = Core(Loader(), Options())
        fullscreenButton = FullscreenButton(core)

        core.activeContainer = container
        container.playback = FakePlayback()
    }

    @Test
    fun shouldFullscreenButtonBeVisibleAndNotSelectedWhenEnterFullScreen() {
        triggerDidEnterFullscreen()

        assertEquals(View.VISIBLE, fullscreenButton.view.visibility)
        assertFalse(fullscreenButton.view.isSelected)
    }

    @Test
    fun shouldFullscreenButtonBeGoneWhenPlaybackIsIdleOnRender() {
        fullscreenButton.render()
        container.playback = FakePlayback(stateFake = Playback.State.IDLE)

        assertEquals(View.GONE, fullscreenButton.view.visibility)
        assertEquals(UIPlugin.Visibility.HIDDEN, fullscreenButton.visibility)
    }

    @Test
    fun shouldFullscreenButtonBeGoneWhenPlaybackIsNoneOnRender() {
        fullscreenButton.render()
        container.playback = FakePlayback(stateFake = Playback.State.NONE)

        assertEquals(View.GONE, fullscreenButton.view.visibility)
        assertEquals(UIPlugin.Visibility.HIDDEN, fullscreenButton.visibility)
    }

    @Test
    fun shouldHaveZeroRightPadding() {
        fullscreenButton.render()

        assertEquals(0, fullscreenButton.view.paddingRight)
    }

    @Test
    fun shouldFullscreenButtonBeGoneWhenPlaybackIsIdleOnPlayingEvent() {
        container.playback = FakePlayback(stateFake = Playback.State.IDLE)

        triggerPlaying()

        assertEquals(View.GONE, fullscreenButton.view.visibility)
        assertEquals(UIPlugin.Visibility.HIDDEN, fullscreenButton.visibility)
    }

    @Test
    fun shouldFullscreenButtonBeGoneWhenPlaybackIsNoneOnPlayingEvent() {
        container.playback = FakePlayback(stateFake = Playback.State.NONE)

        triggerPlaying()

        assertEquals(View.GONE, fullscreenButton.view.visibility)
        assertEquals(UIPlugin.Visibility.HIDDEN, fullscreenButton.visibility)
    }

    @Test
    fun shouldFullscreenBeGoneWhenPlaybackIsIdle() {
        container.playback = FakePlayback(stateFake = Playback.State.IDLE)
        assertEquals(View.GONE, fullscreenButton.view.visibility)
    }

    @Test
    fun shouldFullscreenBeGoneWhenPlaybackIsNone() {
        container.playback = FakePlayback(stateFake = Playback.State.NONE)
        assertEquals(View.GONE, fullscreenButton.view.visibility)
    }

    @Test
    fun shouldFullscreenBeVisibleWhenPlaybackIsNotIdle() {
        assertEquals(View.VISIBLE, fullscreenButton.view.visibility)
    }

    @Test
    fun shouldFullscreenButtonBeVisibleAndSelectedWhenExitFullScreen() {
        fullscreenButton.core.fullscreenState = Core.FullscreenState.FULLSCREEN
        fullscreenButton.render()

        triggerDidExitFullscreen()

        assertEquals(View.VISIBLE, fullscreenButton.view.visibility)
        assertTrue(fullscreenButton.view.isSelected)
    }

    @Test
    fun shouldEnterFullscreenOnClick() {
        var enterFullScreenWasCalled = false

        core.on(Event.REQUEST_FULLSCREEN.value, Callback.wrap { enterFullScreenWasCalled = true })
        core.fullscreenState = Core.FullscreenState.EMBEDDED
        fullscreenButton.onClick()

        assertTrue(enterFullScreenWasCalled)
    }

    @Test
    fun shouldExitFullscreenOnClick() {
        var exitFullScreenWasCalled = false

        core.on(Event.EXIT_FULLSCREEN.value, Callback.wrap { exitFullScreenWasCalled = true })
        core.fullscreenState = Core.FullscreenState.FULLSCREEN
        fullscreenButton.onClick()

        assertTrue(exitFullScreenWasCalled)
    }

    @Test
    fun shouldShowFullScreenButtonWhenTriggerDidTouchMediaControlAndPlaybackIsNotIdle() {
        triggerDidTouchMediaControl()
        assertEquals(View.VISIBLE, fullscreenButton.view.visibility)
    }

    @Test
    fun shouldShowFullScreenButtonWhenTriggerPlayingAndPlaybackIsNotIdle() {
        triggerPlaying()
        assertEquals(View.VISIBLE, fullscreenButton.view.visibility)
    }

    @Test
    fun shouldShowFullScreenButtonWhenTriggerChangePlaybackAndPlaybackIsNotIdle() {
        triggerDidChangePlayback()
        assertEquals(View.VISIBLE, fullscreenButton.view.visibility)
    }

    @Test
    fun shouldHideFullScreenButtonWhenDidCompleteAndPlaybackIsNotIdle() {
        triggerDidComplete()
        assertEquals(View.GONE, fullscreenButton.view.visibility)
    }

    @Test
    fun shouldHideFullScreenButtonWhenTriggerTouchMediaControlAndPlaybackIsIdle() {
        container.playback = FakePlayback(stateFake = Playback.State.IDLE)

        triggerDidTouchMediaControl()
        assertEquals(View.GONE, fullscreenButton.view.visibility)
    }

    @Test
    fun shouldHideFullScreenButtonWhenPlayingAndPlaybackIsIdle() {
        container.playback = FakePlayback(stateFake = Playback.State.IDLE)

        triggerPlaying()
        assertEquals(View.GONE, fullscreenButton.view.visibility)
    }

    @Test
    fun shouldHideFullScreenButtonWhenChangePlaybackAndPlaybackIsIdle() {
        container.playback = FakePlayback(stateFake = Playback.State.IDLE)

        triggerDidChangePlayback()
        assertEquals(View.GONE, fullscreenButton.view.visibility)
    }


    private fun triggerDidEnterFullscreen() {
        core.trigger(InternalEvent.DID_ENTER_FULLSCREEN.value)
    }

    private fun triggerDidTouchMediaControl() {
        core.trigger(InternalEvent.DID_TOUCH_MEDIA_CONTROL.value)
    }

    private fun triggerDidExitFullscreen() {
        core.trigger(InternalEvent.DID_EXIT_FULLSCREEN.value)
    }

    private fun triggerDidComplete() {
        core.activePlayback?.trigger(Event.DID_COMPLETE.value)
    }

    private fun triggerDidChangePlayback() {
        core.activePlayback?.trigger(InternalEvent.DID_CHANGE_PLAYBACK.value)
    }

    private fun triggerPlaying() {
        core.trigger(Event.PLAYING.value)
    }

    class FakePlayback(source: String = "aSource", mimeType: String? = null, options: Options = Options(), var stateFake: State = State.PLAYING) : Playback(source, mimeType, options) {
        companion object : PlaybackSupportInterface {
            override val name: String = "fakePlayback"
            override fun supportsSource(source: String, mimeType: String?) = true
        }

        override val state: State
            get() = stateFake
    }
}