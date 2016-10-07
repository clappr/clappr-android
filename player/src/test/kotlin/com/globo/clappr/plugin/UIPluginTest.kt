package com.globo.clappr.plugin

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
        class TestPlugin: UIPlugin() {
            override val name = "testplugin"
            override fun setup(context: BaseObject) {}
        }

        val plugin = TestPlugin()
        assertTrue("plugin enabled", plugin.visibility == PluginVisibility.HIDDEN)
    }
}