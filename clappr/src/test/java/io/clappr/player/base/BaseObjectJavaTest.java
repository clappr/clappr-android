package io.clappr.player.base;

import android.os.Bundle;

import io.clappr.player.BuildConfig;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class BaseObjectJavaTest {
    static final String EVENT = "someevent";

    static boolean callbackWasCalled = false;

    static int callbackCalls = 0;

    static final Function1<Bundle, Unit> callback = new Function1<Bundle, Unit>() {
        @Override
        public Unit invoke(@Nullable Bundle bundle) {
            callbackWasCalled = true;
            ++callbackCalls;
            return null;
        }
    };

    @Before
    public void setup() {
        BaseObject.setApplicationContext(null);
        callbackWasCalled = false;
        callbackCalls = 0;
    }

    @Test
    public void baseObjectCreation() {
        BaseObject.setApplicationContext(ShadowApplication.getInstance().getApplicationContext());
        BaseObject bo = new BaseObject();
        assertNotNull("should not throw exception on creation", bo);
    }

    @Test
    public void baseObjectShouldAllowRegisteringJavaCallbacks() {
        BaseObject.setApplicationContext(ShadowApplication.getInstance().getApplicationContext());
        BaseObject bo = new BaseObject();
        String listenId = bo.on(EVENT, callback);
        assertNotNull("listenId should not be null", listenId);
    }

    @Test
    public void baseObjectShouldTriggerEvents() {
        BaseObject.setApplicationContext(ShadowApplication.getInstance().getApplicationContext());
        BaseObject bo = new BaseObject();
        bo.on(EVENT, callback);

        bo.trigger(EVENT);
        assertTrue("callback should have been called", callbackWasCalled);
    }

    @Test
    public void baseObjectShouldTriggerOnceCallback() {
        BaseObject.setApplicationContext(ShadowApplication.getInstance().getApplicationContext());
        BaseObject bo = new BaseObject();
        bo.once(EVENT, callback);

        bo.trigger(EVENT);
        bo.trigger(EVENT);
        assertTrue("callback should only have been called once", callbackCalls == 1);
    }
}
