package com.globo.clappr.base

import com.globo.clappr.BuildConfig
import com.globo.clappr.components.Playback
import com.globo.clappr.plugin.Loader
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23))
open class PlaybackTest {
    @Before
    fun setup() {
        BaseObject.context = ShadowApplication.getInstance().applicationContext
    }

    @Test
    fun shouldLoadPlugins() {
        val playback = Playback(Loader(), Options())

        assertTrue("no plugins", playback.plugins.isNotEmpty())
    }
}