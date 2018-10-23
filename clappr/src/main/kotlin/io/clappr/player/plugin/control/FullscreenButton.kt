package io.clappr.player.plugin.control

import io.clappr.player.R
import io.clappr.player.base.Callback
import io.clappr.player.base.Event
import io.clappr.player.base.InternalEvent
import io.clappr.player.base.NamedType
import io.clappr.player.components.Core


open class FullscreenButton(core: Core) : ButtonPlugin(core) {

    companion object : NamedType {
        override val name = "fullscreenButton"
    }

    override var panel: Panel = Panel.BOTTOM
    override var position: Position = Position.RIGHT

    override val resourceDrawable: Int
        get() = R.drawable.fullscreen_button

    override val idResourceDrawable: Int
        get() = R.id.fullscreen_button

    override val resourceLayout: Int
        get() = R.layout.bottom_panel_button_plugin

    private val playbackListenerIds = mutableListOf<String>()

    init {
        bindCoreEvents()
    }

    open fun bindCoreEvents() {
        val bindEventsCallback = Callback.wrap {
            bindPlaybackEvents()
            updateState()
        }
        val updateStateCallback = Callback.wrap { updateState() }

        listenTo(core, InternalEvent.DID_CHANGE_ACTIVE_PLAYBACK.value, bindEventsCallback)
        listenTo(core, InternalEvent.DID_CHANGE_ACTIVE_CONTAINER.value, bindEventsCallback)
        listenTo(core, InternalEvent.DID_ENTER_FULLSCREEN.value, updateStateCallback)
        listenTo(core, InternalEvent.DID_EXIT_FULLSCREEN.value, updateStateCallback)
    }

    open fun bindPlaybackEvents() {
        stopPlaybackListeners()

        core.activePlayback?.let {
            playbackListenerIds.add(listenTo(it, Event.PLAYING.value, Callback.wrap { _ -> updateState() }))
            playbackListenerIds.add(listenTo(it, Event.DID_COMPLETE.value, Callback.wrap { _ -> hide() }))
        }
    }

    private fun stopPlaybackListeners() {
        playbackListenerIds.forEach(::stopListening)
        playbackListenerIds.clear()
    }

    private fun updateState() {
        if (isPlaybackIdle) {
            hide()
        } else {
            when (core.fullscreenState) {
                Core.FullscreenState.FULLSCREEN -> showExitFullscreenIcon()
                Core.FullscreenState.EMBEDDED -> showEnterFullscreenIcon()
            }
        }
    }

    private fun showEnterFullscreenIcon() {
        show()
        view.isSelected = false
    }

    private fun showExitFullscreenIcon() {
        show()
        view.isSelected = true
    }

    override fun onClick() {
        core.trigger(InternalEvent.DID_TOUCH_MEDIA_CONTROL.value)
        when (core.fullscreenState) {
            Core.FullscreenState.FULLSCREEN -> core.trigger(Event.EXIT_FULLSCREEN.value)
            Core.FullscreenState.EMBEDDED -> core.trigger(Event.REQUEST_FULLSCREEN.value)
        }
    }

    override fun render() {
        super.render()
        removeDefaultRightPadding()
        updateState()
    }

    //Because full screen button is the last button
    private fun removeDefaultRightPadding() {
        val leftPadding = context?.resources?.getDimensionPixelOffset(R.dimen.bottom_panel_button_left_padding)
                ?: 0
        val verticalPadding = context?.resources?.getDimensionPixelOffset(R.dimen.fullscreen_button_vertical_padding)
                ?: 0
        view.setPadding(leftPadding, verticalPadding, 0, verticalPadding)
    }

    override fun destroy() {
        stopPlaybackListeners()
        super.destroy()
    }
}