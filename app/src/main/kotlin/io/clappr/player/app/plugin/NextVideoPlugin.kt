package io.clappr.player.app.plugin

import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.squareup.picasso.Picasso
import io.clappr.player.app.R
import io.clappr.player.base.Callback
import io.clappr.player.base.Event
import io.clappr.player.base.InternalEvent
import io.clappr.player.base.NamedType
import io.clappr.player.components.Core
import io.clappr.player.plugin.core.UICorePlugin


open class NextVideoPlugin(core: Core) : UICorePlugin(core) {

    companion object : NamedType {
        override val name = "nextVideo"
    }

    override var state: State = State.ENABLED

    override val view by lazy {
        LayoutInflater.from(context).inflate(R.layout.next_video_plugin, null) as RelativeLayout
    }

    open val videoListView by lazy { view.findViewById(R.id.video_list) as LinearLayout }

    private val videoList = listOf(
            Pair("http://clappr.io/poster.png", "http://clappr.io/highline.mp4"),
            Pair("http://clappr.io/poster.png", "http://clappr.io/highline.mp4"),
            Pair("http://clappr.io/poster.png", "http://clappr.io/highline.mp4")
    )

    internal val playbackListenerIds = mutableListOf<String>()

    init {
        bindCoreEvents()
    }

    override fun destroy() {
        stopPlaybackListeners()
        super.destroy()
    }

    override fun render() {
        super.render()
        setupLayout()
    }

    private fun bindCoreEvents() {
        listenTo(core, InternalEvent.DID_CHANGE_ACTIVE_PLAYBACK.value, Callback.wrap {
            hide()
            bindPlaybackEvents()
        })
    }

    private fun bindPlaybackEvents() {
        stopPlaybackListeners()

        core.activePlayback?.let {
            playbackListenerIds.add(listenTo(it, Event.WILL_PLAY.value, hideNextVideo()))
            playbackListenerIds.add(listenTo(it, Event.DID_STOP.value, showNextVideo()))
            playbackListenerIds.add(listenTo(it, Event.DID_COMPLETE.value, showNextVideo()))
        }
    }

    private fun setupLayout() {
        videoList.forEach { entry ->
            videoListView.addView(getNextVideoView(entry))
        }
    }

    private fun getNextVideoView(entry: Pair<String, String>) =
            (LayoutInflater.from(context).inflate(R.layout.next_video_item, null) as RelativeLayout).apply {
                val videoPoster = findViewById<ImageView>(R.id.video_poster)
                Picasso.get().load(entry.first).fit().centerCrop().into(videoPoster)

                setOnClickListener { onClick(entry.second) }
            }

    private fun hideNextVideo() = Callback.wrap {
        hide()
    }

    private fun showNextVideo() = Callback.wrap {
        show()
    }

    private fun stopPlaybackListeners() {
        playbackListenerIds.forEach(::stopListening)
        playbackListenerIds.clear()
    }

    private fun onClick(url: String) {
        core.activePlayback?.stop()
        core.options.source = url
        core.load()
    }
}