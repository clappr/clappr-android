package io.clappr.player.app.plugin.assertPlugin

import android.view.View
import io.clappr.player.plugin.UIPlugin
import kotlin.test.assertEquals

fun assertUiPluginHidden(plugin: UIPlugin) {
    assertEquals(UIPlugin.Visibility.HIDDEN, plugin.visibility)
    assertEquals(View.GONE, plugin.view?.visibility)
}

fun assertUiPluginShown(plugin: UIPlugin) {
    assertEquals(UIPlugin.Visibility.VISIBLE, plugin.visibility)
    assertEquals(View.VISIBLE, plugin.view?.visibility)
}