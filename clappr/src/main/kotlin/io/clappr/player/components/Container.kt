package io.clappr.player.components

import android.os.Build
import android.widget.FrameLayout
import io.clappr.player.base.InternalEvent
import io.clappr.player.plugin.Loader
import io.clappr.player.base.Options
import io.clappr.player.base.UIObject
import io.clappr.player.playback.NoOpPlayback
import io.clappr.player.plugin.Plugin
import io.clappr.player.plugin.container.UIContainerPlugin

class Container(val loader: Loader, val options: Options) : UIObject() {
    val plugins: List<Plugin>

    var playback: Playback? = null
        set(value) {
            if (playback != value) {
                trigger(InternalEvent.WILL_CHANGE_PLAYBACK.value)

                field = value

                trigger(InternalEvent.DID_CHANGE_PLAYBACK.value)
            }
        }

    val frameLayout: FrameLayout
        get() = view as FrameLayout

    override val viewClass: Class<*>
        get() = FrameLayout::class.java

    init {
        plugins = loader.loadPlugins(this)
        val source = options.source
        val mimeType = options.mimeType
        if (source != null) {
            load(source, mimeType)
        }
    }

    fun load(source: String, mimeType: String? = null): Boolean {
        trigger(InternalEvent.WILL_CHANGE_SOURCE.value)
        var supported = playback?.load(source, mimeType) ?: false
        if (!supported) {
            playback = loader.loadPlayback(source, mimeType, options)
            if (playback?.name == NoOpPlayback.name) {
                playback = null
            }
            supported = playback != null
            render()
        }
        return supported
    }

    override fun render(): Container {
        frameLayout.removeAllViews()
        val playback = this.playback
        playback?.let {
            frameLayout.addView(playback.view)
            playback.render()
        }
        plugins.filterIsInstance(UIContainerPlugin::class.java).forEach {
            frameLayout.addView(it.view)
            it.render()
        }
        return this
    }
}
