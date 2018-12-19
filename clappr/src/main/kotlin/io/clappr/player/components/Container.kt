package io.clappr.player.components

import android.view.View
import android.view.ViewManager
import android.widget.FrameLayout
import io.clappr.player.base.InternalEvent
import io.clappr.player.base.Options
import io.clappr.player.base.UIObject
import io.clappr.player.log.Logger
import io.clappr.player.playback.NoOpPlayback
import io.clappr.player.plugin.Loader
import io.clappr.player.plugin.Plugin
import io.clappr.player.plugin.container.UIContainerPlugin

class Container(val loader: Loader, options: Options) : UIObject() {

    private val TAG = "Container"

    val plugins: List<Plugin>
        get() = internalPlugins

    private val internalPlugins: MutableList<Plugin>

    var playback: Playback? = null
        set(value) {
            if (playback != value) {
                trigger(InternalEvent.WILL_CHANGE_PLAYBACK.value)

                field = value

                trigger(InternalEvent.DID_CHANGE_PLAYBACK.value)
            }
        }

    private val frameLayout: FrameLayout
        get() = view as FrameLayout

    var options : Options = options
        set(options)  {
            field = options
            trigger(InternalEvent.DID_UPDATE_OPTIONS.value)
        }

    override val viewClass: Class<*>
        get() = FrameLayout::class.java

    init {
        internalPlugins = loader.loadPlugins(this).toMutableList()
    }

    fun destroy() {
        trigger(InternalEvent.WILL_DESTROY.value)
        playback?.destroy()
        playback = null
        internalPlugins.forEach { handlePluginAction({ it.destroy() },
                "Plugin ${it.javaClass.simpleName} crashed during destroy") }
        internalPlugins.clear()
        stopListening()
        trigger(InternalEvent.DID_DESTROY.value)
    }

    fun load(source: String, mimeType: String? = null): Boolean {
        trigger(InternalEvent.WILL_LOAD_SOURCE.value)

        playback = loader.loadPlayback(source, mimeType, options)
        if (playback?.name == NoOpPlayback.entry.name) {
            playback = null
        }
        val supported = playback != null
        render()

        val eventToTrigger = if (supported) InternalEvent.DID_LOAD_SOURCE else InternalEvent.DID_NOT_LOAD_SOURCE
        trigger(eventToTrigger.value)

        return supported
    }

    override fun render(): Container {
        frameLayout.removeAllViews()
        playback?.let {
            removeViewFromParent(it.view, it.name)
            frameLayout.addView(it.view)
            it.render()
        }
        internalPlugins.filterIsInstance(UIContainerPlugin::class.java).forEach {
            removeViewFromParent(it.view, it.name)
            frameLayout.addView(it.view)
            handlePluginAction({ it.render() },
                    "Plugin ${it.javaClass.simpleName} crashed during render")
        }
        return this
    }

    private fun removeViewFromParent(view: View?, name: String?) {
        (view?.parent as? ViewManager)?.let {
            Logger.error(TAG, "View on parent: ${name}")
            it.removeView(view)
        }
    }

    private fun handlePluginAction(action: () -> Unit, errorMessage: String) {
        try {
            action.invoke()
        } catch (error: Exception) {
            Logger.error(Container::class.java.simpleName, errorMessage, error)
        }
    }
}
