package com.globo.clappr.plugin

<<<<<<< HEAD
<<<<<<< HEAD
import android.content.Context
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> feature(loading_plugin): bind events to their respective callbacks
=======
>>>>>>> refactor(loading_plugin): extract spinner setup to a method
=======
import android.support.v4.content.ContextCompat
import android.view.Gravity
>>>>>>> feat(loading_plugin): add layout to wrap spinner
import android.view.View
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams.MATCH_PARENT
import android.widget.ProgressBar
<<<<<<< HEAD
import com.globo.clappr.base.Callback
import com.globo.clappr.base.Event
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> feature(loading_plugin): create LoadingPlugin class
=======
import android.widget.ProgressBar
>>>>>>> feature(loading_plugin): add ProgressBar var
=======
>>>>>>> feature(loading_plugin): bind events to their respective callbacks
=======
import com.globo.clappr.base.InternalEvent
>>>>>>> refactor(loading_plugin): divide bindEventListeners to separated methods
import com.globo.clappr.base.NamedType
=======
import com.globo.clappr.base.*
>>>>>>> feat(exoplayer): bind visibility events when plugin is enable
import com.globo.clappr.components.Container
import com.globo.clappr.plugin.container.UIContainerPlugin

<<<<<<< HEAD
open class LoadingPlugin(container: Container, context: Context) : UIContainerPlugin(container) {
<<<<<<< HEAD
<<<<<<< HEAD

    private var spinner: ProgressBar?

    companion object: NamedType {
        override val name = "spinner"
    }

    init {
        spinner = ProgressBar(context)
        bindEventListeners()
        container.frameLayout.addView(spinner)
    }

    fun bindEventListeners() {
        listenTo(container.playback!!, Event.READY.value, startAnimating)
        listenTo(container.playback!!, Event.PLAYING.value, stopAnimating)
        listenTo(container.playback!!, Event.DID_COMPLETE.value, stopAnimating)
    }

    private val startAnimating = Callback.wrap {
        spinner?.visibility = View.VISIBLE
    }

    private val stopAnimating = Callback.wrap {
        spinner?.visibility = View.INVISIBLE
    }
=======
    companion object: NamedType {
        override val name = "spinner"
    }
>>>>>>> feature(loading_plugin): create LoadingPlugin class
=======
=======
open class LoadingPlugin(container: Container) : UIContainerPlugin(container) {
>>>>>>> refactor(loading_plugin): remove context param from constructor

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
        container.on(InternalEvent.DID_CHANGE_PLAYBACK.value, bindLoadingVisibilityCallBacks())
    }

    private fun bindLoadingVisibilityCallBacks(): Callback {
        return Callback.wrap {
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
        linearLayout.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
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
>>>>>>> feature(loading_plugin): add ProgressBar var
}