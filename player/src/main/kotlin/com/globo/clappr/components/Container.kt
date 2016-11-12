package com.globo.clappr.components

import com.globo.clappr.plugin.Loader
import com.globo.clappr.base.Options
import com.globo.clappr.base.UIObject
import com.globo.clappr.playback.NoOpPlayback
import com.globo.clappr.plugin.Plugin

open class Container(val loader: Loader, val options: Options) : UIObject() {
    val plugins: List<Plugin>

    var playback: Playback? = null

    init {
        plugins = loader.loadPlugins(this)
        val source = options.source
        val mimeType = options.mimeType
        if (source != null) {
            load(source, mimeType)
        }
    }

    fun load(source: String, mimeType: String? = null): Boolean {
        var supported = playback?.load(source, mimeType) ?: false
        if (!supported) {
            playback = loader.loadPlayback(source, mimeType, options)
            supported = playback?.name == NoOpPlayback.name ?: false
        }
        return supported
    }
}

// Base - Core, Lifecycle, Playback, etc - 11
// MediaControl - Plugins, Render, etc - 16
