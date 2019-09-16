package io.clappr.player.components

enum class AudioLanguage(val value: String) {
    ORIGINAL("und"),
    PORTUGUESE("por"),
    ENGLISH("eng")
}

enum class SubtitleLanguage(val value: String) {
    OFF("off"),
    PORTUGUESE("por")
}

@Deprecated("ClapprOption.DEFAULT_AUDIO and ClapprOption.DEFAULT_SUBTITLE should be used instead.")
fun buildMediaOptionsJson(selectedSubtitle: String, selectedAudio: String?) = when (selectedAudio) {
    null -> """{"media_option":[{"name":"$selectedSubtitle","type":"SUBTITLE"}]}}"""
    else -> """{"media_option":[{"name":"$selectedSubtitle","type":"SUBTITLE"},{"name":"$selectedAudio","type":"AUDIO"}]}}"""
}