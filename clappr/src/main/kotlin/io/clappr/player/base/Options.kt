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
     * Byte Array of drm licenses
     */
    DRM_LICENSES("drmLicenses"),
    /**
     * The minimum size in seconds to a video be considered with DVR
     */
    MIN_DVR_SIZE("minDvrSize"),
    /**
     * The sequence in which the plugins will be displayed in the media control. Names are separated by commas and are case sensitive.
     */
    MEDIA_CONTROL_PLUGINS("mediaControlPlugins"),
    /**
     *  If true the video will be played forever (loop mode).
     *  If false the video will be stopped when it ends
     */
    LOOP("loop"),
    /**
     * Boolean value indicating if Audio Focus should be handled by Clappr. Default value is false.
     */
    HANDLE_AUDIO_FOCUS("handleAudioFocus"),

    /**
     * String that represents default audio
     */
    DEFAULT_AUDIO("defaultAudio"),

    /**
     * String that represents default subtitle
     */
    DEFAULT_SUBTITLE("defaultSubtitle")

}