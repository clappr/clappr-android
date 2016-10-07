package com.globo.clappr.plugin

import com.globo.clappr.BuildConfig
import com.globo.clappr.base.BaseObject
import com.globo.clappr.base.Options
import com.globo.clappr.components.Container
import com.globo.clappr.components.Core
import com.globo.clappr.components.Playback
import com.globo.clappr.plugin.Container.ContainerPlugin
import com.globo.clappr.plugin.Container.UIContainerPlugin
import com.globo.clappr.plugin.Core.CorePlugin
import com.globo.clappr.plugin.Core.UICorePlugin
import com.globo.clappr.plugin.Playback.PlaybackPlugin
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23))
class ComponentPluginTest {
    @Before
    fun setup() {
        BaseObject.context = ShadowApplication.getInstance().applicationContext
    }

    @Test
    fun shouldHaveAName() {
        val testPlugin = arrayOf(CorePlugin(), UICorePlugin(),
                                 ContainerPlugin(), UIContainerPlugin(),
                                 PlaybackPlugin())

        testPlugin.forEach { assertTrue("no name", it.name.isNotEmpty()) }
    }

    @Test
    fun corePluginSetup() {
        val corePlugin = arrayOf(CorePlugin(), UICorePlugin())
        corePlugin.forEach {  it.setup(Core(Loader(), Options())) }
    }

    @Test
    fun containerPluginSetup() {
        val containerPlugin = arrayOf(ContainerPlugin(), UIContainerPlugin())
        containerPlugin.forEach {  it.setup(Container(Loader(), Options())) }
    }

    @Test
    fun playbackPluginSetup() {
        val plabackPlugin = PlaybackPlugin()
        plabackPlugin.setup(Playback(Loader(), Options()))
    }

    @Test(expected = java.lang.ClassCastException::class)
    fun corePluginSetupWithouComponet() {
        val testPlugin = arrayOf(CorePlugin(), UICorePlugin(),
                                 ContainerPlugin(), UIContainerPlugin(),
                                 PlaybackPlugin())
        testPlugin.forEach {  it.setup(BaseObject()) }
    }
}