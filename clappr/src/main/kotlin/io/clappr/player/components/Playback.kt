package io.clappr.player.components

import android.os.Bundle
import io.clappr.player.base.*
import io.clappr.player.log.Logger
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.full.companionObjectInstance

interface PlaybackSupportInterface : NamedType {
    fun supportsSource(source: String, mimeType: String? = null): Boolean
}

abstract class Playback(var source: String, var mimeType: String? = null, options: Options = Options()) : UIObject(), NamedType {

    enum class MediaType {
        UNKNOWN,
        VOD,
        LIVE
    }

    enum class State {
        NONE, IDLE, PLAYING, PAUSED, STALLED, ERROR
    }

    companion object : PlaybackSupportInterface {
        override val name = ""

        @JvmStatic
        override fun supportsSource(source: String, mimeType: String?): Boolean {
            return false
        }
    }

    init {
        if (!supportsSource(source, mimeType)) {
            throw IllegalArgumentException("Attempt to initialize a playback with an unsupported source")
        }
    }

    open fun destroy() {
        stopListening()
    }

    var options : Options = options
        set(options)  {
            field = options
            trigger(InternalEvent.DID_UPDATE_OPTIONS.value)
        }

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

    private var mediaOptionList = LinkedList<MediaOption>()
    private var selectedMediaOptionList = ArrayList<MediaOption>()

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

    open fun setSelectedMediaOption(mediaOption: MediaOption) {
        selectedMediaOptionList.removeAll { it.type == mediaOption.type }
        selectedMediaOptionList.add(mediaOption)

        trigger(InternalEvent.MEDIA_OPTIONS_UPDATE.value)

        val bundle = Bundle()
        bundle.putString(EventData.MEDIA_OPTIONS_SELECTED_RESPONSE.value, convertSelectedMediaOptionsToJson())
        trigger(Event.MEDIA_OPTIONS_SELECTED.value, bundle)
    }

    open fun resetAvailableMediaOptions() {
        mediaOptionList.clear()
    }

    fun convertSelectedMediaOptionsToJson(): String {
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
                        .forEach { setSelectedMediaOption(it.getString(mediaOptionsNameJson), it.getString(mediaOptionsTypeJson)) }
            }
        } catch (jsonException: JSONException) {
            Logger.error(name, "Parser Json Exception ${jsonException.message}")
        }
    }

    private fun setSelectedMediaOption(mediaOptionName: String, mediaOptionType: String) {
        mediaOptionList.forEach {
            if (it.name.toUpperCase() == mediaOptionName.toUpperCase()
                    && it.type.name.toUpperCase() == mediaOptionType.toUpperCase()) {
                setSelectedMediaOption(it)
            }
        }
    }

    internal fun supportsSource(source: String, mimeType: String?): Boolean {
        val companion = javaClass.kotlin.companionObjectInstance as? PlaybackSupportInterface
        return companion?.supportsSource(source, mimeType) ?: false
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

        if (!play()) once(Event.READY.value, Callback.wrap { play() })

        return this
    }

    private fun configureStartAt() {
        if (options.containsKey(ClapprOption.START_AT.value))
            once(Event.READY.value, Callback.wrap {
                (options[ClapprOption.START_AT.value] as? Number)?.let {
                    seek(it.toInt())
                }
                options.remove(ClapprOption.START_AT.value)
            })
    }
}
