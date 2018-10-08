package io.clappr.player.plugin.control

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
import io.clappr.player.plugin.Loader
import io.clappr.player.plugin.Plugin
import io.clappr.player.plugin.UIPlugin
import io.clappr.player.plugin.control.MediaControl.Plugin.Panel
import io.clappr.player.plugin.control.MediaControl.Plugin.Position
import org.junit.After
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

    private lateinit var mediaControl: MediaControl
    private lateinit var core: Core
    private lateinit var container: Container

    @Before
    fun setUp() {
        BaseObject.context = ShadowApplication.getInstance().applicationContext
        container = Container(Loader(), Options())

        core = Core(Loader(), Options())

        mediaControl = MediaControl(core)

        core.activeContainer = container
        container.playback = FakePlayback()
    }

    @After
    fun tearDwon() {
        Loader.clearPlugins()
        Loader.clearPlaybacks()
    }

    @Test
    fun shouldAddMediaControlPluginInCenterPanel() {
        setupFakeMediaControlPlugin(Panel.CENTER, Position.NONE)
        assertMediaControlPanel(mediaControl.centerPanel, Panel.CENTER, Position.NONE)
    }

    @Test
    fun shouldAddMediaControlPluginInCenterPanelEvenWhenPositionIsLeft() {
        setupFakeMediaControlPlugin(Panel.CENTER, Position.LEFT)
        assertMediaControlPanel(mediaControl.centerPanel, Panel.CENTER, Position.LEFT)
    }

    @Test
    fun shouldAddMediaControlPluginInCenterPanelEvenWhenPositionIsRight() {
        setupFakeMediaControlPlugin(Panel.CENTER, Position.RIGHT)
        assertMediaControlPanel(mediaControl.centerPanel, Panel.CENTER, Position.RIGHT)
    }

    @Test
    fun shouldAddMediaControlPluginInBottomPanel() {
        setupFakeMediaControlPlugin(Panel.BOTTOM, Position.NONE)
        assertMediaControlPanel(mediaControl.bottomPanel, Panel.BOTTOM, Position.NONE)
    }

    @Test
    fun shouldAddMediaControlPluginInBottomLeftPanel() {
        setupFakeMediaControlPlugin(Panel.BOTTOM, Position.LEFT)
        assertMediaControlPanel(mediaControl.bottomLeftPanel, Panel.BOTTOM, Position.LEFT)
    }

    @Test
    fun shouldAddMediaControlPluginInBottomRightPanel() {
        setupFakeMediaControlPlugin(Panel.BOTTOM, Position.RIGHT)
        assertMediaControlPanel(mediaControl.bottomRightPanel, Panel.BOTTOM, Position.RIGHT)
    }

    @Test
    fun shouldAddMediaControlPluginInTopPanel() {
        setupFakeMediaControlPlugin(Panel.TOP, Position.NONE)
        assertMediaControlPanel(mediaControl.topPanel, Panel.TOP, Position.NONE)
    }

    @Test
    fun shouldAddMediaControlPluginInTopLeftPanel() {
        setupFakeMediaControlPlugin(Panel.TOP, Position.LEFT)
        assertMediaControlPanel(mediaControl.topLeftPanel, Panel.TOP, Position.LEFT)
    }

    @Test
    fun shouldAddMediaControlPluginInTopRightPanel() {
        setupFakeMediaControlPlugin(Panel.TOP, Position.RIGHT)
        assertMediaControlPanel(mediaControl.topRightPanel, Panel.TOP, Position.RIGHT)
    }

    @Test
    fun shouldNotAddMediaControlPluginWhenPanelIsNone() {
        setupFakeMediaControlPlugin(Panel.NONE, Position.NONE)

        assertTrue(mediaControl.controlPlugins.size == 1, "Media Control Plugin should be added to Media Control")
        assertTrue(mediaControl.centerPanel.childCount == 0, "Media Control Plugin should not be added to Center panel in Media Control")
        assertTrue(mediaControl.topPanel.childCount == 0, "Media Control Plugin should not be added to Top panel in Media Control")
        assertTrue(mediaControl.topRightPanel.childCount == 0, "Media Control Plugin should not be added to Top Right panel in Media Control")
        assertTrue(mediaControl.topLeftPanel.childCount == 0, "Media Control Plugin should not be added to Top Left panel in Media Control")
        assertTrue(mediaControl.bottomPanel.childCount == 0, "Media Control Plugin should not be added to Bottom panel in Media Control")
        assertTrue(mediaControl.bottomRightPanel.childCount == 0, "Media Control Plugin should not be added to Bottom Right panel in Media Control")
        assertTrue(mediaControl.bottomLeftPanel.childCount == 0, "Media Control Plugin should not be added to Bottom Left panel in Media Control")
    }

    @Test
    fun shouldSetModalPanelVisibilityToInvisibleWhenMediaControlIsRendered() {
        mediaControl.render()

        assertEquals(View.INVISIBLE, mediaControl.modalPanel.visibility, "Modal Panel should be INVISIBLE")
    }

    @Test
    fun shouldOpenModalWhenReceiveOpenModalPanelEvent() {
        mediaControl.render()

        triggerOpenModalPanelEvent()

        assertEquals(View.VISIBLE, mediaControl.modalPanel.visibility, "Modal Panel should be VISIBLE")
    }

    @Test
    fun shouldCloseModalWhenReceiveCloseModalPanelEvent() {
        triggerOpenModalPanelEvent()

        triggerCloseModalPanelEvent()

        assertEquals(View.INVISIBLE, mediaControl.modalPanel.visibility, "Modal Panel should be INVISIBLE")
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

        assertEquals(mediaControl.modalPanel, modalView)
    }

    @Test
    fun shouldHideControlsPanelOnOpenModalEvent() {
        triggerOpenModalPanelEvent()

        assertEquals(View.INVISIBLE, mediaControl.controlsPanel.visibility, "Should make controls panel invisible when modal panel open")
    }

    @Test
    fun shouldShowControlsPanelOnCloseModalEvent() {
        triggerOpenModalPanelEvent()

        triggerCloseModalPanelEvent()

        assertEquals(View.VISIBLE, mediaControl.controlsPanel.visibility, "Should make control panel visible when modal panel close")
    }

    @Test
    fun shouldHideForegroundsControlsPanelOnOpenModalEvent() {
        triggerOpenModalPanelEvent()

        assertEquals(View.INVISIBLE, mediaControl.foregroundControlsPanel.visibility, "Should make foreground controls panel invisible when panel modal open")
    }

    @Test
    fun shouldShowMediaControlWhenPlaybackIsPaused() {
        core.activePlayback?.trigger(Event.PLAYING.value)

        core.activePlayback?.trigger(Event.DID_PAUSE.value)

        assertEquals(UIPlugin.Visibility.VISIBLE, mediaControl.visibility)
    }

    @Test
    fun shouldInitializeModalPanelInvisibleWhenMediaControlPluginIsCreated() {
        assertEquals(View.INVISIBLE, mediaControl.modalPanel.visibility)
    }

    @Test
    fun shouldShowOnlyModalPanelWhenPlaybackIsPausedAndPanelModalIsOpen() {
        core.activePlayback?.trigger(Event.PLAYING.value)

        triggerOpenModalPanelEvent()

        core.activePlayback?.trigger(Event.DID_PAUSE.value)

        assertEquals(View.INVISIBLE, mediaControl.foregroundControlsPanel.visibility)
        assertEquals(View.INVISIBLE, mediaControl.controlsPanel.visibility)
        assertEquals(View.VISIBLE, mediaControl.modalPanel.visibility)
    }

    @Test
    fun shouldShowForegroundsControlsPanelOnCloseModalEvent() {
        triggerOpenModalPanelEvent()

        triggerCloseModalPanelEvent()

        assertEquals(mediaControl.foregroundControlsPanel.visibility, View.VISIBLE, "Should make foreground controls panel visible when panel modal close")
    }

    @Test
    fun shouldDisableMediaControlOnEvent() {
        assertTrue(mediaControl.isEnabled, "Media control should be enabled by default")

        triggerDisableMediaControlEvent()

        assertFalse(mediaControl.isEnabled, "Media control should be disabled after Container event")
    }

    @Test
    fun shouldEnableMediaControlOnEvent() {
        mediaControl.state = Plugin.State.DISABLED

        triggerEnableMediaControlEvent()

        assertTrue(mediaControl.isEnabled, "Media control should be enabled after Container event")
    }

    @Test
    fun shouldUnregisterContainerEventsOnContainerChange() {
        mediaControl.state = Plugin.State.DISABLED

        val newContainer = Container(Loader(), Options())
        core.activeContainer = newContainer

        triggerEnableMediaControlEvent()
        assertFalse(mediaControl.isEnabled, "Media control should ignore Container event")

        newContainer.trigger(InternalEvent.ENABLE_MEDIA_CONTROL.value)
        assertTrue(mediaControl.isEnabled, "Media control should handle new Container event")
    }

    @Test
    fun shouldUnregisterPlaybackEventsOnPlaybackChange() {
        val oldPlayback = container.playback!!
        val newPlayback = FakePlayback()

        container.playback = newPlayback

        oldPlayback.trigger(Event.PLAYING.value)
        oldPlayback.trigger(Event.DID_PAUSE.value)
        assertEquals(UIPlugin.Visibility.HIDDEN, mediaControl.visibility, "Media control should ignore Playback event")

        newPlayback.trigger(Event.PLAYING.value)
        newPlayback.trigger(Event.DID_PAUSE.value)
        assertEquals(UIPlugin.Visibility.VISIBLE, mediaControl.visibility, "Media control should handle Playback event")
    }

    @Test
    fun shouldUpdateInteractionTimeWhenDidUpdateInteractingEventIsCalled() {
        val expectedTime = 1234L

        ShadowSystemClock.setCurrentTimeMillis(expectedTime)

        core.trigger(InternalEvent.DID_UPDATE_INTERACTING.value)

        assertEquals(expectedTime, mediaControl.lastInteractionTime)
    }

    @Test
    fun shouldUpdateInteractionTimeWhenDidTouchMediaControlEventIsCalled() {
        val expectedTime = 1234L

        ShadowSystemClock.setCurrentTimeMillis(expectedTime)

        core.trigger(InternalEvent.DID_TOUCH_MEDIA_CONTROL.value)

        assertEquals(expectedTime, mediaControl.lastInteractionTime)
    }

    @Test
    fun shouldShowPluginsByRegisterOrder() {
        setupFakeMediaControlPlugins(Panel.BOTTOM, Position.RIGHT)
        assertOrderOfMediaControlPlugins(mediaControl.bottomRightPanel, FakePlugin::class.java, FakePlugin.name, 0)
        assertOrderOfMediaControlPlugins(mediaControl.bottomRightPanel, FakePlugin2::class.java, FakePlugin2.name, 1)
        assertOrderOfMediaControlPlugins(mediaControl.bottomRightPanel, FakePlugin3::class.java, FakePlugin3.name, 2)
    }

    @Test
    fun shouldShowPluginsByOption() {
        val sequence = "${FakePlugin3.name},${FakePlugin2.name},${FakePlugin.name}"
        setupFakeMediaControlPlugins(Panel.BOTTOM, Position.RIGHT, sequence)

        assertOrderOfMediaControlPlugins(mediaControl.bottomRightPanel, FakePlugin3::class.java, FakePlugin3.name, 0)
        assertOrderOfMediaControlPlugins(mediaControl.bottomRightPanel, FakePlugin2::class.java, FakePlugin2.name, 1)
        assertOrderOfMediaControlPlugins(mediaControl.bottomRightPanel, FakePlugin::class.java, FakePlugin.name, 2)
    }

    @Test
    fun shouldShowPluginsByOptionWithDuplicates() {
        val sequence = "${FakePlugin3.name},${FakePlugin2.name},${FakePlugin.name},${FakePlugin2.name}"
        setupFakeMediaControlPlugins(Panel.BOTTOM, Position.RIGHT, sequence)

        assertOrderOfMediaControlPlugins(mediaControl.bottomRightPanel, FakePlugin3::class.java, FakePlugin3.name, 0)
        assertOrderOfMediaControlPlugins(mediaControl.bottomRightPanel, FakePlugin2::class.java, FakePlugin2.name, 1)
        assertOrderOfMediaControlPlugins(mediaControl.bottomRightPanel, FakePlugin::class.java, FakePlugin.name, 2)
    }

    @Test
    fun shouldShowPluginsByOptionWithExtraSpaces() {
        val sequence = " ${FakePlugin3.name}, ${FakePlugin2.name}, ${FakePlugin.name} "
        setupFakeMediaControlPlugins(Panel.BOTTOM, Position.RIGHT, sequence)

        assertOrderOfMediaControlPlugins(mediaControl.bottomRightPanel, FakePlugin3::class.java, FakePlugin3.name, 0)
        assertOrderOfMediaControlPlugins(mediaControl.bottomRightPanel, FakePlugin2::class.java, FakePlugin2.name, 1)
        assertOrderOfMediaControlPlugins(mediaControl.bottomRightPanel, FakePlugin::class.java, FakePlugin.name, 2)
    }

    @Test
    fun shouldShowPluginsByOptionWithAWrongName() {
        val sequence = "${FakePlugin3.name},${FakePlugin2.name},nonexistent,${FakePlugin.name}"
        setupFakeMediaControlPlugins(Panel.BOTTOM, Position.RIGHT, sequence)

        assertOrderOfMediaControlPlugins(mediaControl.bottomRightPanel, FakePlugin3::class.java, FakePlugin3.name, 0)
        assertOrderOfMediaControlPlugins(mediaControl.bottomRightPanel, FakePlugin2::class.java, FakePlugin2.name, 1)
        assertOrderOfMediaControlPlugins(mediaControl.bottomRightPanel, FakePlugin::class.java, FakePlugin.name, 2)
    }

    @Test
    fun shouldShowPluginsByRegisterOrderFollowedByOption() {
        val sequence = "${FakePlugin3.name},${FakePlugin.name}"
        setupFakeMediaControlPlugins(Panel.BOTTOM, Position.RIGHT, sequence)

        assertOrderOfMediaControlPlugins(mediaControl.bottomRightPanel, FakePlugin2::class.java, FakePlugin2.name, 0)
        assertOrderOfMediaControlPlugins(mediaControl.bottomRightPanel, FakePlugin3::class.java, FakePlugin3.name, 1)
        assertOrderOfMediaControlPlugins(mediaControl.bottomRightPanel, FakePlugin::class.java, FakePlugin.name, 2)
    }

    @Test
    fun shouldShowPluginsByRegisterOrderWhenOptionsIsEmpty() {
        val sequence = ""
        setupFakeMediaControlPlugins(Panel.BOTTOM, Position.RIGHT, sequence)

        assertOrderOfMediaControlPlugins(mediaControl.bottomRightPanel, FakePlugin::class.java, FakePlugin.name, 0)
        assertOrderOfMediaControlPlugins(mediaControl.bottomRightPanel, FakePlugin2::class.java, FakePlugin2.name, 1)
        assertOrderOfMediaControlPlugins(mediaControl.bottomRightPanel, FakePlugin3::class.java, FakePlugin3.name, 2)
    }

    @Test
    fun shouldShowPluginsByRegisterOrderWhenOptionsIsInvalid() {
        val sequence = "invalid"
        setupFakeMediaControlPlugins(Panel.BOTTOM, Position.RIGHT, sequence)

        assertOrderOfMediaControlPlugins(mediaControl.bottomRightPanel, FakePlugin::class.java, FakePlugin.name, 0)
        assertOrderOfMediaControlPlugins(mediaControl.bottomRightPanel, FakePlugin2::class.java, FakePlugin2.name, 1)
        assertOrderOfMediaControlPlugins(mediaControl.bottomRightPanel, FakePlugin3::class.java, FakePlugin3.name, 2)
    }

    private fun setupFakeMediaControlPlugins(panel: Panel, position: Position, sequenceOption: String? = null) {
        FakePlugin.currentPanel = panel
        FakePlugin.currentPosition = position
        FakePlugin2.currentPanel = panel
        FakePlugin2.currentPosition = position
        FakePlugin3.currentPanel = panel
        FakePlugin3.currentPosition = position

        Loader.registerPlugin(FakePlugin::class)
        Loader.registerPlugin(FakePlugin2::class)
        Loader.registerPlugin(FakePlugin3::class)

        core = Core(Loader(), Options())
        sequenceOption?.let {
            core.options[io.clappr.player.base.ClapprOption.MEDIA_CONTROL_PLUGINS.value] = it
        }

        mediaControl = MediaControl(core)
        mediaControl.render()
    }

    private fun assertOrderOfMediaControlPlugins(layoutPanel: LinearLayout, className: Class<out MediaControl.Plugin>, name: String, index: Int) {
        val plugin = core.plugins.filterIsInstance(className).first()
        assertEquals(name, plugin.name)
        assertEquals(plugin.view, layoutPanel.getChildAt(index))
    }

    private fun setupFakeMediaControlPlugin(panel: Panel, position: Position) {
        FakePlugin.currentPanel = panel
        FakePlugin.currentPosition = position

        Loader.registerPlugin(FakePlugin::class)

        core = Core(Loader(), Options())

        mediaControl = MediaControl(core)
        mediaControl.render()
    }

    private fun assertMediaControlPanel(layoutPanel: LinearLayout, panel: Panel, position: Position) {
        val plugin = core.plugins.filterIsInstance(FakePlugin::class.java).first()
        assertTrue(mediaControl.controlPlugins.any { p -> p is MediaControlTest.FakePlugin }, "Media Control Plugin should be added to Media Control")
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

    class FakePlugin(core: Core) : MediaControl.Plugin(core) {
        companion object : NamedType {
            override val name = "fakeMediaControlPlugin"

            var currentPanel: Panel = Panel.NONE
            var currentPosition: Position = Position.NONE
        }

        override var panel: Panel = currentPanel
        override var position: Position = currentPosition
    }

    class FakePlugin2(core: Core) : MediaControl.Plugin(core) {
        companion object : NamedType {
            override val name = "fakeMediaControlPlugin2"

            var currentPanel: Panel = Panel.NONE
            var currentPosition: Position = Position.NONE
        }

        override var panel: Panel = currentPanel
        override var position: Position = currentPosition
    }

    class FakePlugin3(core: Core) : MediaControl.Plugin(core) {
        companion object : NamedType {
            override val name = "fakeMediaControlPlugin3"

            var currentPanel: Panel = Panel.NONE
            var currentPosition: Position = Position.NONE
        }

        override var panel: Panel = currentPanel
        override var position: Position = currentPosition
    }
}
