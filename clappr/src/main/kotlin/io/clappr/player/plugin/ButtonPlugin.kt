package io.clappr.player.plugin

import android.view.LayoutInflater
import android.widget.ImageButton
import io.clappr.player.components.Core
import io.clappr.player.plugin.Control.MediaControl

abstract class ButtonPlugin(core: Core) : MediaControl.Plugin(core) {

    abstract val resourceDrawable: Int
    abstract val idResourceDrawable: Int

    abstract val resourceLayout: Int

    override val view by lazy {
        LayoutInflater.from(context).inflate(resourceLayout, null) as ImageButton
    }

    init {
        view.setOnClickListener { onClick() }
    }

    override fun destroy() {
        view.setOnClickListener(null)
        super.destroy()
    }

    open fun onClick() {}

    override fun render() {
        super.render()
        view.setImageResource(resourceDrawable)
        view.id = idResourceDrawable
    }
}