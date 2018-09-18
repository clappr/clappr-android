package io.clappr.player.plugin

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import io.clappr.player.BuildConfig
import io.clappr.player.base.*
import io.clappr.player.components.Container
import io.clappr.player.components.Core
import io.clappr.player.components.Playback
import io.clappr.player.components.PlaybackSupportInterface
import io.clappr.player.plugin.Control.MediaControl
import io.clappr.player.plugin.Control.MediaControlPlugin
import io.clappr.player.plugin.Control.MediaControlPlugin.Panel
import io.clappr.player.plugin.Control.MediaControlPlugin.Position
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication
import org.robolectric.shadows.ShadowSystemClock
import org.robolectric.shadows.ShadowView
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [23], shadows = [ShadowSystemClock::class, ShadowView::class])
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
    fun shouldAddMediaControlPluginInCenterPanel() {
        setupFakeMediaControlPlugin(Panel.CENTER, Position.NONE)
        assertMediaControlPanel(mediaControlPlugin.centerPanel, Panel.CENTER, Position.NONE)
    }

    @Test
    fun shouldAddMediaControlPluginInCenterPanelEvenWhenPositionIsLeft() {
        setupFakeMediaControlPlugin(Panel.CENTER, Position.LEFT)
        assertMediaControlPanel(mediaControlPlugin.centerPanel, Panel.CENTER, Position.LEFT)
    }

    @Test
    fun shouldAddMediaControlPluginInCenterPanelEvenWhenPositionIsRight() {
        setupFakeMediaControlPlugin(Panel.CENTER, Position.RIGHT)
        assertMediaControlPanel(mediaControlPlugin.centerPanel, Panel.CENTER, Position.RIGHT)
    }

    @Test
    fun shouldAddMediaControlPluginInBottomPanel() {
        setupFakeMediaControlPlugin(Panel.BOTTOM, Position.NONE)
        assertMediaControlPanel(mediaControlPlugin.bottomPanel, Panel.BOTTOM, Position.NONE)
    }

    @Test
    fun shouldAddMediaControlPluginInBottomLeftPanel() {
        setupFakeMediaControlPlugin(Panel.BOTTOM, Position.LEFT)
        assertMediaControlPanel(mediaControlPlugin.bottomLeftPanel, Panel.BOTTOM, Position.LEFT)
    }

    @Test
    fun shouldAddMediaControlPluginInBottomRightPanel() {
        setupFakeMediaControlPlugin(Panel.BOTTOM, Position.RIGHT)
        assertMediaControlPanel(mediaControlPlugin.bottomRightPanel, Panel.BOTTOM, Position.RIGHT)
    }

    @Test
    fun shouldAddMediaControlPluginInTopPanel() {
        setupFakeMediaControlPlugin(Panel.TOP, Position.NONE)
        assertMediaControlPanel(mediaControlPlugin.topPanel, Panel.TOP, Position.NONE)
    }

    @Test
    fun shouldAddMediaControlPluginInTopLeftPanel() {
        setupFakeMediaControlPlugin(Panel.TOP, Position.LEFT)
        assertMediaControlPanel(mediaControlPlugin.topLeftPanel, Panel.TOP, Position.LEFT)
    }

    @Test
    fun shouldAddMediaControlPluginInTopRightPanel() {
        setupFakeMediaControlPlugin(Panel.TOP, Position.RIGHT)
        assertMediaControlPanel(mediaControlPlugin.topRightPanel, Panel.TOP, Position.RIGHT)
    }

    @Test
    fun shouldNotAddMediaControlPluginWhenPanelIsNone() {
        setupFakeMediaControlPlugin(Panel.NONE, Position.NONE)

        assertTrue(mediaControlPlugin.controlPlugins.size == 1, "Media Control Plugin should be added to Media Control")
        assertTrue(mediaControlPlugin.centerPanel.childCount == 0, "Media Control Plugin should not be added to Center panel in Media Control")
        assertTrue(mediaControlPlugin.topPanel.childCount == 0, "Media Control Plugin should not be added to Top panel in Media Control")
        assertTrue(mediaControlPlugin.topRightPanel.childCount == 0, "Media Control Plugin should not be added to Top Right panel in Media Control")
        assertTrue(mediaControlPlugin.topLeftPanel.childCount == 0, "Media Control Plugin should not be added to Top Left panel in Media Control")
        assertTrue(mediaControlPlugin.bottomPanel.childCount == 0, "Media Control Plugin should not be added to Bottom panel in Media Control")
        assertTrue(mediaControlPlugin.bottomRightPanel.childCount == 0, "Media Control Plugin should not be added to Bottom Right panel in Media Control")
        assertTrue(mediaControlPlugin.bottomLeftPanel.childCount == 0, "Media Control Plugin should not be added to Bottom Left panel in Media Control")
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

    @Test
    fun shouldUpdateInteractionTimeWhenDidUpdateInteractingEventIsCalled() {
        val expectedTime = 1234L

        ShadowSystemClock.setCurrentTimeMillis(expectedTime)

        core.trigger(InternalEvent.DID_UPDATE_INTERACTING.value)

        assertEquals(expectedTime, mediaControlPlugin.lastInteractionTime)
    }

    @Test
    fun shouldUpdateInteractionTimeWhenDidTouchMediaControlEventIsCalled() {
        val expectedTime = 1234L

        ShadowSystemClock.setCurrentTimeMillis(expectedTime)

        core.trigger(InternalEvent.DID_TOUCH_MEDIA_CONTROL.value)

        assertEquals(expectedTime, mediaControlPlugin.lastInteractionTime)
    }

    private fun setupFakeMediaControlPlugin(panel: Panel, position: Position) {
        FakeMediaControlPlugin.currentPanel = panel
        FakeMediaControlPlugin.currentPosition = position

        Loader.registerPlugin(FakeMediaControlPlugin::class)

        core = Core(Loader(), Options())

        mediaControlPlugin = MediaControl(core)
        mediaControlPlugin.render()
    }

    private fun assertMediaControlPanel(layoutPanel: LinearLayout, panel: Panel, position: Position) {
        val plugin = core.plugins.filterIsInstance(FakeMediaControlPlugin::class.java).first()

        assertTrue(mediaControlPlugin.controlPlugins.size == 1, "Media Control Plugin should be added to Media Control")
        assertTrue(layoutPanel.childCount == 1, "Media Control Plugin should be added to $panel panel and $position position in Media Control")
        assertEquals(plugin.view, layoutPanel.getChildAt(0))
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

    class FakeMediaControlPlugin(core: Core) : MediaControlPlugin(core) {
        companion object : NamedType {
            override val name = "fakeMediaControlPlugin"

            var currentPanel: Panel = Panel.NONE
            var currentPosition: Position = Position.NONE
        }

        override var panel: Panel = currentPanel
        override var position: Position = currentPosition
    }
}
