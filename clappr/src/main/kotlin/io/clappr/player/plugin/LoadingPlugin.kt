package io.clappr.player.plugin

import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import io.clappr.player.base.Event
import io.clappr.player.base.EventHandler
import io.clappr.player.base.InternalEvent
import io.clappr.player.base.NamedType
import io.clappr.player.components.Container
import io.clappr.player.plugin.Plugin.State
import io.clappr.player.plugin.UIPlugin.Visibility
import io.clappr.player.plugin.container.UIContainerPlugin


class LoadingPlugin(container: Container) : UIContainerPlugin(container, name = name) {

    private var spinnerLayout: LinearLayout? = LinearLayout(applicationContext)

    companion object : NamedType {
        override val name = "spinner"

        val entry = PluginEntry.Container(name = name, factory = { container -> LoadingPlugin(container) })
    }

    override var state: State = State.ENABLED

    override val view: View?
        get() = spinnerLayout

    private val playbackListenerIds = mutableListOf<String>()

    init {
        setupSpinnerLayout()
        bindEventListeners()
    }

    private fun bindEventListeners() {
        listenTo(container, InternalEvent.DID_CHANGE_PLAYBACK.value) { bindLoadingVisibilityCallBacks() }
    }

    private fun bindLoadingVisibilityCallBacks() {
        stopPlaybackListeners()

        container.playback?.let {
            playbackListenerIds.add(listenTo(it, Event.STALLING.value, startAnimating()))
            playbackListenerIds.add(listenTo(it, Event.WILL_PLAY.value, startAnimating()))
            playbackListenerIds.add(listenTo(it, Event.PLAYING.value, stopAnimating()))
            playbackListenerIds.add(listenTo(it, Event.DID_STOP.value, stopAnimating()))
            playbackListenerIds.add(listenTo(it, Event.DID_PAUSE.value, stopAnimating()))
            playbackListenerIds.add(listenTo(it, Event.DID_COMPLETE.value, stopAnimating()))
            playbackListenerIds.add(listenTo(it, Event.ERROR.value, stopAnimating()))
        }
    }

    private fun stopPlaybackListeners() {
        playbackListenerIds.forEach(::stopListening)
        playbackListenerIds.clear()
    }

    private fun startAnimating(): EventHandler {
        return {
            spinnerLayout?.visibility = View.VISIBLE
            visibility = Visibility.VISIBLE
        }
    }

    private fun setupSpinnerLayout() {
        spinnerLayout?.let {
            it.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            it.gravity = Gravity.CENTER
            it.setBackgroundColor(ContextCompat.getColor(applicationContext, android.R.color.black))
            it.alpha = 0.7f

            it.addView(ProgressBar(applicationContext))
        }
    }

    private fun stopAnimating(): EventHandler {
        return {
            spinnerLayout?.visibility = View.INVISIBLE
            visibility = Visibility.HIDDEN
        }
    }

    override fun destroy() {
        stopPlaybackListeners()
        super.destroy()
    }
}