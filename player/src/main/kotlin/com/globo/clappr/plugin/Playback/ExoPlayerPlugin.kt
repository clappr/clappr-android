package com.globo.clappr.plugin.Playback

import android.graphics.Color
import android.util.Log
import android.view.ViewGroup
import com.globo.clappr.base.BaseObject
import com.globo.clappr.components.Playback
import com.globo.clappr.plugin.UIPlugin

open class ExoPlayerPlugin(val playback: Playback) : UIPlugin(playback) {
    companion object {
        const val name = "exoplayerplugin"

        var containerView: ViewGroup? = null
    }

    init {
        this.playback.view?.setBackgroundColor(Color.parseColor("#00ff00"))
        containerView?.addView(this.playback.view)
    }
}