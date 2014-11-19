package com.globo.clappr.player.playback;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.globo.clappr.player.Player;

import java.io.IOException;

class DefaultPlayback extends Playback implements android.media.MediaPlayer.OnBufferingUpdateListener,
        android.media.MediaPlayer.OnCompletionListener, android.media.MediaPlayer.OnErrorListener,
        android.media.MediaPlayer.OnPreparedListener, android.media.MediaPlayer.OnVideoSizeChangedListener,
        android.media.MediaPlayer.OnInfoListener {

    @SuppressLint("ViewConstructor")
    class PlayerView extends SurfaceView {

        int surfaceHeight;

        int surfaceWidth;

        public PlayerView(Context context) {
            super(context);
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            if (isInPlaybackState()) {
                player.toggleMediaControl();
            }
            return false;
        }

        @Override
        public boolean onTrackballEvent(MotionEvent ev) {
            if (isInPlaybackState()) {
                player.toggleMediaControl();
            }
            return false;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = getDefaultSize(player.dimensions.videoWidth, widthMeasureSpec);
            int height = getDefaultSize(player.dimensions.videoHeight, heightMeasureSpec);
            Log.d(Player.LOG_TAG, "onMeasure: width=" + width + ", height=" + height);
            if (player.dimensions.videoWidth > 0 && player.dimensions.videoHeight > 0) {
                if (player.dimensions.videoWidth * height > width * player.dimensions.videoHeight) {
                    Log.d(Player.LOG_TAG, "image too tall, correcting");
                    height = width * player.dimensions.videoHeight / player.dimensions.videoWidth;
                } else if (player.dimensions.videoWidth * height < width * player.dimensions.videoHeight) {
                    Log.d(Player.LOG_TAG, "image too wide, correcting");
                    width = height * player.dimensions.videoWidth / player.dimensions.videoHeight;
                } else {
                    Log.d(Player.LOG_TAG, "aspect ratio is correct: " +
                            width + "/" + height + "=" +
                            player.dimensions.videoWidth + "/" + player.dimensions.videoHeight);
                }
            }
            Log.d(Player.LOG_TAG, "setting size to " + width + 'x' + height);
            player.dimensions.stageWidth = width;
            player.dimensions.stageHeight = height;
            setMeasuredDimension(width, height);
        }

    }

    static final int UNSUPPORTED_MEDIA_ENCODING = Integer.MIN_VALUE;

    private android.media.MediaPlayer mediaPlayerImpl;

    private int seekPositionWhenPrepared;

    private Handler seekHandler;

    private SurfaceHolder.Callback surfaceHolderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
        {
            Log.d(Player.LOG_TAG, "surfaceChanged: width=" + width + ", height=" + height);
            view.surfaceWidth = width;
            view.surfaceHeight = height;
            boolean isValidState = (targetState == State.PLAYING);
            boolean hasValidSize = (player.dimensions.videoWidth == width && player.dimensions.videoHeight == height);
            if (isValidState && hasValidSize)
                start();
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder)
        {
            surfaceHolder = holder;
            view.surfaceWidth = holder.getSurfaceFrame().width();
            view.surfaceHeight = holder.getSurfaceFrame().height();
            Log.d(Player.LOG_TAG, "surfaceCreated: width=" + view.surfaceWidth + ", height=" + view.surfaceHeight);
            openVideo();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder)
        {
            view.surfaceWidth = view.surfaceHeight = 0;
            surfaceHolder = null;
            player.showMediaControl(false);
        }
    };

    private Uri uri;

    private PlayerView view;

    private SurfaceHolder surfaceHolder;

    DefaultPlayback(Player player) {
        super(player);
    }

    @Override
    public boolean canPause() {
        return player.settings.canPause;
    }

    @Override
    public boolean canSeekBackward() {
        return player.settings.canSeekBackward;
    }

    @Override
    public boolean canSeekForward() {
        return player.settings.canSeekForward;
    }

    @Override
    public int getAudioSessionId() {
        if (mediaPlayerImpl != null)
            return mediaPlayerImpl.getAudioSessionId();

        return 0;
    }

    @Override
    public int getBufferPercentage() {
        return player.info.bufferPercentage;
    }

    @Override
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            return mediaPlayerImpl.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public int getDuration() {
        if (mediaPlayerImpl != null && isInPlaybackState())
            return mediaPlayerImpl.getDuration();
        return 0;
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public boolean isPlaying() {
        return (mediaPlayerImpl != null && mediaPlayerImpl.isPlaying()) || currentState == State.PLAYING;
    }

    @Override
    public void onBufferingUpdate(android.media.MediaPlayer mp, int percent) {
        Log.d(Player.LOG_TAG, "DefaultPlayback.onBufferingUpdate()");
        player.info.bufferPercentage = percent;
    }

    @Override
    public void onCompletion(android.media.MediaPlayer mp) {
        Log.d(Player.LOG_TAG, "DefaultPlayback.onCompletion()");
//        player.handleCompletion();
    }

    @Override
    public boolean onError(android.media.MediaPlayer mp, int what, int extra) {
        Log.d(Player.LOG_TAG, "DefaultPlayback.onError(" + mp + ", " + what + ", " + extra + ")");
//        player.releaseMediaPlayer();

        if (what == android.media.MediaPlayer.MEDIA_ERROR_UNKNOWN) {
            switch (extra) {
                case android.media.MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                case android.media.MediaPlayer.MEDIA_ERROR_MALFORMED:
                case android.media.MediaPlayer.MEDIA_ERROR_IO:
                    break;
                case android.media.MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                case UNSUPPORTED_MEDIA_ENCODING:
//                    player.handleRecoverablePlaybackError(Error.UNSUPPORTED_MEDIA);
                    return true;
                default:
//                    player.handleRecoverablePlaybackError(Error.PLAYBACK_FAILED);
                    return true;
            }
        } else if (what == android.media.MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
//            player.handleRecoverablePlaybackError(Error.PLAYBACK_FAILED);
            return true;
        }

        currentState = State.ERROR;
        targetState = State.ERROR;

//        Error error = Error.PLAYBACK_FAILED;
        if (what == android.media.MediaPlayer.MEDIA_ERROR_UNKNOWN) {
            switch (extra) {
                case android.media.MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                case UNSUPPORTED_MEDIA_ENCODING:
//                    error = Error.UNSUPPORTED_MEDIA;
                    break;
                default:
//                    error = Error.PLAYBACK_FAILED;
                    break;
            }
        }

//        return player.handleError(error, null);
        return true;
    }

    @Override
    public void onPrepared(android.media.MediaPlayer mp) {
        Log.d(Player.LOG_TAG, "DefaultPlayback.onPrepared()");
        currentState = State.PREPARED;

        // No metadata is available to know the player capabilities
        player.settings.canPause = player.settings.canSeekBackward = player.settings.canSeekForward = true;

//        if (player.mediaController != null) {
//            player.mediaController.setEnabled(true);
//        }
        player.dimensions.videoWidth = mp.getVideoWidth();
        player.dimensions.videoHeight = mp.getVideoHeight();

        if (player.dimensions.videoWidth != 0 && player.dimensions.videoHeight != 0) {
            if (view.surfaceWidth == player.dimensions.videoWidth && view.surfaceHeight == player.dimensions.videoHeight) {
                if (targetState == State.PLAYING) {
                    start();
                    player.showMediaControl(true);
                } else if (!isPlaying() &&
                        (getCurrentPosition() > 0)) {
                    player.showMediaControl(true, true);
                }
            } else {
                if (targetState == State.PLAYING) {
                    start();
                }
            }
        } else {
            if (targetState == State.PLAYING) {
                start();
            }
            player.showMediaControl(true);
        }
    }

    @Override
    public boolean onInfo(android.media.MediaPlayer mp, int what, int extra) {
        if (what == android.media.MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            Log.d(Player.LOG_TAG, "DefaultPlayback.onInfo(): MEDIA_INFO_VIDEO_RENDERING_START");
            if (seekPositionWhenPrepared > 0) {
                seekTo(seekPositionWhenPrepared);
            }
        }
        return false;
    }

    @Override
    public void onVideoSizeChanged(android.media.MediaPlayer mp, int width, int height) {
        Log.d(Player.LOG_TAG, "DefaultPlayback.onVideoSizeChanged()");
        player.dimensions.videoWidth = width;
        player.dimensions.videoHeight = height;
        if (width != 0 && height != 0) {
            view.getHolder().setFixedSize(width, height);
            view.requestLayout();
        }
    }

    @Override
    public void pause() {
        if (isInPlaybackState()) {
            mediaPlayerImpl.pause();
            currentState = State.PAUSED;
        }
        targetState = State.PAUSED;
//        player.handlePause();
    }

    @Override
    public void prepare() throws IllegalStateException, IOException {
        mediaPlayerImpl.prepare();
    }

    @Override
    public void prepareAsync() {
        mediaPlayerImpl.prepareAsync();
    }

    @Override
    public void release() {
        if (mediaPlayerImpl != null) {
            mediaPlayerImpl.release();
            mediaPlayerImpl = null;
            currentState = State.IDLE;
        }
    }

    @Override
    public void reset() {
        super.reset();
        if (mediaPlayerImpl != null) {
            mediaPlayerImpl.reset();
            mediaPlayerImpl = null;
            currentState = State.IDLE;
        }
    }

    @Override
    public void seekTo(int pos) {
        if (mediaPlayerImpl != null && isInPlaybackState()) {
            Log.d(Player.LOG_TAG, "DefaultPlayback.seekTo(): " + pos);
            mediaPlayerImpl.seekTo(pos);
            if (seekPositionWhenPrepared > 0) {
                seekPositionWhenPrepared = 0;
                // Using a delayed command instead of onSeekComplete because of: http://code.google.com/p/android/issues/detail?id=55136
                seekHandler = new Handler();
                seekHandler.postDelayed(() -> mediaPlayerImpl.setVolume(1, 1), 1000);
            }
        } else {
            Log.d(Player.LOG_TAG, "DefaultPlayback.seekTo(): seekPositionWhenPrepared=" + pos);
            seekPositionWhenPrepared = pos;
        }
    }

    @Override
    public void setAudioStreamType(int streamType) {
        mediaPlayerImpl.setAudioStreamType(streamType);
    }

    @Override
    public void setDisplay(SurfaceHolder holder) {
        mediaPlayerImpl.setDisplay(holder);
    }

    @Override
    public void setScreenOnWhilePlaying(boolean screenOn) {
        mediaPlayerImpl.setScreenOnWhilePlaying(screenOn);
    }

    @Override
    public void setVideoUri(String uri) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {
        this.uri = Uri.parse(uri);
        seekPositionWhenPrepared = 0;
        openVideo();
        if (view == null) {
            init();
        }
        view.requestLayout();
        view.invalidate();
    }

    @Override
    public void start() {
//        if (isInLoadingPendingState()) {
//            player.handleLoading();
//        }
        if (isInPlaybackState()) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB && currentState == State.PLAYBACK_COMPLETED) {
                release(false);
                openVideo();
            } else {
                currentState = State.PLAYING;
                mediaPlayerImpl.start();
                if (seekPositionWhenPrepared > 0) {
                    mediaPlayerImpl.setVolume(0, 0);
                } else {
//                    player.handlePlay();
                }
            }
        }
        targetState = State.PLAYING;
    }

    @Override
    public void stop() {
        mediaPlayerImpl.seekTo(0);
        mediaPlayerImpl.pause();
//        player.handleStopped();
        currentState = State.IDLE;
    }

    @SuppressWarnings("deprecation")
    private void init() {
        view = new PlayerView(player.context);
        player.dimensions.videoWidth = player.dimensions.videoHeight = 0;
        currentState = State.IDLE;
        targetState = State.IDLE;
        view.getHolder().setSizeFromLayout();
        view.getHolder().addCallback(surfaceHolderCallback);
        view.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.surfaceWidth = view.getHolder().getSurfaceFrame().width();
        view.surfaceHeight = view.getHolder().getSurfaceFrame().height();
    }

    private void openVideo() {
        if (uri == null || surfaceHolder == null) {
            return;
        }

        pauseMusicPlayback();

        // we shouldn't clear the target state, because somebody might have
        // called start() previously
        release(false);
        try {
            player.info.duration = -1;
            player.info.bufferPercentage = 0;
            setupMediaPlayer();
            // we don't set the target state here either, but preserve the
            // target state that was there before.
            currentState = State.PREPARING;
            mediaPlayerImpl.prepareAsync();
        } catch (Exception ex) {
            Log.w(Player.LOG_TAG, "Unable to open content on " + uri, ex);
            currentState = State.ERROR;
            targetState = State.ERROR;
            onError(null, android.media.MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
        }
    }

    private void pauseMusicPlayback() {
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        player.getActivity().sendBroadcast(i);
    }

    private void release(boolean clearTargetState) {
        if (mediaPlayerImpl != null) {
            mediaPlayerImpl.reset();
            mediaPlayerImpl.release();
            mediaPlayerImpl = null;
            currentState = State.IDLE;
            if (clearTargetState) {
                targetState = State.IDLE;
            }
            player.showMediaControl(false);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setupMediaPlayer() throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {
        mediaPlayerImpl = new android.media.MediaPlayer();
        mediaPlayerImpl.setOnInfoListener(this);
        mediaPlayerImpl.setOnPreparedListener(this);
        mediaPlayerImpl.setOnVideoSizeChangedListener(this);
        mediaPlayerImpl.setOnCompletionListener(this);
        mediaPlayerImpl.setOnErrorListener(this);
        mediaPlayerImpl.setOnBufferingUpdateListener(this);
        mediaPlayerImpl.setDataSource(player.getActivity(), uri);
        mediaPlayerImpl.setDisplay(view.getHolder());
        mediaPlayerImpl.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayerImpl.setScreenOnWhilePlaying(true);
    }
}
