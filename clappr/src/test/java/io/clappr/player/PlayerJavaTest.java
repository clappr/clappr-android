package io.clappr.player;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import io.clappr.player.base.BaseObject;
import io.clappr.player.base.Options;

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

    @Ignore @Test
    public void shouldHaveInvalidStatesWithUnsupportedMedia() {
        player.configure(new Options());

        assertEquals("valid duration", Double.NaN, player.getDuration(), 0.0);
        assertEquals("valid position", Double.NaN, player.getPosition(), 0.0);

        assertFalse("play enabled", player.play());
        assertFalse("stop enabled", player.stop());
        assertFalse("pause enabled", player.pause());
        assertFalse("seek enabled", player.seek(0));

        assertFalse("load enabled", player.load(""));
    }
}
