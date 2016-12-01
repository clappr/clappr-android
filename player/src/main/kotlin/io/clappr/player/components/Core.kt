package io.clappr.player.components

import android.os.Bundle
import android.widget.FrameLayout
import io.clappr.player.base.Callback
import io.clappr.player.base.InternalEvent
import io.clappr.player.plugin.Loader
import io.clappr.player.base.Options
import io.clappr.player.base.UIObject
import io.clappr.player.plugin.Plugin
import io.clappr.player.plugin.core.UICorePlugin

class Core(val loader: Loader, val options: Options) : UIObject() {
    val plugins: List<Plugin>

    val containers: MutableList<Container> = mutableListOf()
    var activeContainer: Container? = null
        set(value) {
            if (activeContainer != value) {
                activeContainer?.stopListening()
                trigger(InternalEvent.WILL_CHANGE_ACTIVE_CONTAINER.value)

                field = value

                activeContainer?.on(InternalEvent.WILL_CHANGE_PLAYBACK.value,
                        Callback.wrap { bundle: Bundle? -> trigger(InternalEvent.WILL_CHANGE_ACTIVE_PLAYBACK.value, bundle) } )
                activeContainer?.on(InternalEvent.DID_CHANGE_PLAYBACK.value,
                        Callback.wrap { bundle: Bundle? -> trigger(InternalEvent.DID_CHANGE_ACTIVE_PLAYBACK.value, bundle) } )
                trigger(InternalEvent.DID_CHANGE_ACTIVE_CONTAINER.value)
            }
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
            frameLayout.addView(it.view)
            it.render()
        }
        plugins.filterIsInstance(UICorePlugin::class.java).forEach {
            frameLayout.addView(it.view)
            it.render()
        }
        return this
    }
}