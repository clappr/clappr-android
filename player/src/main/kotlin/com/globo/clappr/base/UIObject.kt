package com.globo.clappr.base

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager

open class UIObject(private val base: BaseObject = BaseObject()): EventInterface by base {
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
            view = viewClass().getConstructor(Context::class.java).newInstance(base.context) as View?
        }
    }
}
