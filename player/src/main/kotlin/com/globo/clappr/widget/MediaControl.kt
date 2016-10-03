package com.globo.clappr.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

class MediaControl: FrameLayout {
    private var enable = false

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}
}