package com.globo.clappr.plugin

import android.content.Context
<<<<<<< HEAD
import android.view.View
import android.widget.ProgressBar
import com.globo.clappr.base.Callback
import com.globo.clappr.base.Event
=======
>>>>>>> feature(loading_plugin): create LoadingPlugin class
import com.globo.clappr.base.NamedType
import com.globo.clappr.components.Container
import com.globo.clappr.plugin.container.UIContainerPlugin

open class LoadingPlugin(container: Container, context: Context) : UIContainerPlugin(container) {
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
}