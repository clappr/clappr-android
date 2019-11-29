package io.clappr.player.plugin.control

import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
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
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowSystemClock
import org.robolectric.shadows.ShadowView
import org.robolectric.util.Scheduler
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
    private lateinit var scheduler: Scheduler

    @Before
    fun setUp() {
        BaseObject.applicationContext = ApplicationProvider.getApplicationContext()
        scheduler = Robolectric.getForegroundThreadScheduler()

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
    fun `should add media control plugin in center panel`() {
        setupFakeMediaControlPlugin(Panel.CENTER, Position.NONE)
        assertMediaControlPanel(getCenterPanel(), Panel.CENTER, Position.NONE)
    }

    @Test
    fun `should add media control plugin in center panel even when position is left`() {
        setupFakeMediaControlPlugin(Panel.CENTER, Position.LEFT)
        assertMediaControlPanel(getCenterPanel(), Panel.CENTER, Position.LEFT)
    }

    @Test
    fun `should add media control plugin in center panel even when position is right`() {
        setupFakeMediaControlPlugin(Panel.CENTER, Position.RIGHT)
        assertMediaControlPanel(getCenterPanel(), Panel.CENTER, Position.RIGHT)
    }

    @Test
    fun `should add media control plugin in bottom panel`() {
        setupFakeMediaControlPlugin(Panel.BOTTOM, Position.NONE)
        assertMediaControlPanel(getBottomPanel(), Panel.BOTTOM, Position.NONE)
    }

    @Test
    fun `should add media control plugin in bottom left panel`() {
        setupFakeMediaControlPlugin(Panel.BOTTOM, Position.LEFT)
        assertMediaControlPanel(getBottomLeftPanel(), Panel.BOTTOM, Position.LEFT)
    }

    @Test
    fun `should add media control plugin in bottom right panel`() {
        setupFakeMediaControlPlugin(Panel.BOTTOM, Position.RIGHT)
        assertMediaControlPanel(getBottomRightPanel(), Panel.BOTTOM, Position.RIGHT)
    }

    @Test
    fun `should add media control plugin in top panel`() {
        setupFakeMediaControlPlugin(Panel.TOP, Position.NONE)
        assertMediaControlPanel(getTopPanel(), Panel.TOP, Position.NONE)
    }

    @Test
    fun `should add media control plugin in top left panel`() {
        setupFakeMediaControlPlugin(Panel.TOP, Position.LEFT)
        assertMediaControlPanel(getTopLeftPanel(), Panel.TOP, Position.LEFT)
    }

    @Test
    fun `should add media control plugin in top right panel`() {
        setupFakeMediaControlPlugin(Panel.TOP, Position.RIGHT)
        assertMediaControlPanel(getTopRightPanel(), Panel.TOP, Position.RIGHT)
    }

    @Test
    fun `should add media control plugin in top center panel`() {
        setupFakeMediaControlPlugin(Panel.TOP, Position.CENTER)

        val plugin = core.plugins.asSequence().filterIsInstance(FakePlugin::class.java).first()

        assertEquals(3, getTopCenterPanel().childCount, "Media Control Plugin should be added to Panel.TOP panel and Position.CENTER position in Media Control")
        assertEquals(FakePlugin.viewId, getTopCenterPanel().getChildAt(2).id, "Media Control Plugin should be added to Media Control")
        assertEquals(plugin.view, getTopCenterPanel().getChildAt(2))
    }

    @Test
    fun `should not add media control plugin when panel is none`() {
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
    fun `should set modal panel visibility to invisible when media control is rendered`() {
        mediaControl.render()

        assertEquals(View.INVISIBLE, getModalPanel().visibility, "Modal Panel should be INVISIBLE")
    }

    @Test
    fun `should open modal when receive open modal panel event`() {
        mediaControl.render()

        triggerOpenModalPanelEvent()

        assertEquals(View.VISIBLE, getModalPanel().visibility, "Modal Panel should be VISIBLE")
    }

    @Test
    fun `should close modal when receive close modal panel event`() {
        triggerOpenModalPanelEvent()

        triggerCloseModalPanelEvent()

        assertEquals(View.INVISIBLE, getModalPanel().visibility, "Modal Panel should be INVISIBLE")
    }

    @Test
    fun `should show controls panel when modal was visible and closed`(){
        triggerOpenModalPanelEvent()

        triggerCloseModalPanelEvent()

        assertEquals(View.VISIBLE, getControlsPanel().visibility)
    }

    @Test
    fun `should keep controls panel visibility when close modal event was triggered with hidden modal panel`(){
        fakePlayback.fakeState = Playback.State.PLAYING
        mediaControl.hide()

        triggerCloseModalPanelEvent()

        assertEquals(View.INVISIBLE, getControlsPanel().visibility)
    }

    @Test
    fun `should show foreground controls panel when modal was visible and closed`(){
        triggerOpenModalPanelEvent()

        triggerCloseModalPanelEvent()

        assertEquals(View.VISIBLE, getForegroundControlsPanel().visibility)
    }


    @Test
    fun `should keep foreground controls panel visibility when close modal event was triggered with hidden modal panel`(){
        fakePlayback.fakeState = Playback.State.PLAYING
        mediaControl.hide()

        triggerCloseModalPanelEvent()

        assertEquals(View.INVISIBLE, getForegroundControlsPanel().visibility)
    }


    @Test
    fun `should send did open modal panel event when modal panel was opened`() {
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
    fun `should pause playback on open modal event`() {
        fakePlayback.fakeState = Playback.State.PLAYING

        triggerOpenModalPanelEvent()

        assertEquals(Playback.State.PAUSED, core.activePlayback?.state)
    }

    @Test
    fun `should hide controls panel on open modal event`() {
        triggerOpenModalPanelEvent()

        assertEquals(View.INVISIBLE, getControlsPanel().visibility, "Should make controls panel invisible when modal panel open")
    }

    @Test
    fun `should show controls panel on close modal event`() {
        triggerOpenModalPanelEvent()

        triggerCloseModalPanelEvent()

        assertEquals(View.VISIBLE, getControlsPanel().visibility, "Should make control panel visible when modal panel close")
    }

    @Test
    fun `should hide foregrounds controls panel on open modal event`() {
        triggerOpenModalPanelEvent()

        assertEquals(View.INVISIBLE, getForegroundControlsPanel().visibility, "Should make foreground controls panel invisible when panel modal open")
    }

    @Test
    fun `should show media control when playback is paused`() {
        core.activePlayback?.trigger(Event.PLAYING.value)

        core.activePlayback?.trigger(Event.DID_PAUSE.value)

        assertEquals(UIPlugin.Visibility.VISIBLE, mediaControl.visibility)
    }


    @Test
    fun `should not show media control when playback is paused and double tap is performed`() {
        mediaControl.visibility = UIPlugin.Visibility.HIDDEN
        fakePlayback.fakeState = Playback.State.PAUSED

        mediaControl.render()

        performDoubleTap(0f, 0f)

        core.activePlayback?.trigger(Event.DID_PAUSE.value)

        assertEquals(UIPlugin.Visibility.HIDDEN, mediaControl.visibility)
    }

    @Test
    fun `should hide media control when double tap is performed`() {
        mediaControl.visibility = UIPlugin.Visibility.VISIBLE
        fakePlayback.fakeState = Playback.State.PLAYING

        mediaControl.render()

        performDoubleTap(0f, 0f)

        assertEquals(UIPlugin.Visibility.HIDDEN, mediaControl.visibility)
    }

    @Test
    fun `should trigger DID_DOUBLE_TOUCH_MEDIA_CONTROL when a double tap is performed`() {
        var didDoubleTouchMediaControlWasTriggered = false

        mediaControl.render()

        core.on(InternalEvent.DID_DOUBLE_TOUCH_MEDIA_CONTROL.value) {
            didDoubleTouchMediaControlWasTriggered = true
        }

        performDoubleTap(0f, 0f)

        assertTrue { didDoubleTouchMediaControlWasTriggered }
    }

    @Test
    fun `should initialize modal panel invisible when media control plugin is created`() {
        assertEquals(View.INVISIBLE, getModalPanel().visibility)
    }

    @Test
    fun `should show only modal panel when playback is paused and panel modal is open`() {
        core.activePlayback?.trigger(Event.PLAYING.value)

        triggerOpenModalPanelEvent()

        core.activePlayback?.trigger(Event.DID_PAUSE.value)

        assertEquals(View.INVISIBLE, getForegroundControlsPanel().visibility)
        assertEquals(View.INVISIBLE, getControlsPanel().visibility)
        assertEquals(View.VISIBLE, getModalPanel().visibility)
    }

    @Test
    fun `should show foregrounds controls panel on close modal event`() {
        triggerOpenModalPanelEvent()

        triggerCloseModalPanelEvent()

        assertEquals(getForegroundControlsPanel().visibility, View.VISIBLE, "Should make foreground controls panel visible when panel modal close")
    }

    @Test
    fun `shoul disable media control on event`() {
        assertTrue(mediaControl.isEnabled, "Media control should be enabled by default")

        triggerDisableMediaControlEvent()

        assertFalse(mediaControl.isEnabled, "Media control should be disabled after Container event")
    }

    @Test
    fun `should enable media control on event`() {
        mediaControl.state = Plugin.State.DISABLED

        triggerEnableMediaControlEvent()

        assertTrue(mediaControl.isEnabled, "Media control should be enabled after Container event")
    }

    @Test
    fun `should unregister container events on container change`() {
        mediaControl.state = Plugin.State.DISABLED

        val newContainer = Container(Options())
        core.activeContainer = newContainer

        triggerEnableMediaControlEvent()
        assertFalse(mediaControl.isEnabled, "Media control should ignore Container event")

        newContainer.trigger(InternalEvent.ENABLE_MEDIA_CONTROL.value)
        assertTrue(mediaControl.isEnabled, "Media control should handle new Container event")
    }

    @Test
    fun `should unregister playback events on playback change`() {
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
    fun `should stop listening old container when did change active container event is triggered`() {
        val oldContainer = container

        assertEquals(Plugin.State.ENABLED, mediaControl.state)

        core.activeContainer = Container(Options())

        oldContainer.trigger(InternalEvent.DISABLE_MEDIA_CONTROL.value)

        assertEquals(Plugin.State.ENABLED, mediaControl.state)
    }

    @Test
    fun `should stop listening old playback when did change active playback event is triggered`() {
        val oldPlayback = container.playback

        assertEquals(UIPlugin.Visibility.HIDDEN, mediaControl.visibility)

        container.playback = FakePlayback()

        oldPlayback?.trigger(Event.DID_PAUSE.value)

        assertEquals(UIPlugin.Visibility.HIDDEN, mediaControl.visibility)
    }

    @Test
    fun `should stop listening old playback after destroy`() {
        setupViewHidden(mediaControl)
        val oldPlayback = container.playback

        mediaControl.destroy()

        oldPlayback?.trigger(InternalEvent.ENABLE_MEDIA_CONTROL.value)

        assertHiddenView(mediaControl)
    }

    @Test
    fun `should show plugins by option`() {
        val sequence = "${FakePlugin3.name},${FakePlugin2.name},${FakePlugin.name}"
        setupFakeMediaControlPlugins(Panel.BOTTOM, Position.RIGHT, sequence)

        assertOrderOfMediaControlPlugins(getBottomRightPanel(), FakePlugin3::class.java, FakePlugin3.name, 0)
        assertOrderOfMediaControlPlugins(getBottomRightPanel(), FakePlugin2::class.java, FakePlugin2.name, 1)
        assertOrderOfMediaControlPlugins(getBottomRightPanel(), FakePlugin::class.java, FakePlugin.name, 2)
    }

    @Test
    fun `should ahow plugins by option with duplicates`() {
        val sequence = "${FakePlugin3.name},${FakePlugin2.name},${FakePlugin.name},${FakePlugin2.name}"
        setupFakeMediaControlPlugins(Panel.BOTTOM, Position.RIGHT, sequence)

        assertOrderOfMediaControlPlugins(getBottomRightPanel(), FakePlugin3::class.java, FakePlugin3.name, 0)
        assertOrderOfMediaControlPlugins(getBottomRightPanel(), FakePlugin2::class.java, FakePlugin2.name, 1)
        assertOrderOfMediaControlPlugins(getBottomRightPanel(), FakePlugin::class.java, FakePlugin.name, 2)
    }

    @Test
    fun `should show plugins by option with extra spaces`() {
        val sequence = " ${FakePlugin3.name}, ${FakePlugin2.name}, ${FakePlugin.name} "
        setupFakeMediaControlPlugins(Panel.BOTTOM, Position.RIGHT, sequence)

        assertOrderOfMediaControlPlugins(getBottomRightPanel(), FakePlugin3::class.java, FakePlugin3.name, 0)
        assertOrderOfMediaControlPlugins(getBottomRightPanel(), FakePlugin2::class.java, FakePlugin2.name, 1)
        assertOrderOfMediaControlPlugins(getBottomRightPanel(), FakePlugin::class.java, FakePlugin.name, 2)
    }

    @Test
    fun `should show plugins by option with a wrong name`() {
        val sequence = "${FakePlugin3.name},${FakePlugin2.name},nonexistent,${FakePlugin.name}"
        setupFakeMediaControlPlugins(Panel.BOTTOM, Position.RIGHT, sequence)

        assertOrderOfMediaControlPlugins(getBottomRightPanel(), FakePlugin3::class.java, FakePlugin3.name, 0)
        assertOrderOfMediaControlPlugins(getBottomRightPanel(), FakePlugin2::class.java, FakePlugin2.name, 1)
        assertOrderOfMediaControlPlugins(getBottomRightPanel(), FakePlugin::class.java, FakePlugin.name, 2)
    }

    @Test
    fun `should hide media control when will load source`() {
        fakePlayback.fakeState = Playback.State.PAUSED
        core.activePlayback?.trigger(Event.DID_PAUSE.value)

        container.trigger(InternalEvent.WILL_LOAD_SOURCE.value)

        assertEquals(mediaControl.visibility, UIPlugin.Visibility.HIDDEN)
        assertEquals(View.INVISIBLE, getBackgroundView().visibility)
        assertEquals(View.INVISIBLE, getControlsPanel().visibility)
        assertEquals(View.INVISIBLE, getForegroundControlsPanel().visibility)
    }

    @Test
    fun `should render with hidden media control`() {
        fakePlayback.fakeState = Playback.State.PAUSED

        mediaControl.render()

        assertEquals(mediaControl.visibility, UIPlugin.Visibility.HIDDEN)
        assertEquals(View.INVISIBLE, getBackgroundView().visibility)
        assertEquals(View.INVISIBLE, getControlsPanel().visibility)
        assertEquals(View.INVISIBLE, getForegroundControlsPanel().visibility)
    }

    @Test
    fun `should trigger will hide media control when hide is called`() {
        assertEventTriggeredWhenHideIsCalled(InternalEvent.WILL_HIDE_MEDIA_CONTROL.value)
    }

    @Test
    fun `should trigger did hide media control when hide is called`() {
        assertEventTriggeredWhenHideIsCalled(InternalEvent.DID_HIDE_MEDIA_CONTROL.value)
    }

    @Test
    fun `should not trigger will hide media control when hide is called and is enable and playback is idle`() {
        assertEventNotTriggeredWhenHideIsCalled(InternalEvent.WILL_HIDE_MEDIA_CONTROL.value)
    }

    @Test
    fun `should not trigger did hide media control when hide is called and is enable and playback is idle`() {
        assertEventNotTriggeredWhenHideIsCalled(InternalEvent.DID_HIDE_MEDIA_CONTROL.value)
    }

    @Test
    fun `should trigger will show media control when show is called`() {
        assertEventTriggeredWhenShowIsCalled(InternalEvent.WILL_SHOW_MEDIA_CONTROL.value)
    }

    @Test
    fun `should trigger did show media control when show is called`() {
        assertEventTriggeredWhenShowIsCalled(InternalEvent.DID_SHOW_MEDIA_CONTROL.value)
    }

    @Test
    fun `should enable media control when will load source is triggered`() {
        mediaControl.state = Plugin.State.DISABLED

        container.trigger(InternalEvent.WILL_LOAD_SOURCE.value)

        assertEquals(Plugin.State.ENABLED, mediaControl.state)
    }

    @Test
    fun `should show media control when key received supported keys on up action`() {
        core.trigger(Event.DID_RECEIVE_INPUT_KEY.value, Bundle().apply {
            putString(EventData.INPUT_KEY_CODE.value, Key.PLAY_PAUSE.value)
            putString(EventData.INPUT_KEY_ACTION.value, Action.UP.value)
        })

        assertEquals(UIPlugin.Visibility.VISIBLE, mediaControl.visibility)
    }

    @Test
    fun `should not show media control when does not support key`() {
        core.trigger(Event.DID_RECEIVE_INPUT_KEY.value, Bundle().apply {
            putString(EventData.INPUT_KEY_CODE.value, Key.UNDEFINED.value)
            putString(EventData.INPUT_KEY_ACTION.value, Action.UP.value)
        })

        assertEquals(UIPlugin.Visibility.HIDDEN, mediaControl.visibility)
    }

    @Test
    fun `should not show media control when receive supported key but with action down`() {
        core.trigger(Event.DID_RECEIVE_INPUT_KEY.value, Bundle().apply {
            putString(EventData.INPUT_KEY_CODE.value, Key.PLAY_PAUSE.value)
            putString(EventData.INPUT_KEY_ACTION.value, Action.DOWN.value)
        })

        assertEquals(UIPlugin.Visibility.HIDDEN, mediaControl.visibility)
    }
    
    @Test
    fun `should hide media control when show is called with a duration greater than zero`(){
        fakePlayback.fakeState = Playback.State.PLAYING

        mediaControl.show(1)

        scheduler.advanceToNextPostedRunnable()

        assertEquals(UIPlugin.Visibility.HIDDEN, mediaControl.visibility)
    }

    @Test
    fun `should not hide media control when show is called with a duration less or equal to zero`(){
        fakePlayback.fakeState = Playback.State.PLAYING

        mediaControl.show(0)

        scheduler.advanceToNextPostedRunnable()

        assertEquals(UIPlugin.Visibility.VISIBLE, mediaControl.visibility)
    }

    @Test
    fun `should remove scheduled hide when show is called and hide is executed`(){
        val expectedHideEventsTriggered = 1
        fakePlayback.fakeState = Playback.State.PLAYING

        var eventTriggeredCount = 0
        core.on(InternalEvent.DID_HIDE_MEDIA_CONTROL.value) { eventTriggeredCount += 1 }

        mediaControl.show(1)
        mediaControl.hide()
        scheduler.advanceToNextPostedRunnable()

        assertEquals(expectedHideEventsTriggered, eventTriggeredCount)
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

    private fun performDoubleTap(x: Float, y: Float) {
        mediaControl.view.dispatchTouchEvent(MotionEvent.obtain(223889, 223889, KeyEvent.ACTION_DOWN, x, y, 0))
        mediaControl.view.dispatchTouchEvent(MotionEvent.obtain(223889, 224019, KeyEvent.ACTION_UP, x, y, 0))
        mediaControl.view.dispatchTouchEvent(MotionEvent.obtain(224069, 224069, KeyEvent.ACTION_DOWN, x, y, 0))
        mediaControl.view.dispatchTouchEvent(MotionEvent.obtain(224069, 224191, KeyEvent.ACTION_UP, x, y, 0))
    }

    class FakePlayback(source: String = "aSource", mimeType: String? = null, options: Options = Options()) : Playback(source, mimeType, options, name, supportsSource) {
        companion object {
            const val name = "fakePlayback"
            val supportsSource: PlaybackSupportCheck = { _, _ -> true }
        }

        override fun pause(): Boolean {
            fakeState = State.PAUSED
            return true
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
