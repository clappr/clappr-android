package io.clappr.player.plugin.control

import android.view.LayoutInflater
import android.widget.ImageButton
import io.clappr.player.components.Core

abstract class ButtonPlugin(core: Core, name: String) : MediaControl.Plugin(core, name) {

    abstract val resourceDrawable: Int
    abstract val idResourceDrawable: Int

    abstract val resourceLayout: Int

    override val view by lazy {
        LayoutInflater.from(applicationContext).inflate(resourceLayout, null) as ImageButton
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