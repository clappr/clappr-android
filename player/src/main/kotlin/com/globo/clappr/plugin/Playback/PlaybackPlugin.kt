package com.globo.clappr.plugin.Playback

import com.globo.clappr.base.BaseObject
import com.globo.clappr.components.Playback
import com.globo.clappr.plugin.UIPlugin

open class PlaybackPlugin() : UIPlugin() {
    override val name = "playbackplugin"

    var playback : Playback? = null
    override fun setup(component: BaseObject) {
        playback = component as? Playback
    }
}