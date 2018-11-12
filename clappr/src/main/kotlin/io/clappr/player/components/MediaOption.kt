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

    @Deprecated("This option is deprecated and can be changed in the near future.", ReplaceWith("SubtitleLanguage"), level = DeprecationLevel.WARNING)
    enum class Language(val value: String) {
        PT_BR(SubtitleLanguage.PORTUGUESE.value)
    }

    @Deprecated("This option is deprecated and can be changed in the near future.", ReplaceWith("AudioLanguage"), level = DeprecationLevel.WARNING)
    enum class Audio(val value: String) {
        PT_BR(AudioLanguage.PORTUGUESE.value),
        ORIGINAL(AudioLanguage.ORIGINAL.value),
        EN(AudioLanguage.ENGLISH.value)
    }
}