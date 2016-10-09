package com.globo.clappr.base

import com.globo.clappr.BuildConfig
import com.globo.clappr.components.Core
import com.globo.clappr.plugin.Loader
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23))
open class CoreTest {
    @Before
    fun setup() {
        BaseObject.context = ShadowApplication.getInstance().applicationContext
    }

    @Test
    fun shouldLoadPlugins() {
        val core = Core(Loader(), Options())

        assertTrue("no plugins", core.plugins.isNotEmpty())
        assertTrue("no containers", core.containers.isNotEmpty())
        assertNotNull("no active container", core.activeContainer)
    }
}