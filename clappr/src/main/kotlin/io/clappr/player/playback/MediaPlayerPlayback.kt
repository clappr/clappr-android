package io.clappr.player.playback

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import io.clappr.player.base.Callback
import io.clappr.player.base.Event
import io.clappr.player.base.Options
import io.clappr.player.base.UIObject
import io.clappr.player.components.Playback
import io.clappr.player.components.PlaybackSupportInterface
import io.clappr.player.log.Logger


class MediaPlayerPlayback(source: String, mimeType: String? = null, options: Options = Options()): Playback(source, mimeType, options) {

    enum class InternalState {
        NONE,
        IDLE,
        ERROR,
        ATTACHED,
        PREPARED,
        STARTED,
        PAUSED,
        STOPPED,
        BUFFERING
    }

    companion object: PlaybackSupportInterface {
        const val TAG: String = "MediaPlayerPlayback"

        override fun supportsSource(source: String, mimeType: String?): Boolean {
            return true
        }

        override val name: String?
            get() = "media_player"
    }

    override val viewClass: Class<*>
        get() = PlaybackView::class.java

    private var mediaPlayer = MediaPlayer()

    private var internalState: InternalState = InternalState.NONE
        set(value) {
            val oldState = state

            field = value

            sendUpdateStateEvents(oldState)
        }

    init {
        mediaPlayer.setOnInfoListener { _, what, extra ->
            infoLog(what, extra)?.let { Logger.info(TAG, it) }
            when(what) {
                MediaPlayer.MEDIA_INFO_BUFFERING_START -> internalState = InternalState.BUFFERING
                MediaPlayer.MEDIA_INFO_BUFFERING_END -> internalState = InternalState.STARTED
            }
            false
        }

        mediaPlayer.setOnErrorListener { _, what, extra ->
            Logger.info(TAG, "error: $what($extra)" )
            internalState = InternalState.ERROR
            true
        }

        mediaPlayer.setOnSeekCompleteListener {
            Logger.info(TAG, "seek completed")
            if (mediaPlayer.isPlaying) {
                trigger(Event.PLAYING.value)
            }
        }

        mediaPlayer.setOnVideoSizeChangedListener { _, width, height ->
            Logger.info(TAG, "video size: $width / $height")
            (view as? PlaybackView)?.videoWidth = width
            (view as? PlaybackView)?.videoHeight = height
            view?.requestLayout()
        }

        mediaPlayer.setOnPreparedListener {
            internalState = InternalState.PREPARED
            mediaPlayer.start()
            internalState = InternalState.STARTED
        }

        mediaPlayer.setOnBufferingUpdateListener { _, percent ->
            Logger.info(TAG, "buffered percentage: $percent")
            val bundle = Bundle()

            bundle.putDouble("percentage", percent.toDouble())
            trigger(Event.BUFFER_UPDATE.value, bundle)
        }

        mediaPlayer.setOnCompletionListener {
            internalState = InternalState.ATTACHED
        }

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer.setScreenOnWhilePlaying(true)

        try {
            mediaPlayer.setDataSource(source)

            internalState = InternalState.IDLE

            mediaPlayer.setDisplay(null)
            val holder = (view as? PlaybackView)?.holder
            holder?.addCallback(object: SurfaceHolder.Callback {
                override fun surfaceChanged(sh: SurfaceHolder, format: Int, width: Int, height: Int) {
                    Logger.info(TAG, "surface changed: $format/$width/$height")
                }

                override fun surfaceDestroyed(sh: SurfaceHolder) {
                    Logger.info(TAG, "surface destroyed")
                    stop()
                    mediaPlayer.setDisplay(null)
                    internalState = InternalState.IDLE
                }

                override fun surfaceCreated(sh: SurfaceHolder) {
                    Logger.info(TAG, "surface created")
                    mediaPlayer.setDisplay(holder)
                    internalState = InternalState.ATTACHED
                }
            })
        } catch (e: Exception) {
            internalState = InternalState.ERROR
        }
    }

    private fun infoLog(what: Int, extra: Int): String? {
        val log = when(what) {
            MediaPlayer.MEDIA_INFO_BUFFERING_START -> "MEDIA_INFO_BUFFERING_START"
            MediaPlayer.MEDIA_INFO_BUFFERING_END -> "MEDIA_INFO_BUFFERING_END"
            MediaPlayer.MEDIA_INFO_UNKNOWN -> "MEDIA_INFO_UNKNOWN"
            MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING -> "MEDIA_INFO_VIDEO_TRACK_LAGGING"
            MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> "MEDIA_INFO_VIDEO_RENDERING_START"
            MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING -> "MEDIA_INFO_BAD_INTERLEAVING"
            MediaPlayer.MEDIA_INFO_NOT_SEEKABLE -> "MEDIA_INFO_NOT_SEEKABLE"
            MediaPlayer.MEDIA_INFO_METADATA_UPDATE -> "MEDIA_INFO_METADATA_UPDATE"
            MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE -> "MEDIA_INFO_UNSUPPORTED_SUBTITLE"
            MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT -> "MEDIA_INFO_SUBTITLE_TIMED_OUT"
            else -> "UNKNOWN"
        }

        return "$log ($what / $extra)"
    }

    class PlaybackView(context: Context?) : SurfaceView(context) {
        var videoHeight = 0
        var videoWidth = 0

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            var width = View.getDefaultSize(videoWidth, widthMeasureSpec)
            var height = View.getDefaultSize(videoHeight, heightMeasureSpec)
            Logger.info(TAG, "onMeasure: $width/$height")

            if ( (videoWidth > 0) && (videoHeight > 0) ) {
                if (videoWidth * height > width * videoHeight) {
                    Logger.info(TAG, "image too tall")
                    height = width * videoHeight / videoWidth
                } else if (videoWidth * height <  width * videoHeight) {
                    Logger.info(TAG, "image too wide")
                    width = height * videoWidth / videoHeight
                } else {
                    Logger.info(TAG, "aspect ratio is correct: $width/$height = $videoWidth/$videoHeight")
                }
            }

            Logger.info(TAG, "setting size to: $width/$height")
            setMeasuredDimension(width, height)
        }
    }

    override fun render() : UIObject {
        if (!play()) {
            this.once(Event.READY.value, Callback.wrap { _: Bundle? -> play() })
        }

        return this
    }

    override val state: State
        get() = getState(internalState)

    private fun getState (internal: InternalState) : State {
        return when (internal) {
            InternalState.NONE, InternalState.IDLE -> { State.NONE }
            InternalState.ATTACHED, InternalState.PREPARED, InternalState.STOPPED -> { State.IDLE }
            InternalState.STARTED -> { State.PLAYING }
            InternalState.PAUSED -> { State.PAUSED }
            InternalState.BUFFERING -> { State.STALLED }
            InternalState.ERROR -> { State.ERROR}
        }
    }

    override val mediaType: MediaType
        get() {
            return if (mediaPlayer.duration > -1) MediaType.VOD else MediaType.LIVE
        }

    override val duration: Double
        get() {
            if (state != State.NONE && state != State.ERROR && internalState != InternalState.ATTACHED) {
                val mediaDuration = mediaPlayer.duration
                if (mediaDuration > -1) {
                    return mediaDuration / 1000.0
                }
            }

            return Double.NaN
        }

    override val position: Double
        get() {
            if (state != State.NONE && state != State.ERROR && internalState != InternalState.ATTACHED) {
                val currentPosition = mediaPlayer.currentPosition
                if (currentPosition > -1) {
                    return currentPosition / 1000.0
                }
            }

            return Double.NaN
        }

    override val canPlay: Boolean
        get() = ( (state != State.NONE) && (state != State.ERROR) )
    override val canPause: Boolean
        get() = ( (state != State.NONE) && (state != State.ERROR) && (mediaType == MediaType.VOD) )
    override val canSeek: Boolean
        get() = ( (state != State.NONE) && (state != State.ERROR) && (mediaType == MediaType.VOD) )
    val canStop: Boolean
        get() = ( (state == State.PLAYING) || (state == State.PAUSED) || (state == State.STALLED) )


    override fun play(): Boolean {
        if (canPlay) {
            if (state != State.PLAYING) {
                trigger(Event.WILL_PLAY.value)
            }
            if (internalState == InternalState.ATTACHED) {
                mediaPlayer.prepareAsync()
            } else {
                mediaPlayer.start()
            }
            if (state == State.PAUSED) {
                internalState = InternalState.STARTED
            }
            return true
        } else {
            return false
        }
    }

    override fun pause(): Boolean {
        return if (canPause) {
            if ( (state == State.PLAYING) || (state == State.STALLED) ) {
                trigger(Event.WILL_PAUSE.value)
                mediaPlayer.pause()
            }
            if (state == State.PLAYING) {
                internalState = InternalState.PAUSED
            }
            true
        } else {
            false
        }
    }

    override fun stop(): Boolean {
        return if (canStop) {
            trigger(Event.WILL_STOP.value)
            try {
                mediaPlayer.stop()
                internalState = InternalState.STOPPED
            } catch (iee: IllegalStateException) {
                Logger.info(TAG, "stop - $iee")
            }
            true
        } else {
            false
        }
    }

    override fun seek(seconds: Int): Boolean {
        return if (canSeek) {
            trigger(Event.WILL_SEEK.value)
            mediaPlayer.seekTo(seconds * 1000)
            true
        } else {
            false
        }
    }

    private fun sendUpdateStateEvents(previousState: State) {
        if (state != previousState) {
            when (state) {
                State.IDLE -> {
                    if (previousState == State.NONE) {
                        trigger(Event.READY.value)
                    } else if (internalState == InternalState.STOPPED) {
                        trigger(Event.DID_STOP.value)
                    } else if (previousState == State.PLAYING) {
                        trigger(Event.DID_COMPLETE.value)
                    }
                }
                State.PLAYING -> {
                    trigger(Event.PLAYING.value)
                }
                State.PAUSED -> {
                    trigger(Event.DID_PAUSE.value)
                }
                State.ERROR -> {
                    trigger(Event.ERROR.value)
                }
                State.STALLED -> {
                    trigger(Event.STALLED.value)
                }
                State.NONE -> { }
            }
        }
    }
}
