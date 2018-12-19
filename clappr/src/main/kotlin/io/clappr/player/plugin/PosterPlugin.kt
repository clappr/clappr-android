package io.clappr.player.plugin

import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.jakewharton.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import io.clappr.player.base.*
import io.clappr.player.components.Container
import io.clappr.player.components.Playback
import io.clappr.player.log.Logger
import io.clappr.player.plugin.Plugin.State
import io.clappr.player.plugin.container.UIContainerPlugin
import okhttp3.OkHttpClient

class PosterPlugin(container: Container): UIContainerPlugin(container, name = name) {

    private val posterLayout = LinearLayout(applicationContext)

    private val imageView = ImageView(applicationContext)

    private var posterImageUrl: String? = null

    companion object : NamedType {
        override val name = "poster"

        private val httpClient: OkHttpClient by lazy { OkHttpClient.Builder().build() }
        private val picasso: Picasso by lazy {
            Picasso.Builder(BaseObject.applicationContext).downloader(OkHttp3Downloader(httpClient))
                .listener{ _, uri, _ -> Logger.error(message = "Failed to load image: $uri") }
                .build()
        }
    }

    override var state: State = State.ENABLED
        set(value) {
            if (value == State.ENABLED)
                bindEventListeners()
            else
                stopListening()
            field = value
        }

    override val view: View?
        get() = posterLayout

    private val playbackListenerIds = mutableListOf<String>()

    init {
        updateImageUrlFromOptions()
        setupPosterLayout()
        bindEventListeners()
    }

    private fun bindEventListeners() {
        updatePoster()
        listenTo(container, InternalEvent.DID_CHANGE_PLAYBACK.value, Callback.wrap { bindPlaybackListeners() })
        listenTo(container, Event.REQUEST_POSTER_UPDATE.value, Callback.wrap { it -> updatePoster(it) })
        listenTo(container, InternalEvent.DID_UPDATE_OPTIONS.value, Callback.wrap { updateImageUrlFromOptions() })
    }

    private fun bindPlaybackListeners() {
        stopPlaybackListeners()
        updatePoster()

        container.playback?.let {
            playbackListenerIds.addAll(listOf(
                listenTo(it, Event.PLAYING.value, Callback.wrap { _ -> hide() }),
                listenTo(it, Event.DID_STOP.value, Callback.wrap { _ -> show() }),
                listenTo(it, Event.DID_COMPLETE.value, Callback.wrap { _ -> show() })
            ))
        }
    }

    private fun updateImageUrlFromOptions(){
        posterImageUrl = container.options[ClapprOption.POSTER.value] as? String
    }

    private fun setupPosterLayout() {
        posterLayout.let {
            it.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            it.gravity = Gravity.CENTER

            it.setBackgroundColor(ContextCompat.getColor(applicationContext, android.R.color.black))

            it.addView(imageView)

            imageView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        }
    }

    private fun updatePoster(bundle: Bundle? = null) {
        if (bundle != null) {
            val url = bundle.getString("url")
            if (url != null) {
                posterImageUrl = url
            }
        }

        posterLayout.bringToFront()

        when (container.playback?.state) {
            Playback.State.IDLE -> show()
            Playback.State.PLAYING -> hide()
            else -> {}
        }

        posterImageUrl?.let {
            container.trigger(Event.WILL_UPDATE_POSTER.value)
            val uri = Uri.parse(it)
            picasso.load(uri).fit().centerCrop().into(imageView)
            container.trigger(Event.DID_UPDATE_POSTER.value)
        }
    }

    override fun destroy() {
        stopPlaybackListeners()
        super.destroy()
    }

    private fun stopPlaybackListeners() {
        playbackListenerIds.forEach(::stopListening)
        playbackListenerIds.clear()
    }
}