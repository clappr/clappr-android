package io.clappr.player.base

import androidx.annotation.Keep
import io.clappr.player.base.keys.Action
import io.clappr.player.base.keys.Key

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
     * Media source loaded
     */
    DID_LOAD_SOURCE("didLoadSource"),


    /**
     * Media buffer percentage updated
     */
    DID_UPDATE_BUFFER("didUpdateBuffer"),
    /**
     * Media position updated
     */
    DID_UPDATE_POSITION("didUpdatePosition"),


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
     * Media Options Selected. Triggered when the user select a Media Option.
     * Data provided with the [EventData.MEDIA_OPTIONS_SELECTED_RESPONSE] key.
     */
    @Deprecated("Event.DID_SELECT_AUDIO and Event.DID_SELECT_SUBTITLE should be used instead.")
    MEDIA_OPTIONS_SELECTED("mediaOptionsSelected"),

    /**
     * Media Options Update. Triggered when the Playback load a media option
     */
    @Deprecated("Event.DID_UPDATE_AUDIO and Event.DID_UPDATE_SUBTITLE should be used instead.")
    MEDIA_OPTIONS_UPDATE("mediaOptionsUpdate"),

    /**
     * Triggered when an audio is selected.
     * Data provided with the [EventData.SELECTED_AUDIO] key.
     */
    DID_SELECT_AUDIO("didSelectAudio"),

    /**
     * Triggered when a subtitle is selected.
     * Data provided with the [EventData.SELECTED_SUBTITLE] key.
     */
    DID_SELECT_SUBTITLE("didSelectSubtitle"),

    /**
     * Triggered when an audio is updated
     */
    DID_UPDATE_AUDIO("didUpdateAudio"),

    /**
     * Triggered when a subtitle is updated
     */
    DID_UPDATE_SUBTITLE("didUpdateSubtitle"),

    /**
     * There was a change in DVR status
     */
    DID_CHANGE_DVR_STATUS("didChangeDvrStatus"),

    /**
     * There was a change in DVR availability
     */
    DID_CHANGE_DVR_AVAILABILITY("didChangeDvrAvailability"),

    /**
     * Bitrate was updated
     */
    DID_UPDATE_BITRATE("didUpdateBitrate"),

    /**
     * There was a video loop
     */
    DID_LOOP("didLoop"),

    /**
     * A input key was received from an external device
     * **/
    DID_RECEIVE_INPUT_KEY("didReceiveInputKey"),

    /**
     * There was a change in screen orientation
     */
    DID_CHANGE_SCREEN_ORIENTATION("didChangeScreenOrientation")
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
    @Deprecated("EventData.SELECTED_AUDIO and EventData.SELECTED_SUBTITLE should be used instead.")
    MEDIA_OPTIONS_SELECTED_RESPONSE("mediaOptionsSelectedResponse"),

    /**
     * [Event.DID_SELECT_AUDIO] data
     *
     * Type: String
     *
     * Selected audio language
     */
    SELECTED_AUDIO("selectedAudio"),

    /**
     * [Event.DID_SELECT_SUBTITLE] data
     *
     * Type: String
     *
     * Selected subtitle language
     */
    SELECTED_SUBTITLE("selectedSubtitle"),

    /**
     * [Event.DID_UPDATE_AUDIO] data
     *
     * Type: String
     *
     * Updated audio language
     */
    UPDATED_AUDIO("updatedAudio"),

    /**
     * [Event.DID_UPDATE_SUBTITLE] data
     *
     * Type: String
     *
     * Updated subtitle language
     */
    UPDATED_SUBTITLE("updatedSubtitle"),

    /**
     * [Event.DID_UPDATE_BITRATE] data
     *
     * Type: Long
     *
     * Bits per second
     */
    BITRATE("bitrate"),

    /**
     * [Event.DID_RECEIVE_INPUT_KEY] data
     *
     * Type: String
     *
     * Input key received. Can be any of keys specified in [Key] class
     */
    INPUT_KEY_CODE("inputKeyCode"),

    /**
     * [Event.DID_RECEIVE_INPUT_KEY] data
     *
     * Type: String
     *
     * Input key received. Can be [Action.UP] or [Action.DOWN]
     */
    INPUT_KEY_ACTION("inputKeyAction"),

    /**
     * [Event.DID_CHANGE_SCREEN_ORIENTATION] data
     *
     * Type: io.clappr.player.components.Orientation
     *
     * The screen orientation.
     */
    ORIENTATION("orientation")


}
