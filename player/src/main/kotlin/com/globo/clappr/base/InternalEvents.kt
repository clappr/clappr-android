package com.globo.clappr.base

enum class InternalEvent (val value: String) {
    ACTIVE_CONTAINER_CHANGED("activeContainerChanged"),
    ACTIVE_PLAYBACK_CHANGED("activePlaybackChanged"),
    PLAYBACK_CHANGED("playbackChanged")
}