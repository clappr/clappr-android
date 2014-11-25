package com.globo.clappr.player;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.ToggleButton;

import com.globo.clappr.player.playback.Playback;
import com.globo.clappr.player.util.TypefaceManager
import groovy.transform.CompileStatic;

import java.io.IOException;

@CompileStatic
public class Player extends Fragment implements MediaController.MediaPlayerControl {

    public class Dimensions {
        public int stageWidth;

        public int stageHeight;

        public int videoWidth;

        public int videoHeight;
    }

    public class Settings {
        public boolean canSeekBackward;

        public boolean canSeekForward;

        public boolean canPause;
    }

    public class Info {
        public int bufferPercentage;
        public long duration;
    }

    public static final String LOG_TAG = "Clappr";

    private static final long MEDIA_CONTROL_ANIM_DURATION = 400;

    private static final long MEDIA_CONTROL_TIMEOUT = 3000;

    protected View mediaControl;

    protected View controlBar;

    protected Playback playback;

    public final Dimensions dimensions = new Dimensions();

    public final Settings settings = new Settings();

    public final Info info = new Info();

    public Context context;

    private boolean showingMediaControl = false;

    public static Player newInstance() {
        Player player = new Player();
        return player;
    }

    public Player() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        ToggleButton toggleButton = (ToggleButton) view.findViewById(R.id.play_pause);
        toggleButton.setTypeface(TypefaceManager.getInstance().getTypeface("clappr"));
        setClickListeners(view);
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
        TypefaceManager.initialize(activity.getApplicationContext(), R.xml.fonts);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    protected void setClickListeners(View view) {
        controlBar = view.findViewById(R.id.control_bar);
        mediaControl = view.findViewById(R.id.media_control);
    }

    public void toggleMediaControl() {
        showMediaControl(!showingMediaControl);
    }

    public void load(String uri) {
        try {
            playback = Playback.getDefaultInstance(this);
            playback.setVideoUri(uri);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error loading uri " + uri, e);
        }
    }

    public void showMediaControl(boolean show, boolean autoHide) {
        showingMediaControl = show;
        float translation = showingMediaControl ? 0.0f : TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 90.0f, getResources().getDisplayMetrics());
        mediaControl.animate().translationY(translation).setDuration(MEDIA_CONTROL_ANIM_DURATION).start();

        if (autoHide && showingMediaControl) {
            new Handler().postDelayed({ showMediaControl(false) }, MEDIA_CONTROL_TIMEOUT);
        }
    }

    public void showMediaControl(boolean show) {
        showMediaControl(show, false);
    }

    @Override
    public void start() {

    }

    @Override
    public void pause() {

    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return 0;
    }

    @Override
    public void seekTo(int pos) {

    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
