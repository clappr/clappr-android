package io.clappr.player.base

class Options(
    var source: String? = null,
    var mimeType: String? = null,
    var autoPlay: Boolean = true,
    val options: Map<String, Any> = mutableMapOf<String, Any>()): Map<String, Any> by options