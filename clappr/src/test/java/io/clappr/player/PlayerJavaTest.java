package io.clappr.player;

import android.annotation.SuppressLint;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import io.clappr.player.base.Options;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23)
public class PlayerJavaTest {
    private Player player;

    @Before
    public void setup() {
        Player.initialize(ApplicationProvider.getApplicationContext());
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

    @SuppressLint("IgnoreWithoutReason") @Ignore @Test
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
