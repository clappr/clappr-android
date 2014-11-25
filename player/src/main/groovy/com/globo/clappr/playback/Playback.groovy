package com.globo.clappr.playback

import android.view.SurfaceHolder;
import android.view.View;
import android.widget.MediaController;

import com.globo.clappr.Player
import groovy.transform.CompileStatic

@CompileStatic
public abstract class Playback implements MediaController.MediaPlayerControl {

    protected static enum State {
        ERROR,
        IDLE,
        PAUSED,
        PLAYBACK_COMPLETED,
        PLAYING,
        PREPARED,
        PREPARING
    }

    protected Player player;

    protected State currentState;
    protected State targetState;

    Playback(Player player) {
        this.player = player;
        currentState = targetState = State.IDLE;
    }

    public abstract View getView();

    public boolean hasAlternativeAudioTracks() {
        return false;
    }

    public List<String> getAudioTracks() {
        return Collections.emptyList();
    }

    public String getSelectedAudioTrack() {
        return null;
    }

    public boolean selectAudioTrack(String audioTrack) {
        return false;
    }

    public abstract void prepare() throws IllegalStateException, IOException;

    public abstract void prepareAsync();

    public abstract void release();

    public void reset() {
    }

    public abstract void setAudioStreamType(int audioStreamType);

    public abstract void setVideoUri(String uri) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException;

    public abstract void setDisplay(SurfaceHolder holder);

    public abstract void setScreenOnWhilePlaying(boolean b);

    public abstract void stop();

    public boolean isInLoadingPendingState() {
        return currentState == State.IDLE || currentState == State.PREPARING;
    }

    public boolean isInPlaybackState() {
        return currentState != State.ERROR && currentState != State.IDLE && currentState != State.PREPARING;
    }

    public static Playback getDefaultInstance(Player player) {
        return new DefaultPlayback(player);
    }
}
