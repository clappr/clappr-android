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
     * Seek completed
     */
    DID_SEEK("didSeek"),
    /**
     * Media source changed
     */
    DID_CHANGE_SOURCE("didChangeSource"),


    /**
     * Media buffer percentage updated
     */
    BUFFER_UPDATE("bufferUpdate"),
    /**
     * Media position updated
     */
    POSITION_UPDATE("positionUpdate"),


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
    /**
     * Will change media source
     */
    WILL_CHANGE_SOURCE("willChangeSource"),

    /**
     * Player is requesting to enter fullscreen
     */
    REQUEST_FULLSCREEN("requestFullscreen"),

    /**
     * Player is requesting to exit fullscreen
     */
    EXIT_FULLSCREEN("exitFullscreen"),

    /**
     * Request to update poster
     */
    REQUEST_POSTER_UPDATE("requestPosterUpdate"),
    /**
     * Will update poster image
     */
    WILL_UPDATE_POSTER("willUpdatePoster"),
    /**
     * Poster image updated
     */
    DID_UPDATE_POSTER("didUpdatePoster")
}
