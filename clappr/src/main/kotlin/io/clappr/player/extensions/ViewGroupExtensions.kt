package io.clappr.player.extensions

import android.view.ViewGroup

fun ViewGroup.disableAllViews() {
    var index = 0
    while (index < childCount) {
        val view = getChildAt(index++)
        view.isEnabled = false

        if (view is ViewGroup)
            view.disableAllViews()
    }
}

fun ViewGroup.enableAllViews() {
    var index = 0
    while (index < childCount) {
        val view = getChildAt(index++)
        view.isEnabled = true

        if (view is ViewGroup)
            view.enableAllViews()
    }
}



