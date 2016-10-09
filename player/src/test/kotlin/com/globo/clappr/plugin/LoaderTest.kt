package com.globo.clappr.plugin

import com.globo.clappr.BuildConfig
import com.globo.clappr.base.BaseObject
import com.globo.clappr.components.Core
import com.globo.clappr.plugin.Core.CorePlugin
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication
import kotlin.reflect.KClass

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23))
class LoaderTest {
    class TestPlugin : Plugin(BaseObject()) {
        companion object {
            const val name = "TestPlugin"
        }
    }

    class NoNameTestPlugin : Plugin(BaseObject()) {
    }

    class TestCorePlugin(core: Core) : CorePlugin(core) {
        companion object {
            const val name = "coreplugin"
        }
    }

    @Before
    fun setup() {
        BaseObject.context = ShadowApplication.getInstance().applicationContext
    }

    @Test
    fun shouldHaveDefaultPlugins() {
        val loader = Loader()
        assertTrue("no default plugins", loader.availablePlugins.isNotEmpty())
    }

    @Test
    fun shouldAddExternalPlugins() {
        val loader = Loader()
        val externalPlugins = listOf<KClass<out Plugin>>(TestPlugin::class)
        val loaderExternal = Loader(externalPlugins)
        assertTrue("no external plugin", (loaderExternal.availablePlugins.size - loader.availablePlugins.size) == 1)
    }

    @Test
    fun shouldDisconsiderExternalPluginsWithoutName() {
        val loader = Loader()
        val externalPlugins = listOf<KClass<out Plugin>>(NoNameTestPlugin::class)
        val loaderExternal = Loader(externalPlugins)
        assertTrue("no name external plugin added", loaderExternal.availablePlugins.size == loader.availablePlugins.size)
    }

    @Test
    fun externalPluginShouldReplaceDefaultPlugin() {
        val loader = Loader()
        assertNotNull("no default uicoreplugin", loader.availablePlugins.get("coreplugin"))
        val externalPlugins = listOf<KClass<out Plugin>>(TestCorePlugin::class)
        val loaderExternal = Loader(externalPlugins)
        assertFalse("no external plugin replace", loader.availablePlugins.get("coreplugin")!!.equals(loaderExternal.availablePlugins.get("coreplugin")))
        assertTrue("invalid external plugin", TestCorePlugin::class.equals(loaderExternal.availablePlugins.get("coreplugin")))
    }

}