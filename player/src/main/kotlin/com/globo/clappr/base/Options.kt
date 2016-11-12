package com.globo.clappr.base

import com.globo.clappr.components.Playback
import com.globo.clappr.plugin.Plugin
import kotlin.reflect.KClass

class Options(
    var source: String? = null,
    var mimeType: String? = null,
    var autoPlay: Boolean = true,
    var plugins: List<out KClass<Plugin>> = emptyList(),
    var playbacks: List<out KClass<Playback>> = emptyList(),
    val options: Map<String, Any> = mutableMapOf<String, Any>()): Map<String, Any> by options {
}