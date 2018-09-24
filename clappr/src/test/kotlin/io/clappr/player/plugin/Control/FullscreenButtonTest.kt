package io.clappr.player.plugin.Control

import android.view.View
import io.clappr.player.BuildConfig
import io.clappr.player.base.BaseObject
import io.clappr.player.base.Event
import io.clappr.player.base.InternalEvent
import io.clappr.player.base.Options
import io.clappr.player.components.Container
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
        container.playback = MediaControlTest.FakePlayback()
    }

    private fun setupFakeMediaControlPlugin(panel: MediaControl.Plugin.Panel, position: MediaControl.Plugin.Position) {
        MediaControlTest.FakePlugin.currentPanel = panel
        MediaControlTest.FakePlugin.currentPosition = position

        Loader.registerPlugin(MediaControlTest.FakePlugin::class)

        core = Core(Loader(), Options())

        fullscreenButton = FullscreenButton(core)
        fullscreenButton.render()
    }

    @Test
    fun shouldEnterFullscreen() {
        setupFakeMediaControlPlugin(MediaControl.Plugin.Panel.BOTTOM, MediaControl.Plugin.Position.RIGHT)
        triggerPlaying()

        triggerDidTouchMediaControl()
        triggerRequestFullscreen()
        triggerDidEnterFullscreen()

        kotlin.test.assertEquals(View.VISIBLE, fullscreenButton.view.visibility, "Fullscreen button should be VISIBLE")
        kotlin.test.assertFalse(fullscreenButton.view.isSelected)
    }

    @Test
    fun shouldExitFullscreen() {
        setupFakeMediaControlPlugin(MediaControl.Plugin.Panel.BOTTOM, MediaControl.Plugin.Position.RIGHT)
        fullscreenButton.core.fullscreenState = Core.FullscreenState.FULLSCREEN
        fullscreenButton.render()

        triggerPlaying()

        triggerDidTouchMediaControl()
        triggerExitFullscreen()
        triggerDidExitFullscreen()

        kotlin.test.assertEquals(View.VISIBLE, fullscreenButton.view.visibility, "Fullscreen button should be VISIBLE")
        kotlin.test.assertTrue(fullscreenButton.view.isSelected)
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

    private fun triggerPlaying() {
        core.trigger(Event.PLAYING.value)
    }
}