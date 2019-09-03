package io.clappr.player.components

data class MediaOption(val name: String, val type: MediaOptionType)

val SUBTITLE_OFF = MediaOption(SubtitleLanguage.OFF.value, MediaOptionType.SUBTITLE)

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