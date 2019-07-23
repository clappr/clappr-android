package io.clappr.player.plugin.control

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.test.core.app.ApplicationProvider
import io.clappr.player.R
import io.clappr.player.base.*
import io.clappr.player.base.keys.Action
import io.clappr.player.base.keys.Key
import io.clappr.player.components.Container
import io.clappr.player.components.Core
import io.clappr.player.components.Playback
import io.clappr.player.components.PlaybackSupportCheck
import io.clappr.player.plugin.*
import io.clappr.player.plugin.control.MediaControl.Plugin.Panel
import io.clappr.player.plugin.control.MediaControl.Plugin.Position
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowSystemClock
import org.robolectric.shadows.ShadowView
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23], shadows = [ShadowSystemClock::class, ShadowView::class])
class MediaControlTest {

    private lateinit var mediaControl: MediaControl
    private lateinit var core: Core
    private lateinit var container: Container
    private lateinit var fakePlayback: FakePlayback

    @Before
    fun setUp() {
        BaseObject.applicationContext = ApplicationProvider.getApplicationContext()
        container = Container(Options())

        core = Core(Options())

        mediaControl = MediaControl(core)

        core.activeContainer = container
        fakePlayback = FakePlayback()
        container.playback = fakePlayback
    }

    @After
    fun tearDown() {
        Loader.clearPlugins()
        Loader.clearPlaybacks()
    }

    @Test
    fun shouldAddMediaControlPluginInCenterPanel() {
        setupFakeMediaControlPlugin(Panel.CENTER, Position.NONE)
        assertMediaControlPanel(getCenterPanel(), Panel.CENTER, Position.NONE)
    }

    @Test
    fun shouldAddMediaControlPluginInCenterPanelEvenWhenPositionIsLeft() {
        setupFakeMediaControlPlugin(Panel.CENTER, Position.LEFT)
        assertMediaControlPanel(getCenterPanel(), Panel.CENTER, Position.LEFT)
    }

    @Test
    fun shouldAddMediaControlPluginInCenterPanelEvenWhenPositionIsRight() {
        setupFakeMediaControlPlugin(Panel.CENTER, Position.RIGHT)
        assertMediaControlPanel(getCenterPanel(), Panel.CENTER, Position.RIGHT)
    }

    @Test
    fun shouldAddMediaControlPluginInBottomPanel() {
        setupFakeMediaControlPlugin(Panel.BOTTOM, Position.NONE)
        assertMediaControlPanel(getBottomPanel(), Panel.BOTTOM, Position.NONE)
    }

    @Test
    fun shouldAddMediaControlPluginInBottomLeftPanel() {
        setupFakeMediaControlPlugin(Panel.BOTTOM, Position.LEFT)
        assertMediaControlPanel(getBottomLeftPanel(), Panel.BOTTOM, Position.LEFT)
    }

    @Test
    fun shouldAddMediaControlPluginInBottomRightPanel() {
        setupFakeMediaControlPlugin(Panel.BOTTOM, Position.RIGHT)
        assertMediaControlPanel(getBottomRightPanel(), Panel.BOTTOM, Position.RIGHT)
    }

    @Test
    fun shouldAddMediaControlPluginInTopPanel() {
        setupFakeMediaControlPlugin(Panel.TOP, Position.NONE)
        assertMediaControlPanel(getTopPanel(), Panel.TOP, Position.NONE)
    }

    @Test
    fun shouldAddMediaControlPluginInTopLeftPanel() {
        setupFakeMediaControlPlugin(Panel.TOP, Position.LEFT)
        assertMediaControlPanel(getTopLeftPanel(), Panel.TOP, Position.LEFT)
    }

    @Test
    fun shouldAddMediaControlPluginInTopRightPanel() {
        setupFakeMediaControlPlugin(Panel.TOP, Position.RIGHT)
        assertMediaControlPanel(getTopRightPanel(), Panel.TOP, Position.RIGHT)
    }

    @Test
    fun shouldAddMediaControlPluginInTopCenterPanel() {
        setupFakeMediaControlPlugin(Panel.TOP, Position.CENTER)

        val plugin = core.plugins.asSequence().filterIsInstance(FakePlugin::class.java).first()

        assertEquals(3, getTopCenterPanel().childCount, "Media Control Plugin should be added to Panel.TOP panel and Position.CENTER position in Media Control")
        assertEquals(FakePlugin.viewId, getTopCenterPanel().getChildAt(2).id, "Media Control Plugin should be added to Media Control")
        assertEquals(plugin.view, getTopCenterPanel().getChildAt(2))
    }

    @Test
    fun shouldNotAddMediaControlPluginWhenPanelIsNone() {
        setupFakeMediaControlPlugin(Panel.NONE, Position.NONE)

        assertEquals(0, getCenterPanel().childCount, "Media Control Plugin should not be added to Center panel in Media Control")
        assertEquals(0, getTopPanel().childCount,"Media Control Plugin should not be added to Top panel in Media Control")
        assertEquals(0, getTopRightPanel().childCount,"Media Control Plugin should not be added to Top Right panel in Media Control")
        assertEquals(0, getTopLeftPanel().childCount,"Media Control Plugin should not be added to Top Left panel in Media Control")
        assertEquals(0, getBottomPanel().childCount,"Media Control Plugin should not be added to Bottom panel in Media Control")
        assertEquals(0, getBottomRightPanel().childCount,"Media Control Plugin should not be added to Bottom Right panel in Media Control")
        assertEquals(0, getBottomLeftPanel().childCount,"Media Control Plugin should not be added to Bottom Left panel in Media Control")
    }

    @Test
    fun shouldSetModalPanelVisibilityToInvisibleWhenMediaControlIsRendered() {
        mediaControl.render()

        assertEquals(View.INVISIBLE, getModalPanel().visibility, "Modal Panel should be INVISIBLE")
    }

    @Test
    fun shouldOpenModalWhenReceiveOpenModalPanelEvent() {
        mediaControl.render()

        triggerOpenModalPanelEvent()

        assertEquals(View.VISIBLE, getModalPanel().visibility, "Modal Panel should be VISIBLE")
    }

    @Test
    fun shouldCloseModalWhenReceiveCloseModalPanelEvent() {
        triggerOpenModalPanelEvent()

        triggerCloseModalPanelEvent()

        assertEquals(View.INVISIBLE, getModalPanel().visibility, "Modal Panel should be INVISIBLE")
    }

    @Test
    fun shouldShowControlsPanelWhenModalWasVisibleAndClosed(){
        triggerOpenModalPanelEvent()

        triggerCloseModalPanelEvent()

        assertEquals(View.VISIBLE, getControlsPanel().visibility)
    }

    @Test
    fun shouldKeepControlsPanelVisibilityWhenCloseModalEventWasTriggeredWithHiddenModalPanel(){
        fakePlayback.fakeState = Playback.State.PLAYING
        mediaControl.hide()

        triggerCloseModalPanelEvent()

        assertEquals(View.INVISIBLE, getControlsPanel().visibility)
    }

    @Test
    fun shouldShowForegroundControlsPanelWhenModalWasVisibleAndClosed(){
        triggerOpenModalPanelEvent()

        triggerCloseModalPanelEvent()

        assertEquals(View.VISIBLE, getForegroundControlsPanel().visibility)
    }


    @Test
    fun shouldKeepForegroundControlsPanelVisibilityWhenCloseModalEventWasTriggeredWithHiddenModalPanel(){
        fakePlayback.fakeState = Playback.State.PLAYING
        mediaControl.hide()

        triggerCloseModalPanelEvent()

        assertEquals(View.INVISIBLE, getForegroundControlsPanel().visibility)
    }


    @Test
    fun shouldSendDidOpenModalPanelEventWhenModalPanelWasOpened() {
        var didOpenModalPanelEventWasSent = false

        var resultBundle: Bundle? = null

        core.on(InternalEvent.DID_OPEN_MODAL_PANEL.value) { bundle ->
            didOpenModalPanelEventWasSent = true
            resultBundle = bundle
        }

        triggerOpenModalPanelEvent()

        assertTrue(didOpenModalPanelEventWasSent, "Should trigger ${InternalEvent.DID_OPEN_MODAL_PANEL.value} when Modal Panel was open ")

        val map = (resultBundle?.getSerializable(MediaControl.modalPanelViewKey) as? HashMap<*, *>)
        var modalView: ViewGroup? = null

        map?.let {
            modalView = it[MediaControl.modalPanelViewKey] as ViewGroup
        }

        assertEquals(getModalPanel(), modalView)
    }

    @Test
    fun shouldHideControlsPanelOnOpenModalEvent() {
        triggerOpenModalPanelEvent()

        assertEquals(View.INVISIBLE, getControlsPanel().visibility, "Should make controls panel invisible when modal panel open")
    }

    @Test
    fun shouldShowControlsPanelOnCloseModalEvent() {
        triggerOpenModalPanelEvent()

        triggerCloseModalPanelEvent()

        assertEquals(View.VISIBLE, getControlsPanel().visibility, "Should make control panel visible when modal panel close")
    }

    @Test
    fun shouldHideForegroundsControlsPanelOnOpenModalEvent() {
        triggerOpenModalPanelEvent()

        assertEquals(View.INVISIBLE, getForegroundControlsPanel().visibility, "Should make foreground controls panel invisible when panel modal open")
    }

    @Test
    fun shouldShowMediaControlWhenPlaybackIsPaused() {
        core.activePlayback?.trigger(Event.PLAYING.value)

        core.activePlayback?.trigger(Event.DID_PAUSE.value)

        assertEquals(UIPlugin.Visibility.VISIBLE, mediaControl.visibility)
    }

    @Test
    fun shouldInitializeModalPanelInvisibleWhenMediaControlPluginIsCreated() {
        assertEquals(View.INVISIBLE, getModalPanel().visibility)
    }

    @Test
    fun shouldShowOnlyModalPanelWhenPlaybackIsPausedAndPanelModalIsOpen() {
        core.activePlayback?.trigger(Event.PLAYING.value)

        triggerOpenModalPanelEvent()

        core.activePlayback?.trigger(Event.DID_PAUSE.value)

        assertEquals(View.INVISIBLE, getForegroundControlsPanel().visibility)
        assertEquals(View.INVISIBLE, getControlsPanel().visibility)
        assertEquals(View.VISIBLE, getModalPanel().visibility)
    }

    @Test
    fun shouldShowForegroundsControlsPanelOnCloseModalEvent() {
        triggerOpenModalPanelEvent()

        triggerCloseModalPanelEvent()

        assertEquals(getForegroundControlsPanel().visibility, View.VISIBLE, "Should make foreground controls panel visible when panel modal close")
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

        val newContainer = Container(Options())
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
    fun shouldStopListeningOldContainerWhenDidChangeActiveContainerEventIsTriggered() {
        val oldContainer = container

        assertEquals(Plugin.State.ENABLED, mediaControl.state)

        core.activeContainer = Container(Options())

        oldContainer.trigger(InternalEvent.DISABLE_MEDIA_CONTROL.value)

        assertEquals(Plugin.State.ENABLED, mediaControl.state)
    }

    @Test
    fun shouldStopListeningOldPlaybackWhenDidChangeActivePlaybackEventIsTriggered() {
        val oldPlayback = container.playback

        assertEquals(UIPlugin.Visibility.HIDDEN, mediaControl.visibility)

        container.playback = FakePlayback()

        oldPlayback?.trigger(Event.DID_PAUSE.value)

        assertEquals(UIPlugin.Visibility.HIDDEN, mediaControl.visibility)
    }

    @Test
    fun shouldStopListeningOldPlaybackAfterDestroy() {
        setupViewHidden(mediaControl)
        val oldPlayback = container.playback

        mediaControl.destroy()

        oldPlayback?.trigger(InternalEvent.ENABLE_MEDIA_CONTROL.value)

        assertHiddenView(mediaControl)
    }

    @Test
    fun shouldShowPluginsByOption() {
        val sequence = "${FakePlugin3.name},${FakePlugin2.name},${FakePlugin.name}"
        setupFakeMediaControlPlugins(Panel.BOTTOM, Position.RIGHT, sequence)

        assertOrderOfMediaControlPlugins(getBottomRightPanel(), FakePlugin3::class.java, FakePlugin3.name, 0)
        assertOrderOfMediaControlPlugins(getBottomRightPanel(), FakePlugin2::class.java, FakePlugin2.name, 1)
        assertOrderOfMediaControlPlugins(getBottomRightPanel(), FakePlugin::class.java, FakePlugin.name, 2)
    }

    @Test
    fun shouldShowPluginsByOptionWithDuplicates() {
        val sequence = "${FakePlugin3.name},${FakePlugin2.name},${FakePlugin.name},${FakePlugin2.name}"
        setupFakeMediaControlPlugins(Panel.BOTTOM, Position.RIGHT, sequence)

        assertOrderOfMediaControlPlugins(getBottomRightPanel(), FakePlugin3::class.java, FakePlugin3.name, 0)
        assertOrderOfMediaControlPlugins(getBottomRightPanel(), FakePlugin2::class.java, FakePlugin2.name, 1)
        assertOrderOfMediaControlPlugins(getBottomRightPanel(), FakePlugin::class.java, FakePlugin.name, 2)
    }

    @Test
    fun shouldShowPluginsByOptionWithExtraSpaces() {
        val sequence = " ${FakePlugin3.name}, ${FakePlugin2.name}, ${FakePlugin.name} "
        setupFakeMediaControlPlugins(Panel.BOTTOM, Position.RIGHT, sequence)

        assertOrderOfMediaControlPlugins(getBottomRightPanel(), FakePlugin3::class.java, FakePlugin3.name, 0)
        assertOrderOfMediaControlPlugins(getBottomRightPanel(), FakePlugin2::class.java, FakePlugin2.name, 1)
        assertOrderOfMediaControlPlugins(getBottomRightPanel(), FakePlugin::class.java, FakePlugin.name, 2)
    }

    @Test
    fun shouldShowPluginsByOptionWithAWrongName() {
        val sequence = "${FakePlugin3.name},${FakePlugin2.name},nonexistent,${FakePlugin.name}"
        setupFakeMediaControlPlugins(Panel.BOTTOM, Position.RIGHT, sequence)

        assertOrderOfMediaControlPlugins(getBottomRightPanel(), FakePlugin3::class.java, FakePlugin3.name, 0)
        assertOrderOfMediaControlPlugins(getBottomRightPanel(), FakePlugin2::class.java, FakePlugin2.name, 1)
        assertOrderOfMediaControlPlugins(getBottomRightPanel(), FakePlugin::class.java, FakePlugin.name, 2)
    }

    @Test
    fun shouldHideMediaControlWhenWillLoadSource() {
        fakePlayback.fakeState = Playback.State.PAUSED
        core.activePlayback?.trigger(Event.DID_PAUSE.value)

        container.trigger(InternalEvent.WILL_LOAD_SOURCE.value)

        assertEquals(mediaControl.visibility, UIPlugin.Visibility.HIDDEN)
        assertEquals(View.INVISIBLE, getBackgroundView().visibility)
        assertEquals(View.INVISIBLE, getControlsPanel().visibility)
        assertEquals(View.INVISIBLE, getForegroundControlsPanel().visibility)
    }

    @Test
    fun shouldRenderWithHiddenMediaControl() {
        fakePlayback.fakeState = Playback.State.PAUSED

        mediaControl.render()

        assertEquals(mediaControl.visibility, UIPlugin.Visibility.HIDDEN)
        assertEquals(View.INVISIBLE, getBackgroundView().visibility)
        assertEquals(View.INVISIBLE, getControlsPanel().visibility)
        assertEquals(View.INVISIBLE, getForegroundControlsPanel().visibility)
    }

    @Test
    fun shouldTriggerWillHideMediaControlWhenHideIsCalled() {
        assertEventTriggeredWhenHideIsCalled(InternalEvent.WILL_HIDE_MEDIA_CONTROL.value)
    }

    @Test
    fun shouldTriggerDidHideMediaControlWhenHideIsCalled() {
        assertEventTriggeredWhenHideIsCalled(InternalEvent.DID_HIDE_MEDIA_CONTROL.value)
    }

    @Test
    fun shouldNotTriggerWillHideMediaControlWhenHideIsCalledAndIsEnableAndPlaybackIsIdle() {
        assertEventNotTriggeredWhenHideIsCalled(InternalEvent.WILL_HIDE_MEDIA_CONTROL.value)
    }

    @Test
    fun shouldNotTriggerDidHideMediaControlWhenHideIsCalledAndIsEnableAndPlaybackIsIdle() {
        assertEventNotTriggeredWhenHideIsCalled(InternalEvent.DID_HIDE_MEDIA_CONTROL.value)
    }

    @Test
    fun shouldTriggerWillShowMediaControlWhenShowIsCalled() {
        assertEventTriggeredWhenShowIsCalled(InternalEvent.WILL_SHOW_MEDIA_CONTROL.value)
    }

    @Test
    fun shouldTriggerDidShowMediaControlWhenShowIsCalled() {
        assertEventTriggeredWhenShowIsCalled(InternalEvent.DID_SHOW_MEDIA_CONTROL.value)
    }

    @Test
    fun shouldEnableMediaControlWhenWillLoadSourceIsTriggered() {
        mediaControl.state = Plugin.State.DISABLED

        container.trigger(InternalEvent.WILL_LOAD_SOURCE.value)

        assertEquals(Plugin.State.ENABLED, mediaControl.state)
    }

    @Test
    fun shouldShowMediaControlWhenKeyReceivedSupportedKeysOnUpAction() {
        core.trigger(Event.DID_RECEIVE_INPUT_KEY.value, Bundle().apply {
            putString(EventData.INPUT_KEY_CODE.value, Key.PLAY_PAUSE.value)
            putString(EventData.INPUT_KEY_ACTION.value, Action.UP.value)
        })

        assertEquals(UIPlugin.Visibility.VISIBLE, mediaControl.visibility)
    }

    @Test
    fun shouldNotShowMediaControlWhenDoesntNotSupportKey() {
        core.trigger(Event.DID_RECEIVE_INPUT_KEY.value, Bundle().apply {
            putString(EventData.INPUT_KEY_CODE.value, Key.UNDEFINED.value)
            putString(EventData.INPUT_KEY_ACTION.value, Action.UP.value)
        })

        assertEquals(UIPlugin.Visibility.HIDDEN, mediaControl.visibility)
    }

    @Test
    fun shouldNotShowMediaControlWhenReceiveSupportedKeyButWithActionDown() {
        core.trigger(Event.DID_RECEIVE_INPUT_KEY.value, Bundle().apply {
            putString(EventData.INPUT_KEY_CODE.value, Key.PLAY_PAUSE.value)
            putString(EventData.INPUT_KEY_ACTION.value, Action.DOWN.value)
        })

        assertEquals(UIPlugin.Visibility.HIDDEN, mediaControl.visibility)
    }

    @Test
    fun shouldHaveUndefinedKeyInBlockList() {
        val expectedKeys = mutableListOf(Key.UNDEFINED)

        assertTrue { mediaControl.blockListKey.containsAll(expectedKeys) }
    }

    private fun getCenterPanel() = mediaControl.view.findViewById<LinearLayout>(R.id.center_panel)
    private fun getBottomPanel() = mediaControl.view.findViewById<LinearLayout>(R.id.bottom_panel)
    private fun getBottomLeftPanel() = mediaControl.view.findViewById<LinearLayout>(R.id.bottom_left_panel)
    private fun getBottomRightPanel() = mediaControl.view.findViewById<LinearLayout>(R.id.bottom_right_panel)
    private fun getTopPanel() = mediaControl.view.findViewById<LinearLayout>(R.id.top_panel)
    private fun getTopLeftPanel() = mediaControl.view.findViewById<LinearLayout>(R.id.top_left_panel)
    private fun getTopRightPanel() = mediaControl.view.findViewById<LinearLayout>(R.id.top_right_panel)
    private fun getTopCenterPanel() = mediaControl.view.findViewById<RelativeLayout>(R.id.top_center)
    private fun getModalPanel() = mediaControl.view.findViewById<FrameLayout>(R.id.modal_panel)
    private fun getControlsPanel() = mediaControl.view.findViewById<RelativeLayout>(R.id.controls_panel)
    private fun getForegroundControlsPanel() = mediaControl.view.findViewById<FrameLayout>(R.id.foreground_controls_panel)
    private fun getBackgroundView() = mediaControl.view.findViewById<View>(R.id.background_view)

    private fun setupFakeMediaControlPlugins(panel: Panel, position: Position, sequenceOption: String? = null) {
        FakePlugin.currentPanel = panel
        FakePlugin.currentPosition = position
        FakePlugin2.currentPanel = panel
        FakePlugin2.currentPosition = position
        FakePlugin3.currentPanel = panel
        FakePlugin3.currentPosition = position

        Loader.register(FakePlugin.entry)
        Loader.register(FakePlugin2.entry)
        Loader.register(FakePlugin3.entry)

        core = Core(Options())
        sequenceOption?.let {
            core.options[io.clappr.player.base.ClapprOption.MEDIA_CONTROL_PLUGINS.value] = it
        }

        mediaControl = MediaControl(core)
        mediaControl.render()
    }

    private fun assertOrderOfMediaControlPlugins(layoutPanel: LinearLayout, className: Class<out MediaControl.Plugin>, name: String, index: Int) {
        val plugin = core.plugins.asSequence().filterIsInstance(className).first()
        assertEquals(name, plugin.name)
        assertEquals(plugin.view, layoutPanel.getChildAt(index))
    }

    private fun setupFakeMediaControlPlugin(panel: Panel, position: Position) {
        FakePlugin.currentPanel = panel
        FakePlugin.currentPosition = position

        Loader.register(FakePlugin.entry)

        core = Core(Options())

        mediaControl = MediaControl(core)
        mediaControl.render()
    }

    private fun assertMediaControlPanel(layoutPanel: LinearLayout, panel: Panel, position: Position) {
        val plugin = core.plugins.asSequence().filterIsInstance(FakePlugin::class.java).first()

        assertEquals(1, layoutPanel.childCount, "Media Control Plugin should be added to $panel panel and $position position in Media Control")
        assertEquals(FakePlugin.viewId, layoutPanel.getChildAt(0).id, "Media Control Plugin should be added to Media Control")
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

    private fun assertEventTriggeredWhenHideIsCalled(event: String) {
        mediaControl.state = Plugin.State.ENABLED
        fakePlayback.fakeState = Playback.State.PLAYING
        var eventTriggered = false
        core.on(event) {
            eventTriggered = true
        }

        mediaControl.hide()
        assertTrue(eventTriggered)
    }

    private fun assertEventNotTriggeredWhenHideIsCalled(event: String) {
        mediaControl.state = Plugin.State.ENABLED
        fakePlayback.fakeState = Playback.State.IDLE
        var eventTriggered = false
        core.on(event) {
            eventTriggered = true
        }

        mediaControl.hide()
        assertFalse(eventTriggered)
    }

    private fun assertEventTriggeredWhenShowIsCalled(event: String) {
        var eventTriggered = false
        core.on(event) {
            eventTriggered = true
        }

        mediaControl.show()
        assertTrue(eventTriggered)
    }

    class FakePlayback(source: String = "aSource", mimeType: String? = null, options: Options = Options()) : Playback(source, mimeType, options, name, supportsSource) {
        companion object {
            const val name = "fakePlayback"
            val supportsSource: PlaybackSupportCheck = { _, _ -> true }
        }

        override val state: State
            get() = fakeState

        var fakeState: State = State.IDLE
    }

    class FakePlugin(core: Core) : MediaControl.Plugin(core, name) {
        companion object : NamedType {
            override val name = "fakeMediaControlPlugin"

            val entry = PluginEntry.Core(name = name, factory = { core -> FakePlugin(core) })

            var currentPanel: Panel = Panel.NONE
            var currentPosition: Position = Position.NONE
            const val viewId = 12345
        }

        override var panel: Panel = currentPanel
        override var position: Position = currentPosition

        init {
            view?.id = viewId
        }
    }

    class FakePlugin2(core: Core) : MediaControl.Plugin(core, name) {
        companion object : NamedType {
            override val name = "fakeMediaControlPlugin2"

            val entry = PluginEntry.Core(name = name, factory = { core -> FakePlugin2(core) })

            var currentPanel: Panel = Panel.NONE
            var currentPosition: Position = Position.NONE
        }

        override var panel: Panel = currentPanel
        override var position: Position = currentPosition
    }

    class FakePlugin3(core: Core) : MediaControl.Plugin(core, name) {
        companion object : NamedType {
            override val name = "fakeMediaControlPlugin3"

            val entry = PluginEntry.Core(name = name, factory = { core -> FakePlugin3(core) })

            var currentPanel: Panel = Panel.NONE
            var currentPosition: Position = Position.NONE
        }

        override var panel: Panel = currentPanel
        override var position: Position = currentPosition
    }
}
