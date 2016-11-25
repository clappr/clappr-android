package com.globo.clappr.components

import com.globo.clappr.base.UIObject
import com.globo.clappr.base.NamedType
import com.globo.clappr.base.Options
import kotlin.reflect.companionObjectInstance

interface PlaybackSupportInterface: NamedType {
    fun supportsSource(source: String, mimeType: String? = null): Boolean
}

abstract class Playback(var source: String, var mimeType: String? = null, val options: Options = Options()) : UIObject(), NamedType {

    enum class State {
        NONE, IDLE, PLAYING, PAUSED, STALLED, ERROR
    }

    companion object: PlaybackSupportInterface {
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

    open fun play(): Boolean { return false }
    open fun pause(): Boolean { return false }
    open fun stop(): Boolean { return false }
    open fun seek(seconds: Int): Boolean { return false }

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
        if (options.autoPlay) {
            play()
        }
        return this
    }
}
