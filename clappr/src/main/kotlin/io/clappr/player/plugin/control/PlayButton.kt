package io.clappr.player.plugin.control

import android.support.annotation.Keep
import io.clappr.player.R
import io.clappr.player.base.Callback
import io.clappr.player.base.Event
import io.clappr.player.base.InternalEvent
import io.clappr.player.base.NamedType
import io.clappr.player.components.Core
import io.clappr.player.components.Playback

@Keep
open class PlayButton(core: Core) : ButtonPlugin(core) {

    @Keep
    companion object : NamedType {
        override val name: String?
            get() = "playButton"
    }

    override var panel: Panel = Panel.CENTER

    override val resourceDrawable: Int
        get() = R.drawable.play_button

    override val idResourceDrawable: Int
        get() = R.id.play_pause_button

    override val resourceLayout: Int
        get() = R.layout.button_plugin

    private var scrubbing = false
        set(value) {
            field = value
            if (value) hide() else show()
        }

    init {
        bindEventListeners()
    }

    open fun bindEventListeners() {
        stopListening()
        bindCoreEvents()
        updateState()
        core.activeContainer?.let {
            listenTo(it, InternalEvent.DID_CHANGE_PLAYBACK.value, Callback.wrap { bindEventListeners() })
        }
        core.activePlayback?.let {
            val updateStateCallback = Callback.wrap { updateState() }
            listenTo(it, Event.DID_PAUSE.value, updateStateCallback)
            listenTo(it, Event.DID_STOP.value, updateStateCallback)
            listenTo(it, Event.DID_COMPLETE.value, updateStateCallback)
            listenTo(it, Event.PLAYING.value, updateStateCallback)
            listenTo(it, Event.STALLED.value, updateStateCallback)
        }
    }

    open fun bindCoreEvents() {
        listenTo(core, InternalEvent.DID_CHANGE_ACTIVE_CONTAINER.value, Callback.wrap { bindEventListeners() })
        //TODO: USE IT WHEN SEEKBAR_PLUGIN IS CREATED ON CLAPPR
//        listenTo(core, io.clappr.player.base.InternalEvent.WILL_BEGIN_SCRUBBING.value, Callback.wrap { scrubbing = true })
//        listenTo(core, io.clappr.player.base.InternalEvent.DID_FINISH_SCRUBBING.value, Callback.wrap { scrubbing = false })
    }

    open fun updateState() {
        core.activePlayback?.let {
            when (it.state) {
                Playback.State.STALLED -> hide()
                Playback.State.PLAYING -> if (it.canPause) showPauseIcon() else showStopIcon()
                else -> showPlayIcon()
            }
        }
    }

    open fun showPauseIcon() {
        if (!scrubbing) show()
        view.isSelected = true
    }

    open fun showPlayIcon() {
        if (!scrubbing) show()
        view.isSelected = false
    }


    open fun showStopIcon() {
        // There's no stop icon for current Andy interface
        hide()
    }

    override fun onClick() {
        core.trigger(InternalEvent.DID_TOUCH_MEDIA_CONTROL.value)
        core.activePlayback?.let {
            if (it.state == Playback.State.PLAYING) {
                if (it.canPause) it.pause() else it.stop()
            } else if (it.canPlay) {
                it.play()
            }
            it
        }
    }

    override fun render() {
        super.render()
        updateState()
    }
}