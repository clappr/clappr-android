package com.globo.clappr.base

enum class Event(val value: String) {
    READY("ready"),
    ERROR("error"),
    IDLE("idle"),
    PLAY("play"),
    STOP("stop"),
    SEEK("seek"),
    PAUSE("pause"),
    WILL_PLAY("willPlay"),
    PLAYING("playing"),
    WILL_PAUSE("willPause"),
    DID_PAUSE("didPause"),
    WILL_SEEK("willSeek"),
    WILL_STOP("willStop"),
    DID_STOP("didStop"),
    DID_COMPLETE("didComplete"),
    ENDED("ended"),
    STALLED("stalled"),
}