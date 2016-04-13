package com.globo.clappr.base;

import com.globo.clappr.BuildConfig;

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
    @Test(expected=kotlin.TypeCastException.class)
    public void baseObjectWithoutNullOptions() {
        BaseObject bo = new BaseObject(null);
    }

    @Test(expected = kotlin.TypeCastException.class)
    public void baseObjectWithoutContext() {
        Map opt = new HashMap<String, Object>();
        opt.put("opt", ShadowApplication.getInstance().getApplicationContext());
        BaseObject bo = new BaseObject(opt);
    }

    @Test(expected=java.lang.ClassCastException.class)
    public void baseObjectWithInvalidContext() {
        Map opt = new HashMap<String, Object>();
        opt.put("context", 1);
        BaseObject bo = new BaseObject(opt);
    }

    @Test
    public void baseObjectCreation() {
        Map opt = new HashMap<String, Object>();
        opt.put("context", ShadowApplication.getInstance().getApplicationContext());
        BaseObject bo = new BaseObject(opt);
    }

}
