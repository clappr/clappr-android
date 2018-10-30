package io.clappr.player.components

import android.os.Bundle
import android.widget.FrameLayout
import io.clappr.player.base.Callback
import io.clappr.player.base.InternalEvent
import io.clappr.player.plugin.Loader
import io.clappr.player.base.Options
import io.clappr.player.base.UIObject
import io.clappr.player.log.Logger
import io.clappr.player.plugin.Plugin
import io.clappr.player.plugin.core.UICorePlugin

class Core(val loader: Loader, options: Options) : UIObject() {

    enum class FullscreenState {
        EMBEDDED, FULLSCREEN
    }

    var fullscreenState: FullscreenState = FullscreenState.EMBEDDED
        set(value) {
            if (value != fullscreenState) {
                val beforeEvent: InternalEvent
                val afterEvent: InternalEvent
                if (value == FullscreenState.FULLSCREEN) {
                    beforeEvent = InternalEvent.WILL_ENTER_FULLSCREEN
                    afterEvent = InternalEvent.DID_ENTER_FULLSCREEN
                } else {
                    beforeEvent = InternalEvent.WILL_EXIT_FULLSCREEN
                    afterEvent = InternalEvent.DID_EXIT_FULLSCREEN
                }
                trigger(beforeEvent.value)
                field = value
                trigger(afterEvent.value)
            }
        }

    val plugins: List<Plugin>
        get() = internalPlugins

    private val internalPlugins: MutableList<Plugin>

    val containers: MutableList<Container> = mutableListOf()
    var activeContainer: Container? = null
        set(value) {
            if (activeContainer != value) {
                activeContainer?.stopListening()
                trigger(InternalEvent.WILL_CHANGE_ACTIVE_CONTAINER.value)

                field = value

                activeContainer?.on(InternalEvent.WILL_CHANGE_PLAYBACK.value,
                        Callback.wrap { bundle: Bundle? -> trigger(InternalEvent.WILL_CHANGE_ACTIVE_PLAYBACK.value, bundle) })
                activeContainer?.on(InternalEvent.DID_CHANGE_PLAYBACK.value,
                        Callback.wrap { bundle: Bundle? -> trigger(InternalEvent.DID_CHANGE_ACTIVE_PLAYBACK.value, bundle) })
                trigger(InternalEvent.DID_CHANGE_ACTIVE_CONTAINER.value)
            }
        }

    val activePlayback: Playback?
        get() = activeContainer?.playback

    val frameLayout: FrameLayout
        get() = view as FrameLayout

    var options : Options = options
        set(options)  {
            field = options
            trigger(InternalEvent.DID_UPDATE_OPTIONS.value)
            updateContainerOptions(options)
        }

    private fun updateContainerOptions(options: Options) {
        containers.forEach { it.options = options}
    }

    override val viewClass: Class<*>
        get() = FrameLayout::class.java

    init {
        internalPlugins = loader.loadPlugins(this).toMutableList()

        val container = Container(loader, options)
        containers.add(container)
    }

    fun load() {
        activeContainer = containers.first()
        options.source?.let {
            activeContainer?.load(it, options.mimeType)
        }
    }

    fun destroy() {
        trigger(InternalEvent.WILL_DESTROY.value)
        containers.forEach { it.destroy() }
        containers.clear()
        internalPlugins.forEach { handlePluginAction({ it.destroy() },
                "Plugin ${it.javaClass.simpleName} crashed during destroy") }
        internalPlugins.clear()
        stopListening()
        trigger(InternalEvent.DID_DESTROY.value)
    }

    override fun render(): Core {
        frameLayout.removeAllViews()
        containers.forEach {
            frameLayout.addView(it.view)
            it.render()
        }
        internalPlugins.filterIsInstance(UICorePlugin::class.java).forEach {
            frameLayout.addView(it.view)
            handlePluginAction({ it.render() },
                    "Plugin ${it.javaClass.simpleName} crashed during render")
        }
        return this
    }

    private fun handlePluginAction(action: () -> Unit, errorMessage: String) {
        try {
            action.invoke()
        } catch (error: Exception) {
            Logger.error(Core::class.simpleName, errorMessage, error)
        }
    }
}