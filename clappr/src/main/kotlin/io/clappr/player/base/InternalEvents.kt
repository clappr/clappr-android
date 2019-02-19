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
    WILL_LOAD_SOURCE("willLoadSource"),
    DID_LOAD_SOURCE("didLoadSource"),
    DID_NOT_LOAD_SOURCE("didNotLoadSource"),
    WILL_DESTROY("willDestroy"),
    DID_DESTROY("didDestroy"),
    MEDIA_OPTIONS_READY("mediaOptionsReady"),
    MEDIA_OPTIONS_UPDATE("mediaOptionsUpdate"),
    DID_UPDATE_OPTIONS("didUpdateOptions"),

    DID_TOUCH_MEDIA_CONTROL("didTouchMediaControl"),
    ENABLE_MEDIA_CONTROL("enableMediaControl"),
    DISABLE_MEDIA_CONTROL("disableMediaControl"),
    DID_UPDATE_INTERACTING("didUpdateInteracting"),

    OPEN_MODAL_PANEL("openModalPanel"),
    CLOSE_MODAL_PANEL("closeModalPanel"),
    DID_OPEN_MODAL_PANEL("didOpenModalPanel"),
    DID_CLOSE_MODAL_PANEL("didCloseModalPanel"),

    WILL_SHOW_MEDIA_CONTROL("willShowMediaControl"),
    WILL_HIDE_MEDIA_CONTROL("willHideMediaControl")
}