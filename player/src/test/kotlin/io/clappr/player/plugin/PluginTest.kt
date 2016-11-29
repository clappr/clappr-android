package io.clappr.player.plugin

import io.clappr.player.BuildConfig
import io.clappr.player.base.BaseObject
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23))
class PluginTest {
    @Before
    fun setup() {
        BaseObject.context = ShadowApplication.getInstance().applicationContext
    }

    @Test
    fun shouldStartDisabled() {
        class TestPlugin: Plugin(BaseObject())

        val plugin = TestPlugin()
        assertTrue("plugin enabled", plugin.state == Plugin.State.DISABLED)
    }
}