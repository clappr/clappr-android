package io.clappr.player.plugin.control

import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import io.clappr.player.R
import io.clappr.player.base.ClapprOption
import io.clappr.player.base.Event
import io.clappr.player.base.InternalEvent
import io.clappr.player.base.NamedType
import io.clappr.player.components.Core
import io.clappr.player.components.Playback
import io.clappr.player.plugin.Plugin.State
import io.clappr.player.plugin.PluginEntry
import io.clappr.player.plugin.UIPlugin.Visibility
import io.clappr.player.plugin.core.UICorePlugin

class MediaControl(core: Core) : UICorePlugin(core, name = name) {

    abstract class Plugin(core: Core, name: String) : UICorePlugin(core, name = name) {
        enum class Panel { TOP, BOTTOM, CENTER, NONE }
        enum class Position { LEFT, RIGHT, NONE }

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

    private val defaultShowTimeout = 300L
    private val longShowTimeout = 4000L

    private val handler = Handler()

    private var lastInteractionTime = 0L

    override val view by lazy {
        LayoutInflater.from(applicationContext).inflate(R.layout.media_control, null) as FrameLayout
    }

    private val backgroundView: View by lazy { view.findViewById(R.id.background_view) as View }

    private val controlsPanel by lazy { view.findViewById(R.id.controls_panel) as RelativeLayout }

    private val topPanel by lazy { view.findViewById(R.id.top_panel) as LinearLayout }
    private val topLeftPanel by lazy { view.findViewById(R.id.top_left_panel) as LinearLayout }
    private val topRightPanel by lazy { view.findViewById(R.id.top_right_panel) as LinearLayout }

    private val bottomPanel by lazy { view.findViewById(R.id.bottom_panel) as LinearLayout }
    private val bottomLeftPanel by lazy { view.findViewById(R.id.bottom_left_panel) as LinearLayout }
    private val bottomRightPanel by lazy { view.findViewById(R.id.bottom_right_panel) as LinearLayout }

    private val foregroundControlsPanel by lazy { view.findViewById(R.id.foreground_controls_panel) as FrameLayout }

    private val centerPanel by lazy { view.findViewById(R.id.center_panel) as LinearLayout }

    private val modalPanel by lazy { view.findViewById(R.id.modal_panel) as FrameLayout }

    private val controlPlugins = mutableListOf<MediaControl.Plugin>()

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
        get() {
            return state == State.ENABLED
        }

    private val isPlaybackIdle: Boolean
        get() {
            return core.activePlayback?.state == Playback.State.IDLE ||
                    core.activePlayback?.state == Playback.State.NONE
        }

    private val containerListenerIds = mutableListOf<String>()
    private val playbackListenerIds = mutableListOf<String>()

    init {
        setupPanelsVisibility()
        view.setOnClickListener { toggleVisibility() }
        listenTo(core, InternalEvent.DID_CHANGE_ACTIVE_CONTAINER.value) { setupMediaControlEvents() }
        listenTo(core, InternalEvent.DID_CHANGE_ACTIVE_PLAYBACK.value) { setupPlaybackEvents() }

        listenTo(core, InternalEvent.DID_UPDATE_INTERACTING.value) { updateInteractionTime() }
        listenTo(core, InternalEvent.DID_TOUCH_MEDIA_CONTROL.value) { updateInteractionTime() }

        listenTo(core, InternalEvent.OPEN_MODAL_PANEL.value) { openModal() }
        listenTo(core, InternalEvent.CLOSE_MODAL_PANEL.value) { closeModal() }
    }

    private fun setupPanelsVisibility() {
        modalPanel.visibility = View.INVISIBLE
    }

    private fun setupMediaControlEvents() {
        stopContainerListeners()

        core.activeContainer?.let {
            containerListenerIds.add(listenTo(it, InternalEvent.ENABLE_MEDIA_CONTROL.value) { state = State.ENABLED })
            containerListenerIds.add(listenTo(it, InternalEvent.DISABLE_MEDIA_CONTROL.value) { state = State.DISABLED })
            containerListenerIds.add(listenTo(it, InternalEvent.WILL_LOAD_SOURCE.value) { hide() })
        }
    }

    private fun setupPlaybackEvents() {
        stopPlaybackListeners()

        core.activePlayback?.let {
            playbackListenerIds.add(listenTo(it, Event.DID_PAUSE.value) {
                if (!modalPanelIsOpen())
                    show()
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

    private fun orderedPlugins(list: List<Plugin>, order: String): List<Plugin>{
        val pluginsOrder = order.replace(" ", "").split(",")
        return list.sortedWith(compareBy { pluginsOrder.indexOf(it.name) })
    }

    private fun layoutPlugins() {
        controlPlugins.forEach {
            (it.view?.parent as? ViewGroup)?.removeView(it.view)
            val parent = when (it.panel) {
                MediaControl.Plugin.Panel.TOP ->
                    when (it.position) {
                        MediaControl.Plugin.Position.LEFT -> topLeftPanel
                        MediaControl.Plugin.Position.RIGHT -> topRightPanel
                        else -> topPanel
                    }
                MediaControl.Plugin.Panel.BOTTOM ->
                    when (it.position) {
                        MediaControl.Plugin.Position.LEFT -> bottomLeftPanel
                        MediaControl.Plugin.Position.RIGHT -> bottomRightPanel
                        else -> bottomPanel
                    }
                MediaControl.Plugin.Panel.CENTER ->
                    centerPanel
                else -> null
            }
            parent?.addView(it.view)
        }
    }

    override fun hide() {
        if (isEnabled && isPlaybackIdle) return

        core.trigger(InternalEvent.WILL_HIDE_MEDIA_CONTROL.value)
        visibility = Visibility.HIDDEN
        backgroundView.visibility = View.INVISIBLE
        controlsPanel.visibility = View.INVISIBLE
        foregroundControlsPanel.visibility = View.INVISIBLE
        hideModalPanel()
        core.trigger(InternalEvent.DID_HIDE_MEDIA_CONTROL.value)
    }

    private fun hideDelayed(timeout: Long) {
        handler.postDelayed({
            val currentTime = SystemClock.elapsedRealtime()
            val elapsedTime = currentTime - lastInteractionTime
            val playing = (core.activePlayback?.state == Playback.State.PLAYING)
            if (elapsedTime >= timeout && playing) {
                hide()
            } else {
                hideDelayed(timeout)
            }
        }, timeout)
    }

    override fun show() {
        show(defaultShowTimeout)
    }

    private fun show(timeout: Long) {
        core.trigger(InternalEvent.WILL_SHOW_MEDIA_CONTROL.value)
        visibility = Visibility.VISIBLE
        backgroundView.visibility = View.VISIBLE
        showDefaultMediaControlPanels()

        lastInteractionTime = SystemClock.elapsedRealtime()

        if (!isPlaybackIdle && timeout > 0) {
            hideDelayed(timeout)
        }
        core.trigger(InternalEvent.DID_SHOW_MEDIA_CONTROL.value)
    }

    private fun updateInteractionTime() {
        lastInteractionTime = SystemClock.elapsedRealtime()
    }

    private fun toggleVisibility() {
        if (isEnabled) {
            if (visibility == Visibility.VISIBLE) {
                hide()
            } else {
                show(longShowTimeout)
            }
        }
    }

    private fun openModal() {
        controlsPanel.visibility = View.INVISIBLE
        foregroundControlsPanel.visibility = View.INVISIBLE
        modalPanel.visibility = View.VISIBLE

        val bundle = Bundle()
        val map = hashMapOf<String, Any>(modalPanelViewKey to modalPanel)
        bundle.putSerializable(modalPanelViewKey, map)
        core.trigger(InternalEvent.DID_OPEN_MODAL_PANEL.value, bundle)
    }

    private fun closeModal() {
        if(modalPanel.visibility == View.VISIBLE)
            showDefaultMediaControlPanels()

        hideModalPanel()
        core.trigger(InternalEvent.DID_CLOSE_MODAL_PANEL.value)
    }

    private fun showDefaultMediaControlPanels() {
        controlsPanel.visibility = View.VISIBLE
        foregroundControlsPanel.visibility = View.VISIBLE
    }

    private fun hideModalPanel() {
        modalPanel.visibility = View.INVISIBLE
    }

    override fun render() {
        setupPlugins()
        Handler().post { layoutPlugins() }
        hide()
        hideModalPanel()
    }

    override fun destroy() {
        controlPlugins.clear()
        stopContainerListeners()
        stopPlaybackListeners()
        view.setOnClickListener(null)
        handler.removeCallbacksAndMessages(null)
        super.destroy()
    }

    private fun stopContainerListeners() {
        containerListenerIds.forEach(::stopListening)
        containerListenerIds.clear()
    }

    private fun stopPlaybackListeners() {
        playbackListenerIds.forEach(::stopListening)
        playbackListenerIds.clear()
    }
}