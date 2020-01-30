package io.clappr.player.extensions

import io.clappr.player.base.ClapprOption
import io.clappr.player.base.Options

val Options.isChromeless get() = this[ClapprOption.CHROMELESS.value] == true
