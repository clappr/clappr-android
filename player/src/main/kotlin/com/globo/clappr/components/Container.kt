package com.globo.clappr.components

import com.globo.clappr.plugin.Loader
import com.globo.clappr.base.Options
import com.globo.clappr.base.UIObject
import com.globo.clappr.plugin.Plugin

open class Container(val loader: Loader, val options: Options) : UIObject() {
    val plugins: List<Plugin>

    var playback: Playback? = null

    init {
        plugins = loader.setupContainerPlugins(this)

        playback = Playback(loader, options)
    }

}
