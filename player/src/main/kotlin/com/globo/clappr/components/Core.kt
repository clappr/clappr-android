package com.globo.clappr.components

import com.globo.clappr.plugin.Loader
import com.globo.clappr.base.Options
import com.globo.clappr.base.UIObject

open class Core(val loader: Loader, options: Options) : UIObject() {
    var activeContainer: Container? = null
    var containers: MutableMap<String, Container> = hashMapOf()
}