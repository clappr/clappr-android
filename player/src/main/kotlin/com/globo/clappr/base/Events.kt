package com.globo.clappr.base

enum class Event(val value: String) {
    READY("ready"),
    ERROR("error"),
    WILL_PLAY("willPlay"),
    PLAYING("playing"),
    WILL_PAUSE("willPause"),
    DID_PAUSE("didPause"),
    WILL_SEEK("willSeek"),
    DID_SEEK("didSeek"),
    WILL_STOP("willStop"),
    DID_STOP("didStop"),
    WILL_CHANGE_SOURCE("willChangeSource"),
    DID_CHANGE_SOURCE("didChangeSource"),
    DID_COMPLETE("didComplete"),
    STALLED("stalled"),
    BUFFER_UPDATE("bufferUpdate"),
    POSITION_UPDATE("positionUpdate")
}