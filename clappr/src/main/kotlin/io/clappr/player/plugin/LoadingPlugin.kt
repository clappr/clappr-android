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

    private var spinnerLayout: LinearLayout? = LinearLayout(context)

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

    override val view: View?
        get() = spinnerLayout

    init {
        setupSpinnerLayout()
        bindEventListeners()
    }

    fun bindEventListeners() {
        listenTo(container, InternalEvent.DID_CHANGE_PLAYBACK.value, Callback.wrap { bindLoadingVisibilityCallBacks() })
    }

    private fun bindLoadingVisibilityCallBacks() {
        container.playback?.let {
            listenTo(it, Event.STALLED.value, startAnimating())
            listenTo(it, Event.WILL_PLAY.value, startAnimating())
            listenTo(it, Event.PLAYING.value, stopAnimating())
            listenTo(it, Event.DID_STOP.value, stopAnimating())
            listenTo(it, Event.DID_PAUSE.value, stopAnimating())
            listenTo(it, Event.DID_COMPLETE.value, stopAnimating())
            listenTo(it, Event.ERROR.value, stopAnimating())
        }
    }

    private fun startAnimating(): Callback {
        return Callback.wrap {
            spinnerLayout?.visibility = View.VISIBLE
            visibility = Visibility.VISIBLE
        }
    }

    private fun setupSpinnerLayout() {
        spinnerLayout?.let {
            it.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            it.gravity = Gravity.CENTER
            context?.run { it.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black)) }
            it.alpha = 0.7f

            it.addView(ProgressBar(context))
        }
    }

    private fun stopAnimating(): Callback {
        return Callback.wrap {
            spinnerLayout?.visibility = View.INVISIBLE
            visibility = Visibility.HIDDEN
        }
    }
}