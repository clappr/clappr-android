package com.globo.clappr.plugin

import android.content.Context
import com.globo.clappr.base.NamedType
import com.globo.clappr.components.Container
import com.globo.clappr.plugin.container.UIContainerPlugin

open class LoadingPlugin(container: Container, context: Context) : UIContainerPlugin(container) {
    companion object: NamedType {
        override val name = "spinner"
    }
}