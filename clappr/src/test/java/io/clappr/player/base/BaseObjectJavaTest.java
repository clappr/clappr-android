package io.clappr.player.base;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import io.clappr.player.interop.Callback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23)
public class BaseObjectJavaTest {
    private static final String EVENT = "someevent";

    private static boolean callbackWasCalled = false;

    private static int callbackCalls = 0;

    private static final Callback callback = bundle -> {
        callbackWasCalled = true;
        ++callbackCalls;
        return null;
    };

    @Before
    public void setup() {
        BaseObject.setApplicationContext(ApplicationProvider.getApplicationContext());
        callbackWasCalled = false;
        callbackCalls = 0;
    }

    @Test
    public void baseObjectCreation() {
        BaseObject.setApplicationContext(ApplicationProvider.getApplicationContext());
        BaseObject bo = new BaseObject();
        assertNotNull("should not throw exception on creation", bo);
    }

    @Test
    public void baseObjectShouldAllowRegisteringJavaCallbacks() {
        BaseObject.setApplicationContext(ApplicationProvider.getApplicationContext());
        BaseObject bo = new BaseObject();
        String listenId = bo.on(EVENT, callback);
        assertNotNull("listenId should not be null", listenId);
    }

    @Test
    public void baseObjectShouldTriggerEvents() {
        BaseObject.setApplicationContext(ApplicationProvider.getApplicationContext());
        BaseObject bo = new BaseObject();
        bo.on(EVENT, callback);

        bo.trigger(EVENT);
        assertTrue("callback should have been called", callbackWasCalled);
    }

    @Test
    public void baseObjectShouldTriggerOnceCallback() {
        int expectedCallbackCalls = 1;

        BaseObject.setApplicationContext(ApplicationProvider.getApplicationContext());
        BaseObject bo = new BaseObject();
        bo.once(EVENT, callback);

        bo.trigger(EVENT);
        bo.trigger(EVENT);
        assertEquals("callback should only have been called once",
                expectedCallbackCalls, callbackCalls);
    }
}
