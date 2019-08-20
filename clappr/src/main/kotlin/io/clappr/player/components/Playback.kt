package io.clappr.player.components

import io.clappr.player.base.ClapprOption.*
import io.clappr.player.base.Event.MEDIA_OPTIONS_UPDATE
import io.clappr.player.base.Event.READY
import io.clappr.player.base.InternalEvent
import io.clappr.player.base.NamedType
import io.clappr.player.base.Options
import io.clappr.player.base.UIObject
import io.clappr.player.log.Logger
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

typealias PlaybackSupportCheck = (String, String?) -> Boolean

typealias PlaybackFactory = (String, String?, Options) -> Playback

data class PlaybackEntry(val name: String = "", val supportsSource: PlaybackSupportCheck, val factory: PlaybackFactory)

abstract class Playback(
        var source: String,
        var mimeType: String? = null,
        options: Options = Options(),
        override val name: String = "",
        val supportsSource: PlaybackSupportCheck = { _, _ -> false }) : UIObject(), NamedType {

    enum class MediaType {
        UNKNOWN,
        VOD,
        LIVE
    }

    enum class State {
        NONE, IDLE, PLAYING, PAUSED, STALLING, ERROR
    }

    init {
        if (!supportsSource(source, mimeType)) {
            throw IllegalArgumentException("Attempt to initialize a playback with an unsupported source")
        }
    }

    open fun destroy() {
        stopListening()
    }

    var options: Options = options
        set(options) {
            field = options
            trigger(InternalEvent.DID_UPDATE_OPTIONS.value)
        }

    private val audioKeys = mapOf(
            AudioLanguage.ORIGINAL.value to listOf("original", "und"),
            AudioLanguage.PORTUGUESE.value to listOf("pt", "por"),
            AudioLanguage.ENGLISH.value to listOf("en", "eng")
    )

    private val subtitleKeys = mapOf(
            SubtitleLanguage.OFF.value to listOf("", "off"),
            SubtitleLanguage.PORTUGUESE.value to listOf("pt", "por")
    )

    open val mediaType: MediaType
        get() = MediaType.UNKNOWN

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

    open val hasMediaOptionAvailable: Boolean
        get() = mediaOptionList.isNotEmpty()

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

    protected var mediaOptionList = LinkedList<MediaOption>()
    var selectedMediaOptionList = ArrayList<MediaOption>()
        private set

    var selectedAudio: String? = null
        private set

    var selectedSubtitle: String? = null
        private set

    fun clearMedia() {
        selectedAudio = null
        selectedSubtitle = null
    }

    companion object {
        const val mediaOptionsArrayJson = "media_option"
        const val mediaOptionsNameJson = "name"
        const val mediaOptionsTypeJson = "type"
    }

    fun addAvailableMediaOption(media: MediaOption, index: Int = mediaOptionList.size) {
        mediaOptionList.add(index, media)
    }

    fun availableMediaOptions(type: MediaOptionType): List<MediaOption> {
        return mediaOptionList.filter { it.type == type }
    }

    fun hasMediaOptionAvailable(mediaOption: MediaOption): Boolean {
        return mediaOptionList.contains(mediaOption)
    }

    fun selectedMediaOption(type: MediaOptionType): MediaOption? {
        val selectedList = selectedMediaOptionList.filter { it.type == type }
        return if (!selectedList.isEmpty()) selectedList.first() else null
    }

    fun createAudioMediaOptionFromLanguage(language: String, raw: Any?): MediaOption {
        return audioKeys.entries.find { entry -> entry.value.contains(language.toLowerCase()) }?.let {
            MediaOption(it.key, MediaOptionType.AUDIO, raw, null)
        } ?: MediaOption(language, MediaOptionType.AUDIO, raw, null)
    }

    fun createOriginalOption(raw: Any?) = MediaOption(AudioLanguage.ORIGINAL.value, MediaOptionType.AUDIO, raw, null)

    fun createSubtitleMediaOptionFromLanguage(language: String, raw: Any?): MediaOption {
        return subtitleKeys.entries.find { entry -> entry.value.contains(language.toLowerCase()) }?.let {
            MediaOption(it.key, MediaOptionType.SUBTITLE, raw, null)
        } ?: MediaOption(language, MediaOptionType.SUBTITLE, raw, null)
    }

    open fun setSelectedMediaOption(mediaOption: MediaOption) {
        selectedMediaOptionList.removeAll { it.type == mediaOption.type }
        selectedMediaOptionList.add(mediaOption)

        trigger(MEDIA_OPTIONS_UPDATE.value)
    }

    open fun setSelectedAudio(audio: String) {
        selectedAudio = audio
        trigger(MEDIA_OPTIONS_UPDATE.value)
    }

    open fun setSelectedSubtitle(subtitle: String) {
        selectedSubtitle = subtitle
        trigger(MEDIA_OPTIONS_UPDATE.value)
    }

    fun setupInitialAudioFromOptions() {
        (options[DEFAULT_AUDIO.value] as? String)?.let { setSelectedAudio(it) }
    }

    fun setupInitialSubtitleFromOptions() {
        (options[DEFAULT_SUBTITLE.value] as? String)?.let { setSelectedSubtitle(it) }
    }

    fun setupInitialMediasFromClapprOptions() {
        try {
            options[SELECTED_MEDIA_OPTIONS.value]?.let {
                val jsonObject = JSONObject(it as? String)
                val jsonArray = jsonObject.getJSONArray(mediaOptionsArrayJson)
                (0 until jsonArray.length())
                        .map { jsonArray.getJSONObject(it) }
                        .forEach {
                            setSelectedMediaOption(
                                    it.getString(mediaOptionsNameJson), it.getString(mediaOptionsTypeJson))
                        }
            }
        } catch (jsonException: JSONException) {
            Logger.error(name, "Parser Json Exception ${jsonException.message}")
        }
    }

    private fun setSelectedMediaOption(mediaOptionName: String, mediaOptionType: String) {
        mediaOptionList
            .map {
                it
            }
            .find {
                it.type.name == mediaOptionType && it.name == mediaOptionName.toLowerCase()
            }
            ?.let {
                setSelectedMediaOption(it)
            }
    }

    open fun load(source: String, mimeType: String? = null): Boolean {
        val supported = supportsSource(source, mimeType)
        if (supported) {
            this.source = source
            this.mimeType = mimeType
        }
        return supported
    }

    override fun render(): UIObject {
        if (mediaType != MediaType.LIVE) configureStartAt()

        if (!play()) once(READY.value, { play() })

        return this
    }

    open fun startAt(seconds: Int): Boolean {
        return false
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
