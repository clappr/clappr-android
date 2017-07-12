package io.clappr.player.components

import io.clappr.player.base.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.full.companionObjectInstance

interface PlaybackSupportInterface : NamedType {
    fun supportsSource(source: String, mimeType: String? = null): Boolean
}

abstract class Playback(var source: String, var mimeType: String? = null, val options: Options = Options()) : UIObject(), NamedType {

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

    private var mediaOptionList = LinkedList<MediaOption>()
    private var selectedMediaOptionList = ArrayList<MediaOption>()

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
    }

    open fun resetAvailableMediaOptions() {
        mediaOptionList.clear()
    }

    fun convertSelectedMediaOptionsToJson(mediaOption: MediaOption): String {
        val json = JSONArray()
        selectedMediaOptionList.forEach {
            val jsonObject = JSONObject()
            jsonObject.put("media_option_name", mediaOption.name)
            jsonObject.put("media_option_type", mediaOption.type)
            json.put(jsonObject)
        }

        return json.toString()
    }

    fun setupInitialMediasFromClapprOptions(){
        val jsonArray = JSONArray(options.get(ClapprOption.SELECTED_MEDIA_OPTIONS.value) as? String)
        for (i in 0 until jsonArray.length()) {
            val media = jsonArray.getJSONObject(i)
            setSelectedMediaOption(media.getString("media_option_name"), media.getString("media_option_type"))
        }
    }

    internal fun setSelectedMediaOption(mediaOptionName: String, mediaOptionType: String) {
        mediaOptionList.forEach {
            if(it.name.equals(mediaOptionName) && it.type.name.equals(mediaOptionType)){
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
        if (options.containsKey(ClapprOption.START_AT.value)) {
            once(Event.READY.value, Callback.wrap {
                (options.get(ClapprOption.START_AT.value) as? Int)?.let {
                    seek(it)
                }
                options.remove(ClapprOption.START_AT.value)
            })
        }

        if (options.autoPlay) {
            if (!play()) {
                once(Event.READY.value, Callback.wrap { play() })
            }
        }
        return this
    }
}
