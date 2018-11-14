package io.clappr.player.app.plugin

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import io.clappr.player.base.Callback
import io.clappr.player.base.Event
import io.clappr.player.base.InternalEvent
import io.clappr.player.base.NamedType
import io.clappr.player.components.Container
import io.clappr.player.plugin.container.UIContainerPlugin


open class LabelPlugin(container: Container) : UIContainerPlugin(container) {

    companion object : NamedType {
        override val name = "label"
    }

    override val view: View = LinearLayout(context)

    private var label = TextView(context)

    private val playbackListenerIds = mutableListOf<String>()

    init {
        setupLayout()
        bindEventListeners()
    }

    private fun bindEventListeners() {
        listenTo(container, InternalEvent.DID_CHANGE_PLAYBACK.value, Callback.wrap { bindLoadingVisibilityCallBacks() })
    }

    private fun bindLoadingVisibilityCallBacks() {
        stopPlaybackListeners()

        container.playback?.let {
            playbackListenerIds.add(listenTo(it, Event.STALLED.value, updateLabel("loading")))
            playbackListenerIds.add(listenTo(it, Event.PLAYING.value, hideLabel()))
            playbackListenerIds.add(listenTo(it, Event.DID_PAUSE.value, updateLabel("paused")))
            playbackListenerIds.add(listenTo(it, Event.DID_STOP.value, updateLabel("stopped")))
            playbackListenerIds.add(listenTo(it, Event.DID_COMPLETE.value, updateLabel("completed")))
            playbackListenerIds.add(listenTo(it, Event.ERROR.value, updateLabel("failed")))
        }
    }

    private fun stopPlaybackListeners() {
        playbackListenerIds.forEach(::stopListening)
        playbackListenerIds.clear()
    }

    private fun hideLabel(): Callback {
        return Callback.wrap {
            hide()
        }
    }

    private fun updateLabel(text: String): Callback {
        return Callback.wrap {
            label.text = "Status: $text"

            view.bringToFront()
            show()
        }
    }

    private fun setupLayout() {
        (view as? LinearLayout)?.let {
            it.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            it.gravity = Gravity.END

            label.setTextColor(Color.BLACK)
            label.setBackgroundColor(Color.WHITE)
            label.setPadding(5,2,5,2)

            it.addView(label)
        }
    }

    override fun destroy() {
        stopPlaybackListeners()
        super.destroy()
    }
}