package com.globo.clappr.base

enum class Event(val value: String) {
    READY("ready"),
    ERROR("error"),
    WILL_PLAY("willPlay"),
    PLAYING("playing"),
    WILL_PAUSE("willPause"),
    DID_PAUSE("didPause"),
    WILL_SEEK("willSeek"),
    WILL_STOP("willStop"),
    DID_STOP("didStop"),
    DID_COMPLETE("didComplete"),
    STALLED("stalled"),
    BUFFER_UPDATE("bufferUpdate"),
    POSITION_UPDATE("positionUpdate")
}