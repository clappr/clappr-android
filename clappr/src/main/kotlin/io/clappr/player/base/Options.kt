package io.clappr.player.base

import java.util.*

class Options(
        var source: String? = null,
        var mimeType: String? = null,
        val options: HashMap<String, Any> = hashMapOf<String, Any>()) : MutableMap<String, Any> by options

enum class ClapprOption(val value: String) {
    /**
     * This value can be a number, but will be converted to Integer and may cause a truncated value
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
    SELECTED_MEDIA_OPTIONS("selectedMediaOptions"),
    /**
     * Byte Array of drm licenses
     */
    DRM_LICENSES("drmLicenses"),
    /**
     * The minimum size in seconds to a video be considered with DVR
     */
    MIN_DVR_SIZE("minDvrSize")
}