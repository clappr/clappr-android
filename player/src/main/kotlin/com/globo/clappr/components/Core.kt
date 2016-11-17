package com.globo.clappr.components

import android.widget.FrameLayout
import com.globo.clappr.plugin.Loader
import com.globo.clappr.base.Options
import com.globo.clappr.base.UIObject
import com.globo.clappr.plugin.Plugin

class Core(val loader: Loader, val options: Options) : UIObject() {
    val plugins: List<Plugin>

    var activeContainer: Container? = null
    val containers: MutableList<Container> = mutableListOf()

    val frameLayout: FrameLayout
        get() = view as FrameLayout

    override val viewClass: Class<*>
        get() = FrameLayout::class.java

    init {
        plugins = loader.loadPlugins(this)

        val container = Container(loader, options)
        containers.add(container)
        activeContainer = containers.first()
    }

    override fun render(): Core {
        containers.forEach {
            it.render()
            frameLayout.addView(it.view)
        }
        return this
    }
}