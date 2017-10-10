package io.clappr.player.extensions.context

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration


fun Context.isRunningInAndroidTvDevice(): Boolean {
    val uiModeManager = this.getSystemService(Context.UI_MODE_SERVICE) as? UiModeManager
    return ((uiModeManager?.currentModeType ?: 0) and Configuration.UI_MODE_TYPE_TELEVISION) == Configuration.UI_MODE_TYPE_TELEVISION
}