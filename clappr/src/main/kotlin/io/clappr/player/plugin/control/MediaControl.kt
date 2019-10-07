package io.clappr.player.plugin.control

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.support.v4.view.GestureDetectorCompat
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import io.clappr.player.R
import io.clappr.player.base.*
import io.clappr.player.base.keys.Action
import io.clappr.player.base.keys.Key
import io.clappr.player.components.Core
import io.clappr.player.components.Playback
import io.clappr.player.extensions.animate
import io.clappr.player.plugin.Plugin.State
import io.clappr.player.plugin.PluginEntry
import io.clappr.player.plugin.UIPlugin.Visibility
import io.clappr.player.plugin.core.UICorePlugin

open class MediaControl(core: Core, pluginName: String = name) : UICorePlugin(core, name = pluginName) {

    abstract class Plugin(core: Core, name: String) : UICorePlugin(core, name = name) {
        enum class Panel { TOP, BOTTOM, CENTER, NONE }
        enum class Position { LEFT, RIGHT, CENTER, NONE }

        open var panel: Panel = Panel.NONE
        open var position: Position = Position.NONE

        open val isEnabled: Boolean
            get() {
                return state == State.ENABLED
            }

        open val isPlaybackIdle: Boolean
            get() {
                return core.activePlayback?.state == Playback.State.IDLE ||
                        core.activePlayback?.state == Playback.State.NONE
            }
    }

    companion object : NamedType {
        override val name = "mediaControl"

        const val modalPanelViewKey = "modalPanelView"

        val entry = PluginEntry.Core(name = name, factory = { core -> MediaControl(core) })
    }

    protected val defaultShowDuration = 300L
    protected val longShowDuration = 3000L

    private val handler = Handler()

    private var lastInteractionTime = 0L

    var hideAnimationEnded = false

    override val view by lazy {
        LayoutInflater.from(applicationContext).inflate(R.layout.media_control, null) as FrameLayout
    }

    open val invalidActivationKeys = listOf(Key.UNDEFINED)
    private val navigationKeys = listOf(Key.UP, Key.DOWN, Key.LEFT, Key.RIGHT)

    private val backgroundView: View by lazy { view.findViewById(R.id.background_view) as View }

    private val controlsPanel by lazy { view.findViewById(R.id.controls_panel) as RelativeLayout }

    private val topCenterPanel by lazy { view.findViewById(R.id.top_center) as RelativeLayout }
    private val topPanel by lazy { view.findViewById(R.id.top_panel) as LinearLayout }
    private val topLeftPanel by lazy { view.findViewById(R.id.top_left_panel) as LinearLayout }
    private val topRightPanel by lazy { view.findViewById(R.id.top_right_panel) as LinearLayout }

    private val bottomPanel by lazy { view.findViewById(R.id.bottom_panel) as LinearLayout }
    private val bottomLeftPanel by lazy { view.findViewById(R.id.bottom_left_panel) as LinearLayout }
    private val bottomRightPanel by lazy { view.findViewById(R.id.bottom_right_panel) as LinearLayout }

    private val foregroundControlsPanel by lazy { view.findViewById(R.id.foreground_controls_panel) as FrameLayout }

    private val centerPanel by lazy { view.findViewById(R.id.center_panel) as LinearLayout }

    private val modalPanel by lazy { view.findViewById(R.id.modal_panel) as FrameLayout }

    private val controlPlugins = mutableListOf<Plugin>()

    override var state: State = State.ENABLED
        set(value) {
            if (value == State.ENABLED)
                view.visibility = View.VISIBLE
            else {
                hide()
                view.visibility = View.GONE
            }
            field = value
        }

    val isEnabled: Boolean
        get() = state == State.ENABLED

    protected val isVisible: Boolean
        get() = visibility == Visibility.VISIBLE

    private val isPlaybackIdle: Boolean
        get() {
            return core.activePlayback?.state == Playback.State.IDLE ||
                    core.activePlayback?.state == Playback.State.NONE
        }

    private val containerListenerIds = mutableListOf<String>()
    private val playbackListenerIds = mutableListOf<String>()


    private val doubleTapListener = MediaControlDoubleTapListener()
    private val gestureDetector = GestureDetectorCompat(applicationContext, MediaControlGestureDetector())
        .apply {
            setOnDoubleTapListener(doubleTapListener)
        }

    init {
        hideModalPanel()

        listenTo(core, InternalEvent.DID_CHANGE_ACTIVE_CONTAINER.value) { setupMediaControlEvents() }
        listenTo(core, InternalEvent.DID_CHANGE_ACTIVE_PLAYBACK.value) { setupPlaybackEvents() }

        listenTo(core, InternalEvent.DID_UPDATE_INTERACTING.value) { updateInteractionTime() }
        listenTo(core, InternalEvent.DID_TOUCH_MEDIA_CONTROL.value) { updateInteractionTime() }

        listenTo(core, InternalEvent.OPEN_MODAL_PANEL.value) { openModal() }
        listenTo(core, InternalEvent.CLOSE_MODAL_PANEL.value) { closeModal() }

        listenTo(core, Event.DID_RECEIVE_INPUT_KEY.value) { onInputReceived(it) }
    }

    open fun handleDidPauseEvent() {
        if (!modalPanelIsOpen()) show()
    }

    open fun show(duration: Long) {
        val shouldAnimate = isVisible.not()

        core.trigger(InternalEvent.WILL_SHOW_MEDIA_CONTROL.value)

        showMediaControlElements()
        showDefaultMediaControlPanels()

        if (shouldAnimate) animateFadeIn(view) { setupShowDuration(duration) }
        else setupShowDuration(duration)
    }

    private fun setupShowDuration(duration: Long) {
        updateInteractionTime()

        if (!isPlaybackIdle && duration > 0) {
            hideDelayed(duration)
        }

        core.trigger(InternalEvent.DID_SHOW_MEDIA_CONTROL.value)
    }

    private fun animateFadeIn(view: View, onAnimationEnd: () -> Unit = {}) {
        view.animate(R.anim.anim_media_control_fade_in) {
            onAnimationEnd()
        }
    }

    private fun setupMediaControlEvents() {
        stopContainerListeners()

        core.activeContainer?.let {
            containerListenerIds.add(listenTo(it, InternalEvent.ENABLE_MEDIA_CONTROL.value) { state = State.ENABLED })
            containerListenerIds.add(listenTo(it, InternalEvent.DISABLE_MEDIA_CONTROL.value) { state = State.DISABLED })
            containerListenerIds.add(listenTo(it, InternalEvent.WILL_LOAD_SOURCE.value) {
                state = State.ENABLED
                hide()
            })
        }
    }

    private fun setupPlaybackEvents() {
        stopPlaybackListeners()

        core.activePlayback?.let {
            playbackListenerIds.add(listenTo(it, Event.DID_PAUSE.value) {
                handleDidPauseEvent()
            })
        }
    }

    private fun modalPanelIsOpen() = modalPanel.visibility == View.VISIBLE

    private fun setupPlugins() {
        controlPlugins.clear()

        with(core.plugins.filterIsInstance(MediaControl.Plugin::class.java)) {
            core.options[ClapprOption.MEDIA_CONTROL_PLUGINS.value]?.let {
                controlPlugins.addAll(orderedPlugins(this, it.toString()))
            } ?: controlPlugins.addAll(this)
        }
    }

    private fun orderedPlugins(list: List<Plugin>, order: String): List<Plugin> {
        val pluginsOrder = order.replace(" ", "").split(",")
        return list.sortedWith(compareBy { pluginsOrder.indexOf(it.name) })
    }

    private fun layoutPlugins() {
        controlPlugins.forEach {
            (it.view?.parent as? ViewGroup)?.removeView(it.view)
            val parent = when (it.panel) {
                Plugin.Panel.TOP ->
                    when (it.position) {
                        Plugin.Position.LEFT -> topLeftPanel
                        Plugin.Position.RIGHT -> topRightPanel
                        Plugin.Position.CENTER -> topCenterPanel
                        else -> topPanel
                    }
                Plugin.Panel.BOTTOM ->
                    when (it.position) {
                        Plugin.Position.LEFT -> bottomLeftPanel
                        Plugin.Position.RIGHT -> bottomRightPanel
                        else -> bottomPanel
                    }
                Plugin.Panel.CENTER ->
                    centerPanel
                else -> null
            }
            parent?.addView(it.view)
        }
    }

    private fun showDefaultMediaControlPanels() {
        controlsPanel.visibility = View.VISIBLE
        foregroundControlsPanel.visibility = View.VISIBLE
    }

    private fun hideDefaultMediaControlPanels() {
        controlsPanel.visibility = View.INVISIBLE
        foregroundControlsPanel.visibility = View.INVISIBLE
    }

    private fun hideMediaControlElements() {
        visibility = Visibility.HIDDEN
        backgroundView.visibility = View.INVISIBLE
    }

    private fun showMediaControlElements() {
        visibility = Visibility.VISIBLE
        backgroundView.visibility = View.VISIBLE
    }

    private fun hideDelayed(duration: Long) {
        handler.postDelayed({
            val currentTime = SystemClock.elapsedRealtime()
            val elapsedTime = currentTime - lastInteractionTime
            val playing = (core.activePlayback?.state == Playback.State.PLAYING)
            if (elapsedTime >= duration && playing) {
                hide()
            } else {
                hideDelayed(duration)
            }
        }, duration)
    }

    protected fun updateInteractionTime() {
        lastInteractionTime = SystemClock.elapsedRealtime()
    }

    protected fun toggleVisibility() {
        if (isEnabled) {
            if (isVisible) {
                hide()
            } else {
                show(longShowDuration)
            }
        }
    }

    private fun openModal() {
        core.activePlayback?.pause()

        hideDefaultMediaControlPanels()

        animateFadeIn(modalPanel) { showModalPanel() }

        val bundle = Bundle()
        val map = hashMapOf<String, Any>(modalPanelViewKey to modalPanel)
        bundle.putSerializable(modalPanelViewKey, map)
        core.trigger(InternalEvent.DID_OPEN_MODAL_PANEL.value, bundle)
    }

    private fun closeModal() {
        if (modalPanelIsOpen()) showDefaultMediaControlPanels()

        animateFadeOut(modalPanel) { hideModalPanel() }
        core.trigger(InternalEvent.DID_CLOSE_MODAL_PANEL.value)
    }

    private fun hideModalPanel() {
        modalPanel.visibility = View.INVISIBLE
    }

    private fun showModalPanel() {
        modalPanel.visibility = View.VISIBLE
    }

    private fun onInputReceived(bundle: Bundle?) {
        bundle?.let {
            val keyCode = it.getString(EventData.INPUT_KEY_CODE.value) ?: ""
            val keyAction = it.getString(EventData.INPUT_KEY_ACTION.value) ?: ""
            val key = Key.getByValue(keyCode) ?: Key.UNDEFINED
            val action = Action.getByValue(keyAction)

            if (isValidActivationKey(key) && action == Action.UP) {
                when (isVisible) {
                    true -> if (navigationKeys.contains(key)) updateInteractionTime()
                    else -> if (isValidActivationKey(key)) toggleVisibility()
                }
            }
        }
    }

    private fun isValidActivationKey(key: Key) = invalidActivationKeys.contains(key).not()

    private fun stopContainerListeners() {
        containerListenerIds.forEach(::stopListening)
        containerListenerIds.clear()
    }

    private fun stopPlaybackListeners() {
        playbackListenerIds.forEach(::stopListening)
        playbackListenerIds.clear()
    }

    private fun animateFadeOut(view: View, onAnimationEnd: () -> Unit = {}) {
        view.animate(R.anim.anim_media_control_fade_out) {
            onAnimationEnd()
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected fun setBackground(resource : Int) {
        backgroundView.background = applicationContext.getDrawable(resource)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun render() {
        super.render()

        view.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }

        setupPlugins()
        Handler().post { layoutPlugins() }
        hide()
        hideModalPanel()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun destroy() {
        controlPlugins.clear()
        stopContainerListeners()
        stopPlaybackListeners()
        view.setOnTouchListener(null)
        handler.removeCallbacksAndMessages(null)
        super.destroy()
    }

    override fun hide() {
        hideAnimationEnded = false

        if (isEnabled && isPlaybackIdle) return

        core.trigger(InternalEvent.WILL_HIDE_MEDIA_CONTROL.value)

        animateFadeOut(view) {
            hideMediaControlElements()
            hideDefaultMediaControlPanels()
            hideModalPanel()
            hideAnimationEnded = true

            core.trigger(InternalEvent.DID_HIDE_MEDIA_CONTROL.value)
        }
    }

    override fun show() {
        show(defaultShowDuration)
    }

    class MediaControlGestureDetector : GestureDetector.OnGestureListener {
        override fun onDown(e: MotionEvent?) = true

        override fun onShowPress(e: MotionEvent?) {}

        override fun onSingleTapUp(e: MotionEvent?) = false

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float) = false

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float) = false

        override fun onLongPress(e: MotionEvent?) {}
    }

    inner class MediaControlDoubleTapListener : GestureDetector.OnDoubleTapListener {
        override fun onDoubleTap(event: MotionEvent?): Boolean {
            triggerDoubleTapEvent(event)
            hide()
            return true
        }

        override fun onDoubleTapEvent(e: MotionEvent?) = false
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            toggleVisibility()
            return true
        }
    }

    private fun triggerDoubleTapEvent(event: MotionEvent?) {
        Bundle().apply {
            putInt(InternalEventData.HEIGHT.value, view.height)
            putInt(InternalEventData.WIDTH.value, view.width)

            event?.let {
                putFloat(InternalEventData.TOUCH_X_AXIS.value, it.x)
                putFloat(InternalEventData.TOUCH_Y_AXIS.value, it.y)
            }
        }.also {
            core.trigger(InternalEvent.DID_DOUBLE_TOUCH_MEDIA_CONTROL.value, it)
        }
    }

}