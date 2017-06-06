package io.clappr.player.components

data class MediaOption(val name: String, val type: MediaOptionType, val raw: Any?, val info: Map<String, Any>?)

val SUBTITLE_OFF = MediaOption("", MediaOptionType.SUBTITLE, null, null)

enum class MediaOptionType {
    SUBTITLE,
    AUDIO
}