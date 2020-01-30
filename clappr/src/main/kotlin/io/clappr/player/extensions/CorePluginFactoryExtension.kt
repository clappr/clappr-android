package io.clappr.player.extensions

import io.clappr.player.plugin.CorePluginFactory

fun CorePluginFactory.unlessChromeless(): CorePluginFactory = {
    it.takeUnless { it.options.isChromeless }?.let(this)
}