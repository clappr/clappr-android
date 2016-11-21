package com.globo.clappr.base

enum class InternalEvent (val value: String) {
    WILL_CHANGE_ACTIVE_CONTAINER("willChangeActiveContainer"),
    DID_CHANGE_ACTIVE_CONTAINER("didChangeActiveContainer"),
    WILL_CHANGE_ACTIVE_PLAYBACK("willChangeActivePlayback"),
    DID_CHANGE_ACTIVE_PLAYBACK("didChangeActivePlayback"),
    WILL_CHANGE_PLAYBACK("willChangePlayback"),
    DID_CHANGE_PLAYBACK("didChangePlayback")
}