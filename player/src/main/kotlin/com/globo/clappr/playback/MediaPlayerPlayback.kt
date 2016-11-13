package com.globo.clappr.playback

import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import com.globo.clappr.base.ClapprEvent
import com.globo.clappr.base.Options
import com.globo.clappr.components.Playback
import com.globo.clappr.components.PlaybackSupportInterface


class MediaPlayerPlayback(source: String, mimeType: String? = null, options: Options = Options()): Playback(source, mimeType, options) {

    enum class MediaType {
        UNKNOWN,
        VOD,
        LIVE
    }

    companion object: PlaybackSupportInterface {
        val TAG: String = "MediaPlayerPlayback"

        override fun supportsSource(source: String, mimeType: String?): Boolean {
            return true
        }

        override val name: String?
            get() = "media_player"
    }

    private var mediaPlayer: MediaPlayer
    private var internalState: State = State.NONE
    private var type: MediaType = MediaType.UNKNOWN

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
            updateState(State.PLAYING)
        }

        mediaPlayer.setOnBufferingUpdateListener { mp, percent ->
            Log.i(TAG, "buffered percentage: " + percent)
        }

        mediaPlayer.setOnCompletionListener {
            updateState(State.IDLE)
        }

        mediaPlayer.setDataSource(source)

        // TODO
        mediaPlayer.setDisplay(null)

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer.setScreenOnWhilePlaying(true)

        updateState(State.IDLE)
    }

    override val state: State
        get() = internalState

    override val duration: Double
        get() {
            if (state != State.NONE) {
                val mediaDuration = mediaPlayer.duration
                if (mediaDuration > -1) {
                    return mediaDuration / 1000.0
                }
            }

            return Double.NaN
        }

    override val position: Double
        get() {
            if (state != State.NONE) {
                val currentPosition = mediaPlayer.currentPosition
                if (currentPosition > -1) {
                    return currentPosition / 1000.0
                }
            }

            return Double.NaN
        }

    override val canPlay: Boolean
        get() = (state != State.NONE) && (type != MediaType.UNKNOWN)
    override val canPause: Boolean
        get() = (state != State.NONE) && (type == MediaType.VOD)
    override val canSeek: Boolean
        get() = (state != State.NONE) && (type == MediaType.VOD)

    // TODO
    fun configure(options: Options) {
        // this.options = options
    }

    override fun play(): Boolean {
        if (canPlay) {
            if (state == State.IDLE) {
                mediaPlayer.prepareAsync()
            }
            if (state != State.PLAYING) {
                trigger(ClapprEvent.WILL_PLAY.value)
            }
            return true
        } else {
            return false
        }
    }

    private fun updateState(newState: State) {
        if (newState != state) {
            var previousState = state
            internalState = newState
            when (state) {
                State.IDLE -> {
                    if (previousState == State.NONE) {
                        type = MediaType.VOD
                        trigger(ClapprEvent.READY.value)
                    } else if (previousState == State.PLAYING) {
                        // TODO
                        // ended
                    }
                }
                State.PLAYING -> {
                    trigger(ClapprEvent.PLAYING.value)
                }
            }
        }

    }
}