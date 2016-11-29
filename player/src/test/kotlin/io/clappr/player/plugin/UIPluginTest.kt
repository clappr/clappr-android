package io.clappr.player.plugin

import com.globo.clappr.BuildConfig
import com.globo.clappr.base.BaseObject
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23))
class UIPluginTest {
    @Before
    fun setup() {
        BaseObject.context = ShadowApplication.getInstance().applicationContext
    }

    @Test
    fun shouldStartHidden() {
        class TestPlugin: UIPlugin(BaseObject())

        val plugin = TestPlugin()
        assertTrue("plugin visible", plugin.visibility == UIPlugin.Visibility.HIDDEN)
    }
}