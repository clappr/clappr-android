package io.clappr.player.app.plugin

import android.view.View
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
        override val name = "mycore"
    }

    override var state: State = State.ENABLED

    override val view: View = LinearLayout(context)

    private val videoList = mapOf(
            "http://clappr.io/poster.png" to "http://clappr.io/highline.mp4",
            "http://clappr.io/poster.png" to "http://clappr.io/highline.mp4"
    )

    private val playbackListenerIds = mutableListOf<String>()

    init {
        setupLayout()
        bindCoreEvents()
    }

    private fun setupLayout() {
        (view as LinearLayout).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            orientation = LinearLayout.HORIZONTAL

            videoList.entries.forEach { entry ->
                val positionedRelativeLayoutPoster = getPositionedPosterRelativeLayout().apply {
                    addView(getPoster(entry.key))
                    addView(getPosterPlayIcon())
                }

                val relativeLayout = RelativeLayout(context).apply {
                    layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT)
                    setOnClickListener { onClick(entry.value) }
                    addView(positionedRelativeLayoutPoster)
                }

                addView(relativeLayout)
            }
        }
    }

    private fun getPositionedPosterRelativeLayout(): RelativeLayout {
        val paramsPoster = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                .apply {
                    addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                    addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                }

        return RelativeLayout(context).apply { layoutParams = paramsPoster }
    }

    private fun getPoster(url: String): ImageView {
        val poster = ImageView(context)
        Picasso.get().load(url).resize(400, 200).into(poster)
        return poster
    }

    private fun getPosterPlayIcon(): ImageView {
        return ImageView(context).apply {
            setBackgroundResource(R.drawable.ic_play)
            layoutParams = RelativeLayout.LayoutParams(50, 50).apply { addRule(RelativeLayout.CENTER_IN_PARENT) }
        }
    }

    private fun bindCoreEvents() {
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