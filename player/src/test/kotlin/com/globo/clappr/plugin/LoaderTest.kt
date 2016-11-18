package com.globo.clappr.plugin

import com.globo.clappr.BuildConfig
import com.globo.clappr.base.BaseObject
import com.globo.clappr.base.ContainerTest
import com.globo.clappr.base.NamedType
import com.globo.clappr.base.Options
import com.globo.clappr.components.Core
import com.globo.clappr.components.Playback
import com.globo.clappr.components.PlaybackSupportInterface
import com.globo.clappr.playback.NoOpPlayback
import com.globo.clappr.plugin.core.CorePlugin
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

    class TestPlaybackAny(source: String, mimeType: String? = null, options: Options): Playback(source, mimeType, options) {
        companion object: PlaybackSupportInterface  {
            override val name = "testplayback"
            override fun supportsSource(source: String, mimeType: String?): Boolean { return true }
        }
    }

    class TestPlaybackMp4(source: String, mimeType: String? = null, options: Options): Playback(source, mimeType, options) {
        companion object: PlaybackSupportInterface  {
            override val name = "testplayback"
            override fun supportsSource(source: String, mimeType: String?): Boolean { return source.toLowerCase().endsWith("mp4") }
        }
    }

    class TestPlaybackDash(source: String, mimeType: String? = null, options: Options): Playback(source, mimeType, options) {
        companion object: PlaybackSupportInterface  {
            override val name = "testplaybackdash"
            override fun supportsSource(source: String, mimeType: String?): Boolean { return source.toLowerCase().endsWith("mpd") }
        }
    }

    class TestDuplicatePlayback(source: String, mimeType: String? = null, options: Options): Playback(source, mimeType, options) {
        companion object: PlaybackSupportInterface  {
            override val name = "testplayback"
            override fun supportsSource(source: String, mimeType: String?): Boolean { return true }
        }
    }

    @Before
    fun setup() {
        BaseObject.context = ShadowApplication.getInstance().applicationContext
        Loader.clearPlaybacks()
        Loader.clearPlugins()
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

    @Test
    fun shouldHaveAnEmptyInitialPlaybackList() {
        val loader = Loader()
        assertTrue("default playbacks should be empty", loader.availablePlaybacks.isEmpty())
    }

    @Test
    fun shouldAllowRegisteringPlaybacks() {
        Loader.registerPlayback(TestPlaybackMp4::class)
        val loader = Loader()
        assertTrue("default playbacks should not be empty", loader.availablePlaybacks.isNotEmpty())
    }

    @Test
    fun shouldOverwritePlaybacksWithDuplicateNames() {
        Loader.registerPlayback(TestPlaybackMp4::class)
        Loader.registerPlayback(TestDuplicatePlayback::class)
        val loader = Loader()
        assertTrue("should not have duplicate playbacks", loader.availablePlaybacks.size == 1)
    }

    @Test
    fun shouldInstantiatePlayback() {
        Loader.registerPlayback(TestPlaybackAny::class)
        val loader = Loader()
        val playback = loader.loadPlayback("some-source.mp4", null, Options())
        assertNotNull("should have loaded playback", playback)
    }

    @Test
    fun shouldInstantiatePlaybackWhichCanPlaySource() {
        Loader.registerPlayback(TestPlaybackMp4::class)
        Loader.registerPlayback(TestPlaybackDash::class)
        val loader = Loader()
        var playback = loader.loadPlayback("some-source.mp4", null, Options())
        assertNotNull("should have loaded playback", playback)
        assertTrue("should load mp4 playback", playback is TestPlaybackMp4)

        playback = loader.loadPlayback("some-source.mpd", null, Options())
        assertNotNull("should have loaded playback", playback)
        assertTrue("should load dash playback", playback is TestPlaybackDash)
    }

    @Test
    fun shouldInstantiateFirstPlaybackInRegisteredListWhenThereAreMoreThanOneThatCanPlaySource() {
        Loader.registerPlayback(NoOpPlayback::class)
        var loader = Loader()
        var playback = loader.loadPlayback("some-source.mp4", null, Options())
        assertNotNull("should have loaded playback", playback)
        assertTrue("should load no-op playback", playback is NoOpPlayback)

        Loader.registerPlayback(TestPlaybackMp4::class)
        loader = loader
        playback = loader.loadPlayback("some-source.mp4", null, Options())
        assertNotNull("should have loaded playback", playback)
        assertTrue("should load mp4 playback", playback is TestPlaybackMp4)
    }

    @Test
    fun shouldReturnNullForNoPlayback() {
        var loader = Loader()
        var playback = loader.loadPlayback("some-source.mp4", null, Options())
        assertNull("should not have loaded playback", playback)
    }
}
