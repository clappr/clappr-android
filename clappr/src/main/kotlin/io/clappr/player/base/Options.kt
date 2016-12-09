package io.clappr.player.base

import io.clappr.player.components.Playback
import io.clappr.player.plugin.Plugin
import kotlin.reflect.KClass

class Options(
    var source: String? = null,
    var mimeType: String? = null,
    var autoPlay: Boolean = true,
    var plugins: List<KClass<Plugin>> = emptyList(),
    var playbacks: List<KClass<Playback>> = emptyList(),
    val options: Map<String, Any> = mutableMapOf<String, Any>()): Map<String, Any> by options {
}