package io.clappr.player.base

enum class Event(val value: String) {
    /**
     * Player is ready for playback
     */
    READY("ready"),
    /**
     * Player or media error detected
     */
    ERROR("error"),
    /**
     * Did change to PLAYING state
     */
    PLAYING("playing"),
    /**
     * Media playback completed
     */
    DID_COMPLETE("didComplete"),


    /**
     * Did change to PAUSE state
     */
    DID_PAUSE("didPause"),
    /**
     * Changed to STALLED state
     */
    STALLED("stalled"),
    /**
     * Media playback stopped
     */
    DID_STOP("didStop"),


    /**
     * Will change to PLAYING state
     */
    WILL_PLAY("willPlay"),
    /**
     * Will change to PAUSE state
     */
    WILL_PAUSE("willPause"),
    /**
     * Will change media position
     */
    WILL_SEEK("willSeek"),
    /**
     * Will stop media playback
     */
    WILL_STOP("willStop"),

    DID_SEEK("didSeek"),
    WILL_CHANGE_SOURCE("willChangeSource"),
    DID_CHANGE_SOURCE("didChangeSource"),
    BUFFER_UPDATE("bufferUpdate"),
    POSITION_UPDATE("positionUpdate")
}
