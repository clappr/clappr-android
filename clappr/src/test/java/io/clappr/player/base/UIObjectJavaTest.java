package io.clappr.player.base;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23)
public class UIObjectJavaTest {
    @Before
    public void setup() {
        BaseObject.Companion.setApplicationContext(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void uiObjectCreation() {
        UIObject uo = new UIObject();
        assertTrue("invalid view", uo.getView() != null);
    }
}
