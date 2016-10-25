package com.globo.clappr.plugin

import com.globo.clappr.BuildConfig
import com.globo.clappr.base.BaseObject
import com.globo.clappr.base.NamedType
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
        companion object: NamedType {
            override val name = "testplugin"
        }
    }

    class NoNameTestPlugin : Plugin(BaseObject()) {
    }

    class TestCorePlugin(core: Core) : CorePlugin(core) {
        companion object: NamedType {
            override val name = "coreplugin"
        }
    }

    @Before
    fun setup() {
        BaseObject.context = ShadowApplication.getInstance().applicationContext
        Loader.registeredPlugins.clear()
    }

    @Test
    fun shouldHaveAnEmptyInitialPluginList() {
        val loader = Loader()
        assertTrue("default plugins should be empty", loader.availablePlugins.isEmpty())
    }

    @Test
    fun shouldAllowRegisteringPlugins() {
        Loader.registerPlugin(TestCorePlugin::class)
        val loader = Loader()
        assertTrue("no plugin have been registered", loader.availablePlugins.isNotEmpty())
    }

    @Test
    fun shouldAddExternalPlugins() {
        val loader = Loader()
        val externalPlugins = listOf<KClass<out Plugin>>(TestPlugin::class)
        val loaderExternal = Loader(externalPlugins)
        assertTrue("no external plugin have been added", (loaderExternal.availablePlugins.size - loader.availablePlugins.size) == 1)
    }

    @Test
    fun shouldDisregardExternalPluginsWithoutName() {
        val loader = Loader()
        val externalPlugins = listOf<KClass<out Plugin>>(NoNameTestPlugin::class)
        val loaderExternal = Loader(externalPlugins)
        assertTrue("nameless external plugin added", loaderExternal.availablePlugins.size == loader.availablePlugins.size)
    }

    @Test
    fun externalPluginShouldReplaceDefaultPlugin() {
        Loader.registerPlugin(CorePlugin::class)
        val loader = Loader()
        assertNotNull("no default coreplugin: ${loader.availablePlugins}", loader.availablePlugins["coreplugin"])
        val externalPlugins = listOf<KClass<out Plugin>>(TestCorePlugin::class)
        val loaderExternal = Loader(externalPlugins)
        assertFalse("no external plugin replace", loader.availablePlugins["coreplugin"] == loaderExternal.availablePlugins["coreplugin"])
        assertTrue("invalid external plugin", TestCorePlugin::class == loaderExternal.availablePlugins["coreplugin"])
    }

}