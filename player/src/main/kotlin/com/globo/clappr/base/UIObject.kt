package com.globo.clappr.base

import android.content.Context
import android.view.View
import android.view.ViewManager
import com.globo.clappr.components.PlayerInfo

public open class UIObject: BaseObject {
    var view : View? = null

    constructor(options: Map<String, Object>?) : super(options) {
        ensureView()
    }

    fun viewClass() : Class<*> = View::class as Class<*>

    fun render() : UIObject {
        return this
    }

    fun remove() : UIObject {
        (view?.getParent() as? ViewManager)?.removeView(view)
        return this
    }

    fun ensureView() {
        render()
        if (view == null) {
            view = viewClass().getConstructor(Context::class as Class<*>).newInstance(PlayerInfo.context) as View?
        }
    }
}
