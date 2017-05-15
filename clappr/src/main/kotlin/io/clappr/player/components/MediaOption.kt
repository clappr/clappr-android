package io.clappr.player.components

data class MediaOption(val name: String, val type: MediaOptionType, val raw: Any?, val info: Map<String, Any>?)

enum class MediaOptionType {
    SUBTITLE,
    AUDIO
}