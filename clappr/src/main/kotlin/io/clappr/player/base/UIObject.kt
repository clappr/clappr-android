package io.clappr.player.base

import android.content.Context
import android.view.View
import android.view.ViewManager

open class UIObject: BaseObject() {
    var view : View? = null

    open val viewClass: Class<*> = View::class.java

    init {
        ensureView()
    }

    open fun render() : UIObject {
        return this
    }

    fun remove() : UIObject {
        (view?.parent as? ViewManager)?.removeView(view)
        return this
    }

    private fun ensureView() {
        if (view == null) {
            val constructor = viewClass.getConstructor(Context::class.java) ?: throw IllegalStateException("No constructor was found for parameters (Context)")
            view = constructor.newInstance(applicationContext) as? View
        }
    }
}
