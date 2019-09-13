package io.clappr.player.components

import io.clappr.player.base.ClapprOption.*
import io.clappr.player.base.Event.*
import io.clappr.player.base.InternalEvent.DID_UPDATE_OPTIONS
import io.clappr.player.base.NamedType
import io.clappr.player.base.Options
import io.clappr.player.base.UIObject
import io.clappr.player.components.Playback.MediaType.LIVE
import io.clappr.player.components.Playback.MediaType.UNKNOWN

typealias PlaybackSupportCheck = (String, String?) -> Boolean

typealias PlaybackFactory = (String, String?, Options) -> Playback

data class PlaybackEntry(
    val name: String = "",
    val supportsSource: PlaybackSupportCheck,
    val factory: PlaybackFactory
)

abstract class Playback(
    var source: String,
    var mimeType: String? = null,
    options: Options = Options(),
    override val name: String = "",
    val supportsSource: PlaybackSupportCheck = { _, _ -> false }
) : UIObject(), NamedType {

    enum class MediaType {
        UNKNOWN,
        VOD,
        LIVE
    }

    enum class State {
        NONE, IDLE, PLAYING, PAUSED, STALLING, ERROR
    }

    init {
        require(supportsSource(source, mimeType)) {
            "Attempt to initialize a playback with an unsupported source"
        }
    }

    var options: Options = options
        set(options) {
            field = options
            trigger(DID_UPDATE_OPTIONS.value)
        }

    open val mediaType: MediaType
        get() = UNKNOWN

    open val duration: Double
        get() = Double.NaN

    open val position: Double
        get() = Double.NaN

    open val state: State
        get() = State.NONE

    open val canPlay: Boolean
        get() = false

    open val canPause: Boolean
        get() = false

    open val canSeek: Boolean
        get() = false

    open val isDvrAvailable: Boolean
        get() = false

    open val isDvrInUse: Boolean
        get() = false

    open val bitrate: Long
        get() = 0

    open val avgBitrate: Long
        get() = 0

    open val currentDate: Long? = null

    open val currentTime: Long? = null

    /**
     * Playback volume. Its not the device volume.
     * If the playback has this capability. You can set the volume from 0.0f to 1.0f.
     * Where 0.0f is muted and 1.0f is the playback maximum volume.
     * PS.: If you set a volume less than 0.0f we'll set the volume to 0.0f
     * PS.: If you set a volume greater than 1.0f we'll set the volume to 1.0f
     */
    open var volume: Float? = null

    val availableAudios = mutableSetOf<String>()

    val availableSubtitles = mutableSetOf<String>()

    protected var internalSelectedAudio: String? = null
    open var selectedAudio: String?
        /**
         * @throws IllegalArgumentException when audio is not available
         */
        set(value) {
            require(value in availableAudios) { "Audio not available" }

            internalSelectedAudio = value
            trigger(DID_UPDATE_AUDIO.value)
        }
        get() = internalSelectedAudio

    protected var internalSelectedSubtitle = SubtitleLanguage.OFF.value
    open var selectedSubtitle: String
        /**
         * @throws IllegalArgumentException when subtitle is not available
         */
        set(value) {
            require(value in availableSubtitles) { "Subtitle not available" }

            internalSelectedSubtitle = value
            trigger(DID_UPDATE_SUBTITLE.value)
        }
        get() = internalSelectedSubtitle

    override fun render(): UIObject {
        if (mediaType != LIVE) configureStartAt()

        if (!play()) once(READY.value) { play() }

        return this
    }

    open fun destroy() {
        stopListening()
    }

    open fun play(): Boolean {
        return false
    }

    open fun pause(): Boolean {
        return false
    }

    open fun stop(): Boolean {
        return false
    }

    open fun seek(seconds: Int): Boolean {
        return false
    }

    open fun seekToLivePosition(): Boolean {
        return false
    }

    open fun load(source: String, mimeType: String? = null): Boolean {
        val supported = supportsSource(source, mimeType)
        if (supported) {
            this.source = source
            this.mimeType = mimeType
        }
        return supported
    }

    open fun startAt(seconds: Int) = false

    private val defaultAudio: String?
        get() = options[DEFAULT_AUDIO.value] as? String

    private val defaultSubtitle: String?
        get() = options[DEFAULT_SUBTITLE.value] as? String

    fun setupInitialMediasFromClapprOptions() {
        defaultAudio?.toLowerCase()
            .takeIf { it in availableAudios }
            ?.let { selectedAudio = it }

        defaultSubtitle?.toLowerCase()
            .takeIf { it in availableSubtitles }
            ?.let { selectedSubtitle = it }
    }

    private fun configureStartAt() {
        if (options.containsKey(START_AT.value))
            once(READY.value) {
                (options[START_AT.value] as? Number)?.let {
                    startAt(it.toInt())
                }
                options.remove(START_AT.value)
            }
    }
}
