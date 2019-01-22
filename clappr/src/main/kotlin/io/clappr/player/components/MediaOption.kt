package io.clappr.player.components

data class MediaOption(val name: String, val type: MediaOptionType, val raw: Any?, val info: Map<String, Any>?)

val SUBTITLE_OFF = MediaOption(SubtitleLanguage.OFF.value, MediaOptionType.SUBTITLE, null, null)

enum class AudioLanguage(val value: String) {
    ORIGINAL("und"),
    PORTUGUESE("por"),
    ENGLISH("eng")
}

enum class SubtitleLanguage(val value: String) {
    OFF("off"),
    PORTUGUESE("por")
}

enum class MediaOptionType {
    SUBTITLE,
    AUDIO;
}