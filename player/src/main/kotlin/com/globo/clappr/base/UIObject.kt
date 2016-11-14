package com.globo.clappr.base

import android.content.Context
import android.view.View
import android.view.ViewManager

open class UIObject(): BaseObject() {
    var view : View? = null

    init {
        ensureView()
    }

    fun viewClass() : Class<*> = View::class.java

    open fun render() : UIObject {
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
