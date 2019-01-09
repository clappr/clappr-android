package io.clappr.player.components

import android.os.Bundle
import io.clappr.player.base.*
import io.clappr.player.log.Logger
import org.json.JSONArray
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

    open val currentDate: Long? = null

    open val currentTime: Long? = null

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
    protected var selectedMediaOptionList = ArrayList<MediaOption>()

    private val mediaOptionsArrayJson = "media_option"
    private val mediaOptionsNameJson = "name"
    private val mediaOptionsTypeJson = "type"

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

        trigger(InternalEvent.MEDIA_OPTIONS_UPDATE.value)

        val bundle = Bundle()
        bundle.putString(EventData.MEDIA_OPTIONS_SELECTED_RESPONSE.value, convertSelectedMediaOptionsToJson())
        trigger(Event.MEDIA_OPTIONS_SELECTED.value, bundle)
    }

    private fun convertSelectedMediaOptionsToJson(): String {
        val result = JSONObject()
        val jsonArray = JSONArray()
        selectedMediaOptionList.forEach {
            val jsonObject = JSONObject()
            jsonObject.put(mediaOptionsNameJson, it.name)
            jsonObject.put(mediaOptionsTypeJson, it.type)
            jsonArray.put(jsonObject)
        }
        result.put(mediaOptionsArrayJson, jsonArray)
        return result.toString()
    }

    fun setupInitialMediasFromClapprOptions() {
        try {
            options[ClapprOption.SELECTED_MEDIA_OPTIONS.value]?.let {
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
        mediaOptionList.map { it }.find { it.type.name == mediaOptionType && it.name == mediaOptionName.toLowerCase() }
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

        if (!play()) once(Event.READY.value, { play() })

        return this
    }

    open fun startAt(seconds: Int): Boolean {
        return false
    }

    private fun configureStartAt() {
        if (options.containsKey(ClapprOption.START_AT.value))
            once(Event.READY.value) {
                (options[ClapprOption.START_AT.value] as? Number)?.let {
                    startAt(it.toInt())
                }
                options.remove(ClapprOption.START_AT.value)
            }
    }
}
