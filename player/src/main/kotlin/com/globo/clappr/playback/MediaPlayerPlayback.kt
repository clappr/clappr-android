package com.globo.clappr.playback

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import android.view.*
import android.widget.RelativeLayout
import com.globo.clappr.base.ClapprEvent
import com.globo.clappr.base.Options
import com.globo.clappr.base.UIObject
import com.globo.clappr.components.Playback
import com.globo.clappr.components.PlaybackSupportInterface


class MediaPlayerPlayback(source: String, mimeType: String? = null, options: Options = Options()): Playback(source, mimeType, options) {

    enum class MediaType {
        UNKNOWN,
        VOD,
        LIVE
    }

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
        val TAG: String = "MediaPlayerPlayback"

        override fun supportsSource(source: String, mimeType: String?): Boolean {
            return true
        }

        override val name: String?
            get() = "media_player"
    }

    private var mediaPlayer: MediaPlayer
    private var type: MediaType = MediaType.UNKNOWN
    private var playbackView : PlaybackView? = null

    private var internalState: InternalState = InternalState.NONE

    init {
        mediaPlayer = MediaPlayer()

        // TODO
//        mediaPlayer.setOnInfoListener { mediaPlayer, what, extra ->  throw UnsupportedOperationException("not implemented") }

        mediaPlayer.setOnErrorListener { mp, what, extra ->
            Log.i(TAG, "error: " + what + "(" + extra + ")" )
            updateState(InternalState.ERROR)
            false
        }

        mediaPlayer.setOnSeekCompleteListener {
            Log.i(TAG, "seek completed")
        }

        mediaPlayer.setOnVideoSizeChangedListener { mp, width, height ->
            Log.i(TAG, "video size: " + width + " / " + height)
            playbackView?.videoWidth = width
            playbackView?.videoHeight = height
            playbackView?.requestLayout()
        }

        mediaPlayer.setOnPreparedListener {
            type = if (mediaPlayer.duration > -1) MediaType.VOD else MediaType.LIVE
            updateState(InternalState.PREPARED)
            mediaPlayer.start()
            updateState(InternalState.STARTED)
        }

        mediaPlayer.setOnBufferingUpdateListener { mp, percent ->
            Log.i(TAG, "buffered percentage: " + percent)
        }

        mediaPlayer.setOnCompletionListener {
            updateState(InternalState.ATTACHED)
        }

        mediaPlayer.setDataSource(source)

        mediaPlayer.setDisplay(null)

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer.setScreenOnWhilePlaying(true)

        updateState(InternalState.IDLE)
    }

    class PlaybackView(context: Context?) : SurfaceView(context) {
        var videoHeight = 0
        var videoWidth = 0

        override fun onTouchEvent(event: MotionEvent?): Boolean {
            return super.onTouchEvent(event)
        }

        override fun onTrackballEvent(event: MotionEvent?): Boolean {
            return super.onTrackballEvent(event)
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            var width = View.getDefaultSize(videoWidth, widthMeasureSpec)
            var height = View.getDefaultSize(videoHeight, heightMeasureSpec)
            Log.i(TAG, "onMeasure: " + width + "/" + height)

            if ( (videoWidth > 0) && (videoHeight > 0) ) {
                if (videoWidth * height > width * videoHeight) {
                    Log.i(TAG, "image too tall")
                    height = width * videoHeight / videoWidth
                } else if (videoWidth * height <  width * videoHeight) {
                    Log.i(TAG, "image too wide")
                    width = height * videoWidth / videoHeight
                } else {
                    Log.i(TAG, "aspect ratio is correct: " + width + "/" + height + " = " + videoWidth + "/" + videoHeight)
                }
            }

            Log.i(TAG, "setting size to: " + width + "/" + height)
            setMeasuredDimension(width, height)
        }
    }

    override fun render() : UIObject {
        if (view is ViewGroup) {
            val playbackContainer = RelativeLayout(context)
            playbackView = PlaybackView(context)
            val holder = playbackView!!.holder
            holder.addCallback(object: SurfaceHolder.Callback {
                override fun surfaceChanged(sh: SurfaceHolder, format: Int, width: Int, height: Int) {
                    Log.i(TAG, "surface changed: " + format + "/" + width + "/" + height)
                }

                override fun surfaceDestroyed(sh: SurfaceHolder) {
                    Log.i(TAG, "surface destroyed")
                    mediaPlayer.stop()
                    mediaPlayer.setDisplay(null)
                    updateState(InternalState.IDLE)
                }

                override fun surfaceCreated(sh: SurfaceHolder) {
                    Log.i(TAG, "surface created")
                    mediaPlayer.setDisplay(holder)
                    updateState(InternalState.ATTACHED)
                }
            })
            playbackContainer.gravity = Gravity.CENTER
            playbackContainer.addView(playbackView)
            (view as ViewGroup).addView(playbackContainer)
        }
        return this
    }

    override val state: State
        get() = getState(internalState)

    private fun getState (internal: InternalState) : State {
        when (internal) {
            InternalState.NONE, InternalState.IDLE -> { return State.NONE }
            InternalState.ATTACHED, InternalState.PREPARED, InternalState.STOPPED -> { return State.IDLE }
            InternalState.STARTED -> { return State.PLAYING }
            InternalState.PAUSED -> { return State.PAUSED }
            InternalState.BUFFERING -> { return State.STALLED }
            InternalState.ERROR -> { return State.ERROR}
            else -> return State.NONE
        }
    }

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
        get() = ( (state != State.NONE) && (state != State.ERROR) )
    override val canPause: Boolean
        get() = ( (state != State.NONE) && (state != State.ERROR) && (type == MediaType.VOD) )
    override val canSeek: Boolean
        get() = ( (state != State.NONE) && (state != State.ERROR) && (type == MediaType.VOD) )
    val canStop: Boolean
        get() = ( (state != State.NONE) && (state != State.ERROR) )


    override fun play(): Boolean {
        if (canPlay) {
            if (state != State.PLAYING) {
                trigger(ClapprEvent.WILL_PLAY.value)
            }
            if (internalState == InternalState.ATTACHED) {
                mediaPlayer.prepareAsync()
            } else {
                mediaPlayer.start()
            }
            if (state == State.PAUSED) {
                updateState(InternalState.STARTED)
            }
            return true
        } else {
            return false
        }
    }

    override fun pause(): Boolean {
        if (canPause) {
            if ( (state == State.PLAYING) || (state == State.STALLED) ) {
                trigger(ClapprEvent.WILL_PAUSE.value)
                mediaPlayer.pause()
            }
            if (state == State.PLAYING) {
                updateState(InternalState.PAUSED)
            }
            return true
        } else {
            return false
        }
    }

    override fun stop(): Boolean {
        try {
            mediaPlayer?.stop()
        } catch (iee: IllegalStateException) {
            Log.i(TAG, "stop", iee)
        }
        return true
    }

    override fun seek(seconds: Int): Boolean {
        // TODO
        return super.seek(seconds)
    }

    private fun updateState(value: InternalState) {
        val previousState = state
        if ( (internalState != InternalState.ERROR) || (value == InternalState.IDLE)) {
            internalState = value
        }
        if (state != previousState) {
            when (state) {
                State.IDLE -> {
                    if (previousState == State.NONE) {
                        trigger(ClapprEvent.READY.value)
                    } else if (previousState == State.PLAYING) {
                        trigger(ClapprEvent.ENDED.value)
                    }
                }
                State.PLAYING -> {
                    trigger(ClapprEvent.PLAYING.value)
                }
                State.PAUSED -> {
                    trigger(ClapprEvent.DID_PAUSE.value)
                }
                State.ERROR -> {
                    trigger(ClapprEvent.ERROR.value)
                }
            }
        }
    }
}