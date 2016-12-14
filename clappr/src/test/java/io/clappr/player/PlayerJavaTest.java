package io.clappr.player;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import java.util.HashMap;
import java.util.LinkedList;

import io.clappr.player.base.BaseObject;
import io.clappr.player.base.Options;
import io.clappr.player.components.Playback;
import io.clappr.player.playback.ExoPlayerPlayback;
import io.clappr.player.plugin.Loader;
import io.clappr.player.plugin.Plugin;
import kotlin.reflect.KClass;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class PlayerJavaTest {
    Player player;

    @Before
    public void setup() {
        Player.initialize(ShadowApplication.getInstance().getApplicationContext());
        player = new Player();
    }

    @Test(expected = IllegalStateException.class)
    public void instatiateWithoutContext() {
        BaseObject.setContext(null);
        Player invalidPlayer = new Player();
    }

    @Test
    public void shouldHaveInvalidStatesBeforeConfigure() {
        assertEquals("valid duration", Double.NaN, player.getDuration(), 0.0);
        assertEquals("valid position", Double.NaN, player.getDuration(), 0.0);

        assertFalse("play enabled", player.play());
        assertFalse("stop enabled", player.stop());
        assertFalse("pause enabled", player.pause());
        assertFalse("seek enabled", player.seek(0));
        assertFalse("load enabled", player.load(""));
    }

    @Test
    public void shouldHaveInvalidStatesWithUnsupportedMedia() {
        player.configure(new Options("", null, false, new LinkedList<KClass<Plugin>>(), new LinkedList<KClass<Playback>>(), new HashMap<String, Object>()));

        assertEquals("valid duration", Double.NaN, player.getDuration(), 0.0);
        assertEquals("valid position", Double.NaN, player.getPosition(), 0.0);

        assertFalse("play enabled", player.play());
        assertFalse("stop enabled", player.stop());
        assertFalse("pause enabled", player.pause());
        assertFalse("seek enabled", player.seek(0));

        assertFalse("load enabled", player.load(""));
    }
}