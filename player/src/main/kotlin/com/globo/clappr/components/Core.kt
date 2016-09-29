package com.globo.clappr.components

import com.globo.clappr.base.UIObject

public open class Core: UIObject {
    constructor(options: Map<String, Any>?) : super() {
    }

    var activeContainer: Container? = null
    var containers: MutableMap<String, Container> = hashMapOf()
}