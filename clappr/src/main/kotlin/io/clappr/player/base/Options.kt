package io.clappr.player.base

import java.util.*

class Options(
        var source: String? = null,
        var mimeType: String? = null,
        val options: HashMap<String, Any> = hashMapOf<String, Any>()) : MutableMap<String, Any> by options

enum class ClapprOption(val value: String) {
    /**
     * Media start position. This value can be a number, but the value may be trunked to an Integer
     */
    START_AT("startAt"),
    /**
     * Poster URL
     */
    POSTER("poster"),
    /**
     * Inform the URL license if DRM is necessary
     */
    DRM_LICENSE_URL("drmLicenseUrl"),
    /**
     * Map from subtitles URL`s with name and URL to each one
     */
    SUBTITLES("subtitles"),
    /**
     * String List to selected MediaOptions
     */
    SELECTED_MEDIA_OPTIONS("selectedMediaOptions")
}