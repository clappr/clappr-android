package io.clappr.player.plugin

import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import io.clappr.player.base.Callback
import io.clappr.player.base.Event
import io.clappr.player.base.InternalEvent
import io.clappr.player.base.NamedType
import io.clappr.player.components.Container
import io.clappr.player.plugin.container.UIContainerPlugin


open class LoadingPlugin(container: Container) : UIContainerPlugin(container) {

    private var spinnerLayout: LinearLayout? = null

    companion object : NamedType {
        override val name = "spinner"
    }

    override var state: State = State.ENABLED
        set(value) {
            if (value == State.ENABLED)
                bindEventListeners()
            else
                stopListening()
            field = value
        }

    fun bindEventListeners() {
        listenTo(container, InternalEvent.DID_CHANGE_PLAYBACK.value, Callback.wrap { bindLoadingVisibilityCallBacks() })
    }

    private fun bindLoadingVisibilityCallBacks() {
        if (container.playback != null) {
            listenTo(container.playback!!, Event.STALLED.value, startAnimating())
            listenTo(container.playback!!, Event.WILL_PLAY.value, startAnimating())
            listenTo(container.playback!!, Event.PLAYING.value, stopAnimating())
            listenTo(container.playback!!, Event.DID_STOP.value, stopAnimating())
            listenTo(container.playback!!, Event.DID_PAUSE.value, stopAnimating())
            listenTo(container.playback!!, Event.DID_COMPLETE.value, stopAnimating())
            listenTo(container.playback!!, Event.ERROR.value, stopAnimating())
        }
    }

    private fun startAnimating(): Callback {
        return Callback.wrap {
            if (spinnerLayout == null)
                setupSpinner()

            spinnerLayout?.visibility = View.VISIBLE
            visibility = Visibility.VISIBLE
        }
    }

    private fun setupSpinner() {
        spinnerLayout = createSpinnerLayout()
        spinnerLayout?.addView(ProgressBar(context))

        container.frameLayout.addView(spinnerLayout)
    }

    private fun createSpinnerLayout(): LinearLayout {
        val linearLayout = LinearLayout(context)
        linearLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        linearLayout.setGravity(Gravity.CENTER)
        linearLayout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.black))
        linearLayout.alpha = 0.7f
        return linearLayout
    }

    init {
        bindEventListeners()
    }

    private fun stopAnimating(): Callback {
        return Callback.wrap {
            spinnerLayout?.visibility = View.INVISIBLE
            visibility = Visibility.HIDDEN
        }
    }
}