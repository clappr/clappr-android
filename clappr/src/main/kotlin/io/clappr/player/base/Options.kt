package io.clappr.player.base

class Options(
    var source: String? = null,
    var mimeType: String? = null,
    var autoPlay: Boolean = true,
    val options: MutableMap<String, Any> = mutableMapOf<String, Any>()): MutableMap<String, Any> by options

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