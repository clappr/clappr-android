package com.globo.clappr.base;

import android.content.Context;

import com.globo.clappr.BuildConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import java.util.HashMap;
import java.util.Map;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class BaseObjectJavaTest {
    Context context;

    @Before
    public void setup() {
        context = BaseObject.Companion.getContext();
    }

    @After
    public void tearDown() {
        BaseObject.Companion.setContext(context);
    }

    @Test(expected=kotlin.KotlinNullPointerException.class)
    public void baseObjectWithoutContext() {
        BaseObject bo = new BaseObject();
    }

    @Test
    public void baseObjectCreation() {
        BaseObject.Companion.setContext(ShadowApplication.getInstance().getApplicationContext());
        BaseObject bo = new BaseObject();
    }

}
