package io.clappr.player.plugin

import io.clappr.player.BuildConfig
import io.clappr.player.base.BaseObject
import io.clappr.player.base.NamedType
import io.clappr.player.base.Options
import io.clappr.player.components.Container
import io.clappr.player.components.Core
import io.clappr.player.components.Playback
import io.clappr.player.components.PlaybackSupportInterface
import io.clappr.player.playback.NoOpPlayback
import io.clappr.player.plugin.core.CorePlugin
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication
import kotlin.reflect.KClass

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [23])
class LoaderTest {
    class TestPlugin(baseObject: BaseObject) : Plugin(baseObject) {
        companion object: NamedType {
            override val name = "testplugin"
        }
    }

    class SameNameTestPlugin(baseObject: BaseObject) : Plugin(baseObject) {
        companion object: NamedType {
            override val name = "testplugin"
        }
    }

    class NoNameTestPlugin(baseObject: BaseObject) : Plugin(baseObject)

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
        val expectedListSize = 0

        val loader = Loader()
        val corePlugins = loader.loadPlugins(Core(loader, Options()))
        val containerPlugins = loader.loadPlugins(Container(loader, Options()))

        assertEquals(expectedListSize, corePlugins.size)
        assertEquals(expectedListSize, containerPlugins.size)
    }

    @Test
    fun shouldAllowRegisteringPlugins() {
        val expectedLoadedPluginsListSize = 1
        val expectedLoadedPluginName = "coreplugin"

        Loader.registerPlugin(TestCorePlugin::class)

        val loader = Loader()
        val loadedPlugins = loader.loadPlugins(Core(loader, Options()))

        assertEquals(expectedLoadedPluginsListSize, loadedPlugins.size)
        assertEquals(expectedLoadedPluginName, loadedPlugins[0].name)
    }

    @Test
    fun shouldAllowUnregisteringPlugins() {
        val expectedLoadedPluginsListSize = 0

        Loader.registerPlugin(TestCorePlugin::class)
        val didUnregistered = Loader.unregisterPlugin(TestCorePlugin::class)

        val loader = Loader()
        val loadedPlugins = loader.loadPlugins(Core(loader, Options()))

        assertTrue("plugin still registered", didUnregistered)
        assertEquals(expectedLoadedPluginsListSize, loadedPlugins.size)
    }

    @Test
    fun shouldNotUnregisterNotRegisteredPlugin() {
        val didUnregistered = Loader.unregisterPlugin(TestPlugin::class)

        assertFalse("plugin should not be unregistered", didUnregistered)
    }

    @Test
    fun shouldAddExternalPlugins() {
        val expectedLoadedPluginsListSize = 1
        val expectedLoadedPluginName = "testplugin"

        val loaderExternal = Loader(listOf<KClass<out Plugin>>(TestPlugin::class))
        val loadedPlugins = loaderExternal.loadPlugins(BaseObject())

        assertEquals(expectedLoadedPluginsListSize, loadedPlugins.size)
        assertEquals(expectedLoadedPluginName, loadedPlugins[0].name)
    }

    @Test
    fun shouldDisregardExternalPluginsWithoutName() {
        val expectedLoadedPluginsListSize = 0
        val externalPlugins = listOf<KClass<out Plugin>>(NoNameTestPlugin::class)

        val loaderExternal = Loader(externalPlugins)
        val loadedPlugins = loaderExternal.loadPlugins(BaseObject())

        assertEquals(expectedLoadedPluginsListSize, loadedPlugins.size)
    }

    @Test
    fun externalPluginShouldReplaceDefaultPlugin() {
        val expectedLoadedPluginsListSize = 1
        val expectedLoadedPluginName = "coreplugin"

        Loader.registerPlugin(CorePlugin::class)

        val loader = Loader()
        val loadedPlugins = loader.loadPlugins(Core(loader, Options()))

        assertEquals(expectedLoadedPluginsListSize, loadedPlugins.size)
        assertEquals(expectedLoadedPluginName, loadedPlugins[0].name)

        val loaderExternal = Loader(listOf<KClass<out Plugin>>(TestCorePlugin::class))
        val loadedExternalPlugins = loaderExternal.loadPlugins(Core(loaderExternal, Options()))

        assertEquals(expectedLoadedPluginsListSize, loadedExternalPlugins.size)
        assertEquals(expectedLoadedPluginName, loadedExternalPlugins[0].name)
        assertTrue("invalid external plugin", TestCorePlugin::class == loadedExternalPlugins[0]::class)
    }

    @Test
    fun shouldOverwritePluginWithDuplicateNames() {
        val expectedLoadedPluginsListSize = 1
        val expectedLoadedPluginName = "testplugin"

        Loader.registerPlugin(TestPlugin::class)
        Loader.registerPlugin(SameNameTestPlugin::class)

        val loader = Loader()
        val loadedPlugins = loader.loadPlugins(BaseObject())

        assertEquals(expectedLoadedPluginsListSize, loadedPlugins.size)
        assertEquals(expectedLoadedPluginName, loadedPlugins[0].name)
    }

    @Test
    fun shouldHaveAnEmptyInitialPlaybackList() {
        val loader = Loader()

        val loadedDashPlayback = loader.loadPlayback("123.mpd", "video", Options())
        val loadedHLSPlayback = loader.loadPlayback("123.m3u8", "video", Options())
        val loadedMP4Playback = loader.loadPlayback("123.mp4", "video", Options())

        assertNull("no playback should be loaded", loadedDashPlayback)
        assertNull("no playback should be loaded", loadedHLSPlayback)
        assertNull("no playback should be loaded", loadedMP4Playback)
    }

    @Test
    fun shouldAllowRegisteringPlaybacks() {
        Loader.registerPlayback(TestPlaybackMp4::class)

        val loader = Loader()
        val loadedMP4Playback = loader.loadPlayback("123.mp4", "video", Options())

        assertNotNull("mp4 playback should not be empty", loadedMP4Playback)
    }

    @Test
    fun shouldOverwritePlaybacksWithDuplicateNames() {
        Loader.registerPlayback(TestPlaybackMp4::class)
        Loader.registerPlayback(TestDuplicatePlayback::class)

        val loader = Loader()
        val loadedPlayback = loader.loadPlayback("123.mp4", "video", Options())

        assertNotNull("playback should not be empty", loadedPlayback)
        assertEquals(TestDuplicatePlayback::class, loadedPlayback!!::class)
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

        val loader = Loader()
        var playback = loader.loadPlayback("some-source.mp4", null, Options())

        assertNotNull("should have loaded playback", playback)
        assertTrue("should load no-op playback", playback is NoOpPlayback)

        Loader.registerPlayback(TestPlaybackMp4::class)

        playback = loader.loadPlayback("some-source.mp4", null, Options())

        assertNotNull("should have loaded playback", playback)
        assertTrue("should load mp4 playback", playback is TestPlaybackMp4)
    }

    @Test
    fun shouldReturnNullForNoPlayback() {
        val loader = Loader()
        val playback = loader.loadPlayback("some-source.mp4", null, Options())

        assertNull("should not have loaded playback", playback)
    }
}
