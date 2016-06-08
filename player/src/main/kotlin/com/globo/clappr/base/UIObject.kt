package com.globo.clappr.base

import android.content.Context
import android.view.View
import android.view.ViewManager

public open class UIObject: BaseObject {
    var view : View? = null

    constructor() : super() {
        ensureView()
    }

    fun viewClass() : Class<*> = View::class.java

    fun render() : UIObject {
        return this
    }

    fun remove() : UIObject {
        (view?.parent as? ViewManager)?.removeView(view)
        return this
    }

    fun ensureView() {
        render()
        if (view == null) {
            view = viewClass().getConstructor(Context::class.java).newInstance(context) as View?
        }
    }
}
