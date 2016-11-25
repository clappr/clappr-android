package com.globo.clappr.plugin

import android.content.Context
import android.widget.ProgressBar
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
    }
}