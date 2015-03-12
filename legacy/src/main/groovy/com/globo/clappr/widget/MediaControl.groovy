package com.globo.clappr.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout
import groovy.transform.CompileStatic;

@CompileStatic
public class MediaControl extends FrameLayout {

    private boolean enabled;

    public MediaControl(Context context) {
        super(context);
    }

    public MediaControl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MediaControl(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
