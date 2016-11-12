package com.globo.clappr.components

import com.globo.clappr.base.BaseObject
import com.globo.clappr.base.NamedType
import com.globo.clappr.base.Options

interface PlaybackSupportInterface: NamedType {
    fun supportsSource(source: String, mimeType: String? = null): Boolean
}

abstract class Playback(var source: String, var mimeType: String? = null, val options: Options) : BaseObject(), NamedType {

    enum class State {
        NONE, IDLE, PLAYING, PAUSED, STALLED
    }

    companion object: PlaybackSupportInterface {
        override val name = ""

        override fun supportsSource(source: String, mimeType: String?): Boolean {
            return false
        }
    }

    val duration: Double
        get() = Double.NaN

    val position: Double
        get() = Double.NaN

    val state: State
        get() = State.NONE

    val canPlay: Boolean
        get() = false

    val canPause: Boolean
        get() = false

    val canSeek: Boolean
        get() = false

    fun play() {}
    fun pause() {}
    fun stop() {}
    fun seek(seconds: Int) {}
    fun seekPercentage(percent: Double) {}

    fun load(source: String, mimeType: String? = null): Boolean { return supportsSource(source, mimeType) }
}

