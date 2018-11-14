package io.clappr.player.app.plugin

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.ImageView
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

    override val view: View = RelativeLayout(context)

    private val videoList = listOf(
            Pair("http://clappr.io/poster.png", "http://clappr.io/highline.mp4"),
            Pair("http://clappr.io/poster.png", "http://clappr.io/highline.mp4")
    )

    private val playbackListenerIds = mutableListOf<String>()

    init {
        setupLayout()
        bindCoreEvents()
    }

    private fun bindCoreEvents() {
        listenTo(core, InternalEvent.DID_CHANGE_ACTIVE_PLAYBACK.value, Callback.wrap {
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
        (view as RelativeLayout).apply {
            layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT).apply {
                gravity = Gravity.BOTTOM
            }

            var rightOf: Int? = null
            videoList.forEachIndexed { index, entry ->
                val positionedRelativeLayoutPoster = getPositionedPosterRelativeLayout(entry, rightOf)
                positionedRelativeLayoutPoster.id = index+1

                addView(positionedRelativeLayoutPoster)
                rightOf = positionedRelativeLayoutPoster.id
            }
        }
    }

    private fun getPositionedPosterRelativeLayout(entry: Pair<String, String>, rightOf: Int?): RelativeLayout {
        val paramsPoster = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                .apply {
                    setMargins(10,10, 10, 10)
                    rightOf?.let { addRule(RelativeLayout.RIGHT_OF, it) }
                }

        return RelativeLayout(context).apply {
            layoutParams = paramsPoster
            setPadding(1, 1, 1, 1)
            setBackgroundColor(Color.RED)

            addView(getPoster(entry.first))
            addView(getPosterPlayIcon())

            setOnClickListener { onClick(entry.second) }
        }
    }

    private fun getPoster(url: String): ImageView {
        val poster = ImageView(context)
        Picasso.get().load(url).resize(400, 200).into(poster)
        return poster
    }

    private fun getPosterPlayIcon(): ImageView {
        return ImageView(context).apply {
            setBackgroundResource(R.drawable.icon_play)
            layoutParams = RelativeLayout.LayoutParams(50, 50).apply { addRule(RelativeLayout.CENTER_IN_PARENT) }
        }
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
        core.options.source = url
        core.load()
    }

    override fun destroy() {
        stopPlaybackListeners()
        super.destroy()
    }

}