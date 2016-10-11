package com.globo.clappr.plugin.Playback

import com.globo.clappr.base.BaseObject
import com.globo.clappr.components.Playback
import com.globo.clappr.plugin.UIPlugin

open class ExoPlayerPlugin(val playback: Playback) : UIPlugin(playback) {
    companion object {
        const val name = "playbackplugin"
    }
}