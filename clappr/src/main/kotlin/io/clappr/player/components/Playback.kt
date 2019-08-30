package io.clappr.player.components

import io.clappr.player.base.ClapprOption.*
import io.clappr.player.base.Event.*
import io.clappr.player.base.InternalEvent.DID_UPDATE_OPTIONS
import io.clappr.player.base.NamedType
import io.clappr.player.base.Options
import io.clappr.player.base.UIObject
import io.clappr.player.components.AudioLanguage.*
import io.clappr.player.components.MediaOptionType.AUDIO
import io.clappr.player.components.MediaOptionType.SUBTITLE
import io.clappr.player.components.Playback.MediaType.LIVE
import io.clappr.player.components.Playback.MediaType.UNKNOWN
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

    var options: Options = options
        set(options) {
            field = options
            trigger(DID_UPDATE_OPTIONS.value)
        }

    val availableAudios = mutableListOf<String>()

    val availableSubtitles = mutableListOf<String>()

    var selectedAudio = ""
        set(value) {
            if (value !in availableAudios) return

            changeAudioTrack(value)

            field = value
            trigger(DID_SELECT_AUDIO.value)
        }

    var selectedSubtitle = "off"

    open fun changeAudioTrack(name: String) {}

    @Deprecated("")
    var selectedMediaOptionList = ArrayList<MediaOption>()
        private set

    @Deprecated("")
    protected var mediaOptionList = LinkedList<MediaOption>()

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

    private val audioKeys = mapOf(
        ORIGINAL.value to listOf("original", "und"),
        PORTUGUESE.value to listOf("pt", "por"),
        ENGLISH.value to listOf("en", "eng")
    )

    private val subtitleKeys = mapOf(
        SubtitleLanguage.OFF.value to listOf("", "off"),
        SubtitleLanguage.PORTUGUESE.value to listOf("pt", "por")
    )

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

    open fun setSelectedMediaOption(mediaOption: MediaOption) {
        selectedMediaOptionList.removeAll { it.type == mediaOption.type }
        selectedMediaOptionList.add(mediaOption)

        when (mediaOption.type) {
            AUDIO -> trigger(DID_SELECT_AUDIO.value)
            SUBTITLE -> trigger(DID_SELECT_SUBTITLE.value)
        }

        trigger(MEDIA_OPTIONS_UPDATE.value)
    }

    open fun load(source: String, mimeType: String? = null): Boolean {
        val supported = supportsSource(source, mimeType)
        if (supported) {
            this.source = source
            this.mimeType = mimeType
        }
        return supported
    }

    open fun startAt(seconds: Int): Boolean {
        return false
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
            MediaOption(it.key, AUDIO, raw, null)
        } ?: MediaOption(language, AUDIO, raw, null)
    }

    fun createOriginalOption(raw: Any?) = MediaOption(ORIGINAL.value, AUDIO, raw, null)

    fun createSubtitleMediaOptionFromLanguage(language: String, raw: Any?): MediaOption {
        return subtitleKeys.entries.find { entry -> entry.value.contains(language.toLowerCase()) }?.let {
            MediaOption(it.key, SUBTITLE, raw, null)
        } ?: MediaOption(language, SUBTITLE, raw, null)
    }

    private val defaultAudio: String?
        get() = options[DEFAULT_AUDIO.value] as? String

    private val defaultSubtitle: String?
        get() = options[DEFAULT_SUBTITLE.value] as? String

    private fun List<Pair<String, String>>.selected(mediaOptionType: MediaOptionType) =
            firstOrNull { (_, type) -> type == mediaOptionType.name }?.let { (value, _) -> value }

    private val selectedMediaOptions: List<Pair<String, String>>?
        get() {
            try {
                return options[SELECTED_MEDIA_OPTIONS.value]?.let { selectedMediaOptions ->
                    val jsonObject = JSONObject(selectedMediaOptions as? String)
                    val jsonArray = jsonObject.getJSONArray(mediaOptionsArrayJson)
                    (0 until jsonArray.length())
                            .map { jsonArray.getJSONObject(it) }
                            .map {
                                val type = it.getString(mediaOptionsTypeJson)
                                val name = it.getString(mediaOptionsNameJson)
                                name to type
                            }
                }
            } catch (jsonException: JSONException) {
                Logger.error(name, "Parser Json Exception ${jsonException.message}")
                return null
            }
        }

    fun setupInitialMediasFromClapprOptions() {
        val audio = defaultAudio ?: selectedMediaOptions?.selected(AUDIO)
        val subtitle = (defaultSubtitle) ?: selectedMediaOptions?.selected(SUBTITLE)

        audio?.let { setSelectedMediaOption(it, AUDIO.name) }
        subtitle?.let { setSelectedMediaOption(it, SUBTITLE.name) }
    }

    private fun setSelectedMediaOption(mediaOptionName: String, mediaOptionType: String) {
        mediaOptionList
            .find { it.type.name == mediaOptionType && it.name == mediaOptionName.toLowerCase() }
            ?.let { setSelectedMediaOption(it) }
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

    companion object {
        const val mediaOptionsArrayJson = "media_option"
        const val mediaOptionsNameJson = "name"
        const val mediaOptionsTypeJson = "type"
    }
}
