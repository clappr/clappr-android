package io.clappr.player.app.plugin

import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.squareup.picasso.Picasso
import io.clappr.player.app.R
import io.clappr.player.base.*
import io.clappr.player.components.Container
import io.clappr.player.components.Core
import io.clappr.player.plugin.container.UIContainerPlugin
import io.clappr.player.plugin.core.UICorePlugin


open class PlaybackStatusPlugin(container: Container) : UIContainerPlugin(container) {

    companion object : NamedType {
        override val name = "playbackStatus"
    }

    override val view by lazy {
        LayoutInflater.from(context).inflate(R.layout.playback_status_plugin, null) as RelativeLayout
    }

    open val status by lazy { view.findViewById(R.id.status) as TextView }

    private val playbackListenerIds = mutableListOf<String>()

    init {
        bindContainerEvents()
    }

    private fun bindContainerEvents() {
        listenTo(container, InternalEvent.DID_CHANGE_PLAYBACK.value, Callback.wrap {
            hide()
            bindPlaybackEvents()
        })
    }

    private fun bindPlaybackEvents() {
        stopPlaybackListeners()

        container.playback?.let {
            playbackListenerIds.add(listenTo(it, Event.STALLING.value, updateLabel(Event.STALLING.value)))
            playbackListenerIds.add(listenTo(it, Event.PLAYING.value, updateLabel(Event.PLAYING.value)))
            playbackListenerIds.add(listenTo(it, Event.DID_PAUSE.value, updateLabel(Event.DID_PAUSE.value)))
            playbackListenerIds.add(listenTo(it, Event.DID_STOP.value, updateLabel(Event.DID_STOP.value)))
            playbackListenerIds.add(listenTo(it, Event.DID_COMPLETE.value, updateLabel(Event.DID_COMPLETE.value)))
            playbackListenerIds.add(listenTo(it, Event.ERROR.value, updateLabel(Event.ERROR.value)))
        }
    }

    private fun updateLabel(text: String): Callback {
        return Callback.wrap {
            status.text = text
            show()
        }
    }

    private fun stopPlaybackListeners() {
        playbackListenerIds.forEach(::stopListening)
        playbackListenerIds.clear()
    }
}