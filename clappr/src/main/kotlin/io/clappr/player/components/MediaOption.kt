package io.clappr.player.components

class MediaOption(val name: String, val type: MediaOptionType, val raw: Any?, val info: Map<String, Any>?)

val SUBTITLE_OFF = MediaOption("", MediaOptionType.SUBTITLE, null, null)

enum class MediaOptionType {
    SUBTITLE,
    AUDIO;

    enum class Language(val value: String) {
        PT_BR("Por")
    }

    enum class Audio(val value: String) {
        PT_BR("Por"),
        ORIGINAL("Original")
    }
}