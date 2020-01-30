package io.clappr.player.extensions

import io.clappr.player.plugin.ContainerPluginFactory

fun ContainerPluginFactory.unlessChromeless(): ContainerPluginFactory = {
    it.takeUnless { it.options.isChromeless }?.let(this)
}