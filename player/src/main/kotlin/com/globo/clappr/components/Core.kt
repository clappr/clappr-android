package com.globo.clappr.components

import com.globo.clappr.base.Options
import com.globo.clappr.base.UIObject

open class Core: UIObject {
    constructor(options: Options?) : super() {
    }

    var activeContainer: Container? = null
    var containers: MutableMap<String, Container> = hashMapOf()
}