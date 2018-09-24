package io.clappr.player.plugin.Control

import android.view.View
import io.clappr.player.BuildConfig
import io.clappr.player.base.BaseObject
import io.clappr.player.base.Event
import io.clappr.player.base.InternalEvent
import io.clappr.player.base.Options
import io.clappr.player.components.Core
import io.clappr.player.plugin.Loader
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

    @Before
    fun setUp() {
        BaseObject.context = ShadowApplication.getInstance().applicationContext
        core = Core(Loader(), Options())
        fullscreenButton = FullscreenButton(core)
        fullscreenButton.render()
    }

    @Test
    fun shouldEnterFullscreen() {
        triggerPlaying()

        triggerDidTouchMediaControl()
        triggerRequestFullscreen()
        triggerDidEnterFullscreen()

        assertEquals(View.VISIBLE, fullscreenButton.view.visibility, "Fullscreen button should be VISIBLE")
        assertFalse(fullscreenButton.view.isSelected)
    }

    @Test
    fun shouldExitFullscreen() {
        fullscreenButton.core.fullscreenState = Core.FullscreenState.FULLSCREEN
        fullscreenButton.render()

        triggerPlaying()

        triggerDidTouchMediaControl()
        triggerExitFullscreen()
        triggerDidExitFullscreen()

        assertEquals(View.VISIBLE, fullscreenButton.view.visibility, "Fullscreen button should be VISIBLE")
        assertTrue(fullscreenButton.view.isSelected)
    }


    @Test
    fun shouldShowFullScreenButtonWhenTouchMediaControl() {
        triggerDidTouchMediaControl()
        assertEquals(View.VISIBLE, fullscreenButton.view.visibility)
    }

    @Test
    fun shouldShowFullScreenButtonWhenPlay() {
        triggerPlaying()
        assertEquals(View.VISIBLE, fullscreenButton.view.visibility)
    }

    @Test
    fun shouldShowFullScreenButtonWhenChangePlayback() {
        triggerDidChangePlayback()
        assertEquals(View.VISIBLE, fullscreenButton.view.visibility)
    }

    @Test
    fun shouldHideFullScreenButtonWhenComplete() {
        triggerPlaying()
        triggerDidComplete()
        assertEquals(View.VISIBLE, fullscreenButton.view.visibility)
    }

    private fun triggerDidEnterFullscreen() {
        core.trigger(InternalEvent.DID_ENTER_FULLSCREEN.value)
    }

    private fun triggerRequestFullscreen() {
        core.trigger(Event.REQUEST_FULLSCREEN.value)
    }

    private fun triggerExitFullscreen() {
        core.trigger(Event.EXIT_FULLSCREEN.value)
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
}