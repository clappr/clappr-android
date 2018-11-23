package io.clappr.player.app.plugin.util

import android.view.View
import io.clappr.player.plugin.UIPlugin
import kotlin.test.assertEquals

fun assertHidden(plugin: UIPlugin) {
    assertEquals(UIPlugin.Visibility.HIDDEN, plugin.visibility)
    assertEquals(View.GONE, plugin.view?.visibility)
}

fun assertShown(plugin: UIPlugin) {
    assertEquals(UIPlugin.Visibility.VISIBLE, plugin.visibility)
    assertEquals(View.VISIBLE, plugin.view?.visibility)
}