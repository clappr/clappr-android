package com.globo.clappr.playback

import android.media.AudioManager
import android.media.MediaPlayer
import android.media.TimedMetaData
import android.media.TimedText
import android.util.Log
import com.globo.clappr.base.Options
import com.globo.clappr.components.Playback

enum class PLAYBACK_STATE {
    NONE,
    IDLE,
    PLAYING,
    PAUSED,
    STALLED
}

enum class MEDIA_TYPE {
    UNKNOWN,
    VOD,
    LIVE
}

class MediaPlayerPlayback(var source: String, var mimeType: String? = null, var options: Options?): Playback(options) {

    companion object {
        val TAG: String = "MediaPlayerPlayback"
        fun supportsSource(source: String, mediaType: String? = null) : Boolean {
            return true
        }
    }

    private var mediaPlayer: MediaPlayer

    init {
        mediaPlayer = MediaPlayer()

        // TODO
        mediaPlayer.setOnErrorListener { mp, what, extra -> throw UnsupportedOperationException("not implemented") }
        mediaPlayer.setOnInfoListener { mediaPlayer, what, extra ->  throw UnsupportedOperationException("not implemented") }

        mediaPlayer.setOnSeekCompleteListener {
            Log.i(TAG, "seek completed")
        }

        mediaPlayer.setOnVideoSizeChangedListener { mp, width, height ->
            Log.i(TAG, "video size: " + width + " / " + height)
        }

        mediaPlayer.setOnPreparedListener {
            mediaPlayer.start()
            updateState(PLAYBACK_STATE.PLAYING)
        }

        mediaPlayer.setOnBufferingUpdateListener { mp, percent ->
            Log.i(TAG, "buffered percentage: " + percent)
        }

        mediaPlayer.setOnCompletionListener {
            updateState(PLAYBACK_STATE.IDLE)
        }

        mediaPlayer.setDataSource(source)

        // TODO
        mediaPlayer.setDisplay(null)

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer.setScreenOnWhilePlaying(true)

        updateState(PLAYBACK_STATE.IDLE)
    }

    val duration: Double
        get() {
            if (state != PLAYBACK_STATE.NONE) {
                val mediaDuration = mediaPlayer.duration
                if (mediaDuration > -1) {
                    return mediaDuration / 1000.0
                }
            }

            return Double.NaN
        }

    val position: Double
        get() {
            if (state != PLAYBACK_STATE.NONE) {
                val currentPosition = mediaPlayer.currentPosition
                if (currentPosition > -1) {
                    return currentPosition / 1000.0
                }
            }

            return Double.NaN
        }

    var state: PLAYBACK_STATE = PLAYBACK_STATE.NONE
    var type: MEDIA_TYPE = MEDIA_TYPE.UNKNOWN

    val canPlay: Boolean
        get() = (state != PLAYBACK_STATE.NONE) && (type != MEDIA_TYPE.UNKNOWN)
    val canPause: Boolean
        get() = (state != PLAYBACK_STATE.NONE) && (type == MEDIA_TYPE.VOD)
    val canSeek: Boolean
        get() = (state != PLAYBACK_STATE.NONE) && (type == MEDIA_TYPE.VOD)

    fun configure(options: Options) {
        this.options = options
    }

    fun load(source: String, mimeType: String?): Boolean {
        this.source = source
        this.mimeType = mimeType

        return true
    }

    fun play(): Boolean {
        if (canPlay) {
            if (state == PLAYBACK_STATE.IDLE) {
                mediaPlayer.prepareAsync()
            }
            if (state != PLAYBACK_STATE.PLAYING) {
                // TODO
                // willPlay
            }
            return true
        } else {
            return false
        }
    }

    fun pause(): Boolean {
        return canPause
    }

    fun stop(): Boolean {
        return true
    }

    fun seek(position: Double): Boolean {
        return canSeek
    }

    private fun updateState(newState: PLAYBACK_STATE) {
        if (newState != state) {
            var previousState = state
            state = newState
            when (state) {
                PLAYBACK_STATE.IDLE -> {
                    if (previousState == PLAYBACK_STATE.NONE) {
                        // TODO
                        // ready
                    } else if (previousState == PLAYBACK_STATE.PLAYING) {
                        // TODO
                        // ended
                    }
                }
                PLAYBACK_STATE.PLAYING -> {
                    // TODO
                    // playing
                }
            }
        }

    }
}