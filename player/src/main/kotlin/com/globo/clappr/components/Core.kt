package com.globo.clappr.components

import android.os.Bundle
import android.widget.FrameLayout
import com.globo.clappr.base.Callback
import com.globo.clappr.base.InternalEvent
import com.globo.clappr.plugin.Loader
import com.globo.clappr.base.Options
import com.globo.clappr.base.UIObject
import com.globo.clappr.plugin.Plugin

class Core(val loader: Loader, val options: Options) : UIObject() {
    val plugins: List<Plugin>

    val containers: MutableList<Container> = mutableListOf()
    var activeContainer: Container? = null
        set(value) {
            activeContainer?.stopListening()
            field = value
            activeContainer?.on(InternalEvent.PLAYBACK_CHANGED.value,
                    Callback.wrap { bundle: Bundle? -> trigger(InternalEvent.ACTIVE_PLAYBACK_CHANGED.value, bundle) }
            )
            trigger(InternalEvent.ACTIVE_CONTAINER_CHANGED.value)
        }

    val activePlayback: Playback?
        get() = activeContainer?.playback

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