package com.globo.clappr.components

import com.globo.clappr.plugin.Loader
import com.globo.clappr.base.Options
import com.globo.clappr.base.UIObject
import com.globo.clappr.plugin.Plugin

open class Playback(val loader: Loader, val options: Options) : UIObject() {
    val plugins: List<Plugin>

    init {
        plugins = loader.setupPlaybackPlugins(this)
    }
}