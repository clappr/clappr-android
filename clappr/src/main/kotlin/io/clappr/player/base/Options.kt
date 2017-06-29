package io.clappr.player.base

import java.util.*

class Options(
    var source: String? = null,
    var mimeType: String? = null,
    @Deprecated(message = "Autoplay functionality will be removed from Player on June/15/2017",
            level = DeprecationLevel.WARNING)
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
    POSTER("poster"),
    /**
     * Inform the URL license if DRM is necessary
     */
    DRM_LICENSE_URL("drmLicenseUrl")
}