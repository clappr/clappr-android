package io.clappr.player.base

import android.support.annotation.Keep

@Keep
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
     * Changed to STALLING state
     */
    STALLING("stalling"),
    /**
     * Media playback stopped
     */
    DID_STOP("didStop"),
    /**
     * Seek completed
     */
    DID_SEEK("didSeek"),
    /**
     * Media source load
     */
    DID_LOAD_SOURCE("didLoadSource"),


    /**
     * Media buffer percentage updated
     */
    DID_UPDATE_BUFFER("bufferUpdate"),
    /**
     * Media position updated
     */
    DID_UPDATE_POSITION("positionUpdate"),


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
     * Will load media source
     */
    WILL_LOAD_SOURCE("willLoadSource"),

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
    DID_UPDATE_POSTER("didUpdatePoster"),

    /**
     * Media Options Selected. Data provided with the [EventData.MEDIA_OPTIONS_SELECTED_RESPONSE] key.
     */
    MEDIA_OPTIONS_SELECTED("mediaOptionsSelected"),

    /**
     * There was a change in DVR status
     */
    DID_CHANGE_DVR_STATUS("didChangeDvrStatus"),

    /**
     * There was a change in DVR availability
     */
    DID_CHANGE_DVR_AVAILABILITY("didChangeDvrAvailability")
}

/**
 * Event bundle data keys for selected Events
 */
@Keep
enum class EventData(val value: String) {
    /**
     * [Event.MEDIA_OPTIONS_SELECTED] data
     *
     * Type: String
     *
     * Selected media options.
     */
    MEDIA_OPTIONS_SELECTED_RESPONSE("mediaOptionsSelectedResponse")
}
