package com.globo.clappr.plugin

import android.content.Context
import android.view.View
import android.widget.ProgressBar
import com.globo.clappr.base.Callback
import com.globo.clappr.base.Event
import com.globo.clappr.base.NamedType
import com.globo.clappr.components.Container
import com.globo.clappr.plugin.container.UIContainerPlugin

open class LoadingPlugin(container: Container, context: Context) : UIContainerPlugin(container) {

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
        listenTo(this, Event.READY.value, startAnimating)
        listenTo(this, Event.PLAYING.value, stopAnimating)
        listenTo(this, Event.DID_COMPLETE.value, stopAnimating)
    }

    private val startAnimating = Callback.wrap {
        spinner!!.visibility = View.VISIBLE
    }

    private val stopAnimating = Callback.wrap {
        spinner!!.visibility = View.INVISIBLE
    }
}