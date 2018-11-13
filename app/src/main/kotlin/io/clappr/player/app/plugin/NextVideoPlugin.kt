package io.clappr.player.app.plugin

import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import io.clappr.player.base.Callback
import io.clappr.player.base.Event
import io.clappr.player.base.InternalEvent
import io.clappr.player.base.NamedType
import io.clappr.player.components.Core
import io.clappr.player.plugin.core.UICorePlugin


open class NextVideoPlugin(core: Core) : UICorePlugin(core) {

    companion object : NamedType {
        override val name = "mycore"
    }

    override var state: State = State.ENABLED

    override val view: View = LinearLayout(context)

    private val videoList = mapOf(
            "http://clappr.io/poster.png" to "http://clappr.io/highline.mp4",
            "http://clappr.io/poster.png" to "http://clappr.io/highline.mp4"
    )

    internal val playbackListenerIds = mutableListOf<String>()

    init {
        setupLayout()
        bindCoreEvents()
    }

    private fun setupLayout() {
        (view as LinearLayout).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            orientation = LinearLayout.HORIZONTAL

            videoList.entries.forEach { entry ->
                val poster = TextView(context)
                poster.text = entry.key
                poster.setTextColor(Color.RED)
                poster.setBackgroundColor(Color.WHITE)
                poster.setPadding(15,12,15,12)

                poster.setOnClickListener { onClick(entry.value) }

                addView(poster)
            }
        }
    }

    open fun bindCoreEvents() {
        listenTo(core, InternalEvent.DID_CHANGE_ACTIVE_PLAYBACK.value, Callback.wrap {
            bindPlaybackEvents()
        })
    }

    open fun bindPlaybackEvents() {
        stopPlaybackListeners()

        core.activePlayback?.let {
            playbackListenerIds.add(listenTo(it, Event.WILL_PLAY.value, hidePlayList()))
            playbackListenerIds.add(listenTo(it, Event.DID_STOP.value, showPlayList()))
            playbackListenerIds.add(listenTo(it, Event.DID_COMPLETE.value, showPlayList()))
        }
    }

    private fun hidePlayList() = Callback.wrap {
            hide()
        }


    private fun showPlayList() = Callback.wrap {
            show()
        }


    private fun stopPlaybackListeners() {
        playbackListenerIds.forEach(::stopListening)
        playbackListenerIds.clear()
    }

    private fun onClick(url: String) {
        core.options.source = url
        core.load()
    }

    override fun destroy() {
        stopPlaybackListeners()
        super.destroy()
    }

}