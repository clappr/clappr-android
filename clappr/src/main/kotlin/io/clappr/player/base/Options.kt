package io.clappr.player.base

import java.util.*

class Options(
    var source: String? = null,
    var mimeType: String? = null,
    var autoPlay: Boolean = true,
    val options: HashMap<String, Any> = hashMapOf<String, Any>()): MutableMap<String, Any> by options

enum class ClapprOption(val value: String) {
    /**
     * Media start position
     */
    START_AT("startAt"),
    /**
     * Poster URL
     */
    POSTER("poster")
}