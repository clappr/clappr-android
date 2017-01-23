package io.clappr.player.base

enum class InternalEvent (val value: String) {
    WILL_CHANGE_ACTIVE_CONTAINER("willChangeActiveContainer"),
    DID_CHANGE_ACTIVE_CONTAINER("didChangeActiveContainer"),
    WILL_CHANGE_ACTIVE_PLAYBACK("willChangeActivePlayback"),
    DID_CHANGE_ACTIVE_PLAYBACK("didChangeActivePlayback"),
    WILL_CHANGE_PLAYBACK("willChangePlayback"),
    DID_CHANGE_PLAYBACK("didChangePlayback"),
    WILL_ENTER_FULLSCREEN("willEnterFullscreen"),
    DID_ENTER_FULLSCREEN("didEnterFullscreen"),
    WILL_EXIT_FULLSCREEN("willExitFullscreen"),
    DID_EXIT_FULLSCREEN("didExitFullscreen"),
    WILL_CHANGE_SOURCE("willChangeSource"),
    DID_CHANGE_SOURCE("didChangeSource")
}