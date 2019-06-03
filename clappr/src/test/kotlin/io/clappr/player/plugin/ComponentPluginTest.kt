package io.clappr.player.plugin

import androidx.test.core.app.ApplicationProvider
import io.clappr.player.base.BaseObject
import io.clappr.player.plugin.container.ContainerPlugin
import io.clappr.player.plugin.container.UIContainerPlugin
import io.clappr.player.plugin.core.CorePlugin
import io.clappr.player.plugin.core.UICorePlugin
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class ComponentPluginTest {
    @Before
    fun setup() {
        BaseObject.applicationContext = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun shouldHaveAName() {
        assertTrue("no name", CorePlugin.name.isNotEmpty())
        assertTrue("no name", UICorePlugin.name.isNotEmpty())
        assertTrue("no name", ContainerPlugin.name.isNotEmpty())
        assertTrue("no name", UIContainerPlugin.name.isNotEmpty())
    }
}