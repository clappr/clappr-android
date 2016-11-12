package com.globo.clappr.base;

import android.os.Bundle;

import com.globo.clappr.BuildConfig;

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

    static final Callback callback = new Callback() {
        @Override
        public void invoke(@Nullable Bundle bundle) {
            callbackWasCalled = true;
            ++callbackCalls;
        }
    };

    @Before
    public void setup() {
        BaseObject.setContext(null);
        callbackWasCalled = false;
        callbackCalls = 0;
    }

    @Test(expected=IllegalStateException.class)
    public void baseObjectWithoutContext() {
        BaseObject bo = new BaseObject();
    }

    @Test
    public void baseObjectCreation() {
        BaseObject.setContext(ShadowApplication.getInstance().getApplicationContext());
        BaseObject bo = new BaseObject();
        assertNotNull("should not throw exception on creation", bo);
    }

    @Test
    public void baseObjectShouldAllowRegisteringJavaCallbacks() {
        BaseObject.setContext(ShadowApplication.getInstance().getApplicationContext());
        BaseObject bo = new BaseObject();
        String listenId = bo.on(EVENT, callback);
        assertNotNull("listenId should not be null", listenId);
    }

    @Test
    public void baseObjectShouldTriggerEvents() {
        BaseObject.setContext(ShadowApplication.getInstance().getApplicationContext());
        BaseObject bo = new BaseObject();
        bo.on(EVENT, callback);

        bo.trigger(EVENT);
        assertTrue("callback should have been called", callbackWasCalled);
    }

    @Test
    public void baseObjectShouldTriggerOnceCallback() {
        BaseObject.setContext(ShadowApplication.getInstance().getApplicationContext());
        BaseObject bo = new BaseObject();
        bo.once(EVENT, callback);

        bo.trigger(EVENT);
        bo.trigger(EVENT);
        assertTrue("callback should only have been called once", callbackCalls == 1);
    }
}
