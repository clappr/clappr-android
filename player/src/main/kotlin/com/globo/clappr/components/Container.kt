package com.globo.clappr.components

import com.globo.clappr.plugin.Loader
import com.globo.clappr.base.Options
import com.globo.clappr.base.UIObject

open class Container(options: Options) : UIObject() {
    var playback: Playback? = null
}
