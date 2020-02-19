package io.clappr.player.plugin.control

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import io.clappr.player.R
import io.clappr.player.base.*
import io.clappr.player.base.keys.Action
import io.clappr.player.base.keys.Key
import io.clappr.player.components.Core
import io.clappr.player.components.Playback
import io.clappr.player.extensions.animate
import io.clappr.player.extensions.extractInputKey
import io.clappr.player.extensions.unlessChromeless
import io.clappr.player.plugin.Plugin.State
import io.clappr.player.plugin.PluginEntry
import io.clappr.player.plugin.UIPlugin.Visibility
import io.clappr.player.plugin.core.UICorePlugin

typealias Millisecond = Long

open class MediaControl(core: Core, pluginName: String = name) :
    UICorePlugin(core, name = pluginName) {

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

        val entry = PluginEntry.Core(
            name = name,
            factory = { core: Core -> MediaControl(core) }.unlessChromeless()
        )
    }

    private val defaultShowDuration: Millisecond = 300L

    private val handler = Handler()

    private var lastInteractionTime = 0L
    private var canShowMediaControlWhenPauseAfterTapInteraction = true

    var hideAnimationEnded = false

    val longShowDuration: Millisecond = 3000L

    override val view by lazy {
        LayoutInflater.from(applicationContext).inflate(R.layout.media_control, null) as FrameLayout
    }

    protected open val keysNotAllowedToIteractWithMediaControl = listOf(Key.UNDEFINED)
    private val navigationKeys = listOf(Key.UP, Key.DOWN, Key.LEFT, Key.RIGHT)
    protected open val allowedKeysToToggleMediaControlVisibility = navigationKeys

    private val backgroundView: View by lazy { view.findViewById(R.id.background_view) as View }

    private val controlsPanel by lazy { view.findViewById(R.id.controls_panel) as RelativeLayout }

    private val topCenterPanel by lazy { view.findViewById(R.id.top_center) as ViewGroup }
    private val topPanel by lazy { view.findViewById(R.id.top_panel) as LinearLayout }
    private val topLeftPanel by lazy { view.findViewById(R.id.top_left_panel) as LinearLayout }
    private val topRightPanel by lazy { view.findViewById(R.id.top_right_panel) as LinearLayout }

    private val bottomPanel by lazy { view.findViewById(R.id.bottom_panel) as LinearLayout }
    private val bottomLeftPanel by lazy { view.findViewById(R.id.bottom_left_panel) as LinearLayout }
    private val bottomRightPanel by lazy { view.findViewById(R.id.bottom_right_panel) as LinearLayout }

    private val foregroundControlsPanel by lazy { view.findViewById(R.id.foreground_controls_panel) as FrameLayout }

    private val centerPanel by lazy { view.findViewById(R.id.center_panel) as LinearLayout }

    protected val modalPanel by lazy { view.findViewById(R.id.modal_panel) as FrameLayout }

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

    private val isPlaybackPlaying get() = core.activePlayback?.state == Playback.State.PLAYING
    private val isPlaybackIdle: Boolean
        get() {
            return core.activePlayback?.state == Playback.State.IDLE ||
                    core.activePlayback?.state == Playback.State.NONE
        }

    private val containerListenerIds = mutableListOf<String>()
    private val playbackListenerIds = mutableListOf<String>()


    private val doubleTapListener = MediaControlDoubleTapListener()
    private val gestureDetector =
        GestureDetectorCompat(applicationContext, MediaControlGestureDetector())
            .apply {
                setOnDoubleTapListener(doubleTapListener)
            }

    init {
        hideModalPanel()

        listenTo(
            core,
            InternalEvent.DID_CHANGE_ACTIVE_CONTAINER.value
        ) { setupMediaControlEvents() }
        listenTo(core, InternalEvent.DID_CHANGE_ACTIVE_PLAYBACK.value) { setupPlaybackEvents() }

        listenTo(core, InternalEvent.DID_UPDATE_INTERACTING.value) { updateInteractionTime() }
        listenTo(core, InternalEvent.DID_TOUCH_MEDIA_CONTROL.value) { updateInteractionTime() }

        listenTo(core, InternalEvent.OPEN_MODAL_PANEL.value) { openModal() }
        listenTo(core, InternalEvent.CLOSE_MODAL_PANEL.value) { closeModal() }

        listenTo(core, Event.DID_RECEIVE_INPUT_KEY.value) { onInputReceived(it) }
    }

    open fun handleDidPauseEvent() {
        if (!modalPanelIsOpen() && canShowMediaControlWhenPauseAfterTapInteraction) show()

        canShowMediaControlWhenPauseAfterTapInteraction = true
    }

    open fun show(duration: Millisecond) {
        val shouldAnimate = isVisible.not()

        core.trigger(InternalEvent.WILL_SHOW_MEDIA_CONTROL.value)

        showMediaControlElements()
        showDefaultMediaControlPanels()

        if (shouldAnimate) animateFadeIn(view) { setupShowDuration(duration) }
        else setupShowDuration(duration)
    }

    private fun setupShowDuration(duration: Millisecond) {
        updateInteractionTime()

        if (duration > 0) {
            hideDelayedWithCleanHandler(duration)
        }

        core.trigger(InternalEvent.DID_SHOW_MEDIA_CONTROL.value)
    }

    open fun animateFadeIn(view: View, onAnimationEnd: () -> Unit = {}) {
        view.animate(R.anim.anim_media_control_fade_in) {
            onAnimationEnd()
        }
    }

    private fun setupMediaControlEvents() {
        stopContainerListeners()

        core.activeContainer?.let {
            containerListenerIds.add(
                listenTo(
                    it,
                    InternalEvent.ENABLE_MEDIA_CONTROL.value
                ) { state = State.ENABLED })
            containerListenerIds.add(
                listenTo(
                    it,
                    InternalEvent.DISABLE_MEDIA_CONTROL.value
                ) { state = State.DISABLED })
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

    protected fun modalPanelIsOpen() = modalPanel.visibility == View.VISIBLE

    private fun setupPlugins() {
        controlPlugins.clear()

        with(core.plugins.filterIsInstance(Plugin::class.java)) {
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
            it.render()
        }
    }

    protected fun showDefaultMediaControlPanels() {
        controlsPanel.visibility = View.VISIBLE
        foregroundControlsPanel.visibility = View.VISIBLE
    }

    open fun hideDefaultMediaControlPanels() {
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

    protected fun hideDelayedWithCleanHandler(duration: Millisecond) {
        cancelPendingHideDelayed()
        hideDelayed(duration)
    }

    private fun hideDelayed(duration: Millisecond) {
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

    private fun updateInteractionTime() {
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

    open fun openModal() {
        core.activePlayback?.pause()

        hideDefaultMediaControlPanels()

        animateFadeIn(modalPanel) { showModalPanel() }

        val bundle = Bundle()
        val map = hashMapOf<String, Any>(modalPanelViewKey to modalPanel)
        bundle.putSerializable(modalPanelViewKey, map)
        core.trigger(InternalEvent.DID_OPEN_MODAL_PANEL.value, bundle)
    }

    open fun closeModal() {
        if (modalPanelIsOpen()) {
            showDefaultMediaControlPanels()
            animateFadeOut(modalPanel) { hideModalPanel() }
        } else hideModalPanel()

        core.trigger(InternalEvent.DID_CLOSE_MODAL_PANEL.value)
    }

    protected fun hideModalPanel() {
        modalPanel.visibility = View.INVISIBLE
    }

    protected fun showModalPanel() {
        modalPanel.visibility = View.VISIBLE
    }

    private fun onInputReceived(bundle: Bundle?) {
        bundle?.let { handleInputKey(it) }
    }

    private fun handleInputKey(bundle: Bundle) {
        bundle.extractInputKey()?.apply {
            if (isKeysAllowedToIteractWithMediaControl(key) && action == Action.UP) {
                when (isVisible) {
                    true -> if (navigationKeys.contains(key)) updateInteractionTime()
                    else -> if (allowedKeysToToggleMediaControlVisibility.contains(key)) toggleVisibility()
                }
            }
        }
    }

    private fun isKeysAllowedToIteractWithMediaControl(key: Key) = keysNotAllowedToIteractWithMediaControl.contains(key).not()

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

    protected fun setBackground(context: Context, resource: Int) {
        backgroundView.background = ContextCompat.getDrawable(context, resource)
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
        cancelPendingHideDelayed()
        super.destroy()
    }

    override fun hide() {
        if (isEnabled && isPlaybackIdle) return

        hideAnimationEnded = false

        core.trigger(InternalEvent.WILL_HIDE_MEDIA_CONTROL.value)

        if (isVisible) animateFadeOut(view) { hideMediaControl() }
        else hideMediaControl()
    }

    private fun hideMediaControl() {
        hideMediaControlElements()
        hideDefaultMediaControlPanels()
        hideModalPanel()
        hideAnimationEnded = true

        cancelPendingHideDelayed()
        core.trigger(InternalEvent.DID_HIDE_MEDIA_CONTROL.value)
    }

    protected fun cancelPendingHideDelayed() {
        handler.removeCallbacksAndMessages(null)
    }

    override fun show() {
        show(defaultShowDuration)
    }

    class MediaControlGestureDetector : GestureDetector.OnGestureListener {
        override fun onDown(e: MotionEvent?) = true

        override fun onShowPress(e: MotionEvent?) {}

        override fun onSingleTapUp(e: MotionEvent?) = false

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ) = false

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ) = false

        override fun onLongPress(e: MotionEvent?) {}
    }

    inner class MediaControlDoubleTapListener : GestureDetector.OnDoubleTapListener {
        override fun onDoubleTap(event: MotionEvent?): Boolean {
            canShowMediaControlWhenPauseAfterTapInteraction = isPlaybackPlaying
            triggerDoubleTapEvent(event)
            hide()
            return true
        }

        override fun onDoubleTapEvent(e: MotionEvent?) = false
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            canShowMediaControlWhenPauseAfterTapInteraction = true
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