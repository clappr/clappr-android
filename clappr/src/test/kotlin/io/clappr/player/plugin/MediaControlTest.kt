package io.clappr.player.plugin

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import io.clappr.player.BuildConfig
import io.clappr.player.base.*
import io.clappr.player.components.Container
import io.clappr.player.components.Core
import io.clappr.player.components.Playback
import io.clappr.player.components.PlaybackSupportInterface
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [23])
class MediaControlTest {

    private lateinit var mediaControlPlugin: MediaControl
    private lateinit var core: Core
    private lateinit var container: Container

    @Before
    fun setUp() {
        BaseObject.context = ShadowApplication.getInstance().applicationContext
        container = Container(Loader(), Options())

        core = Core(Loader(), Options())

        mediaControlPlugin = MediaControl(core)

        core.activeContainer = container
        container.playback = FakePlayback()
    }

    @Test
    fun shouldSetModalPanelVisibilityToInvisibleWhenMediaControlIsRendered() {
        mediaControlPlugin.render()

        assertEquals(View.INVISIBLE, mediaControlPlugin.modalPanel.visibility, "Modal Panel should be INVISIBLE")
    }

    @Test
    fun shouldOpenModalWhenReceiveOpenModalPanelEvent() {
        mediaControlPlugin.render()

        triggerOpenModalPanelEvent()

        assertEquals(View.VISIBLE, mediaControlPlugin.modalPanel.visibility, "Modal Panel should be VISIBLE")
    }

    @Test
    fun shouldCloseModalWhenReceiveCloseModalPanelEvent() {
        triggerOpenModalPanelEvent()

        triggerCloseModalPanelEvent()

        assertEquals(View.INVISIBLE, mediaControlPlugin.modalPanel.visibility, "Modal Panel should be INVISIBLE")
    }

    @Test
    fun shouldSendDidCloseModalWhenModalPanelWasClosed() {
        var didCloseModalPanelWasCalled = false

        core.on(InternalEvent.DID_CLOSE_MODAL_PANEL.value, Callback.wrap { didCloseModalPanelWasCalled = true })

        triggerCloseModalPanelEvent()

        assertTrue(didCloseModalPanelWasCalled)
    }

    @Test
    fun shouldSendDidOpenModalPanelEventWhenModalPanelWasOpened() {
        var didOpenModalPanelEventWasSent = false

        var resultBundle: Bundle? = null

        core.on(InternalEvent.DID_OPEN_MODAL_PANEL.value, Callback.wrap { bundle ->
            didOpenModalPanelEventWasSent = true
            resultBundle = bundle
        })

        triggerOpenModalPanelEvent()

        assertTrue(didOpenModalPanelEventWasSent, "Should trigger ${InternalEvent.DID_OPEN_MODAL_PANEL.value} when Modal Panel was open ")

        val map = (resultBundle?.getSerializable(MediaControl.modalPanelViewKey) as? HashMap<*, *>)
        var modalView: ViewGroup? = null

        map?.let {
            modalView = it[MediaControl.modalPanelViewKey] as ViewGroup
        }

        assertEquals(mediaControlPlugin.modalPanel, modalView)
    }

    @Test
    fun shouldHideControlsPanelOnOpenModalEvent() {
        triggerOpenModalPanelEvent()

        assertEquals(View.INVISIBLE, mediaControlPlugin.controlsPanel.visibility, "Should make controls panel invisible when modal panel open")
    }

    @Test
    fun shouldShowControlsPanelOnCloseModalEvent() {
        triggerOpenModalPanelEvent()

        triggerCloseModalPanelEvent()

        assertEquals(View.VISIBLE, mediaControlPlugin.controlsPanel.visibility, "Should make control panel visible when modal panel close")
    }

    @Test
    fun shouldHideForegroundsControlsPanelOnOpenModalEvent() {
        triggerOpenModalPanelEvent()

        assertEquals(View.INVISIBLE, mediaControlPlugin.foregroundControlsPanel.visibility, "Should make foreground controls panel invisible when panel modal open")
    }

    @Test
    fun shouldShowMediaControlWhenPlaybackIsPaused() {
        core.activePlayback?.trigger(Event.PLAYING.value)

        core.activePlayback?.trigger(Event.DID_PAUSE.value)

        assertEquals(UIPlugin.Visibility.VISIBLE, mediaControlPlugin.visibility)
    }

    @Test
    fun shouldInitializeModalPanelInvisibleWhenMediaControlPluginIsCreated() {
        assertEquals(View.INVISIBLE, mediaControlPlugin.modalPanel.visibility)
    }

    @Test
    fun shouldShowOnlyModalPanelWhenPlaybackIsPausedAndPanelModalIsOpen() {
        core.activePlayback?.trigger(Event.PLAYING.value)

        triggerOpenModalPanelEvent()

        core.activePlayback?.trigger(Event.DID_PAUSE.value)

        assertEquals(View.INVISIBLE, mediaControlPlugin.foregroundControlsPanel.visibility)
        assertEquals(View.INVISIBLE, mediaControlPlugin.controlsPanel.visibility)
        assertEquals(View.VISIBLE, mediaControlPlugin.modalPanel.visibility)
    }

    @Test
    fun shouldShowForegroundsControlsPanelOnCloseModalEvent() {
        triggerOpenModalPanelEvent()

        triggerCloseModalPanelEvent()

        assertEquals(mediaControlPlugin.foregroundControlsPanel.visibility, View.VISIBLE, "Should make foreground controls panel visible when panel modal close")
    }

    @Test
    fun shouldDisableMediaControlOnEvent() {
        assertTrue(mediaControlPlugin.isEnabled, "Media control should be enabled by default")

        triggerDisableMediaControlEvent()

        assertFalse(mediaControlPlugin.isEnabled, "Media control should be disabled after Container event")
    }

    @Test
    fun shouldEnableMediaControlOnEvent() {
        mediaControlPlugin.state = Plugin.State.DISABLED

        triggerEnableMediaControlEvent()

        assertTrue(mediaControlPlugin.isEnabled, "Media control should be enabled after Container event")
    }

    @Test
    fun shouldUnregisterContainerEventsOnContainerChange() {
        mediaControlPlugin.state = Plugin.State.DISABLED

        val newContainer = Container(Loader(), Options())
        core.activeContainer = newContainer

        triggerEnableMediaControlEvent()
        assertFalse(mediaControlPlugin.isEnabled, "Media control should ignore Container event")

        newContainer.trigger(InternalEvent.ENABLE_MEDIA_CONTROL.value)
        assertTrue(mediaControlPlugin.isEnabled, "Media control should handle new Container event")
    }

    @Test
    fun shouldUnregisterPlaybackEventsOnPlaybackChange() {
        val oldPlayback = container.playback!!
        val newPlayback = FakePlayback()

        container.playback = newPlayback

        oldPlayback.trigger(Event.PLAYING.value)
        oldPlayback.trigger(Event.DID_PAUSE.value)
        assertEquals(UIPlugin.Visibility.HIDDEN, mediaControlPlugin.visibility, "Media control should ignore Playback event")

        newPlayback.trigger(Event.PLAYING.value)
        newPlayback.trigger(Event.DID_PAUSE.value)
        assertEquals(UIPlugin.Visibility.VISIBLE, mediaControlPlugin.visibility, "Media control should handle Playback event")
    }

    private fun triggerOpenModalPanelEvent() {
        core.trigger(InternalEvent.OPEN_MODAL_PANEL.value)
    }

    private fun triggerCloseModalPanelEvent() {
        core.trigger(InternalEvent.CLOSE_MODAL_PANEL.value)
    }

    private fun triggerDisableMediaControlEvent() {
        container.trigger(InternalEvent.DISABLE_MEDIA_CONTROL.value)
    }

    private fun triggerEnableMediaControlEvent() {
        container.trigger(InternalEvent.ENABLE_MEDIA_CONTROL.value)
    }

    class FakePlayback(source: String = "aSource", mimeType: String? = null, options: Options = Options()) : Playback(source, mimeType, options) {
        companion object : PlaybackSupportInterface {
            override val name: String = "fakePlayback"
            override fun supportsSource(source: String, mimeType: String?) = true
        }
    }

}
