package io.clappr.player.plugin

import io.clappr.player.BuildConfig
import io.clappr.player.base.BaseObject
import io.clappr.player.base.NamedType
import io.clappr.player.base.Options
import io.clappr.player.components.*
import io.clappr.player.playback.NoOpPlayback
import io.clappr.player.plugin.core.CorePlugin
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [23])
class LoaderTest {
    class TestPlugin(core: Core) : CorePlugin(core, name = name) {
        companion object : NamedType {
            override val name = "testplugin"

            val entry = PluginEntry.Core(name = name, factory = { core -> TestPlugin(core) })
        }
    }

    class SameNameTestPlugin(core: Core) : CorePlugin(core, name = name) {
        companion object : NamedType {
            override val name = "testplugin"

            val entry = PluginEntry.Core(name = name, factory = { core -> SameNameTestPlugin(core) })
        }
    }

    class NoNameTestPlugin(core: Core) : CorePlugin(core) {
        companion object {
            val entry = PluginEntry.Core(name = "", factory = { core -> NoNameTestPlugin(core) })
        }
    }

    class TestCorePlugin(core: Core) : CorePlugin(core, name = name) {
        companion object: NamedType {
            override val name = "coreplugin"

            val entry = PluginEntry.Core(name = name, factory = { core -> TestCorePlugin(core) })
        }
    }

    class TestPlaybackAny(source: String, mimeType: String? = null, options: Options): Playback(source, mimeType, options, name, supportsSource) {
        companion object {
            const val name = "testplayback"
            val supportsSource: PlaybackSupportCheck = { _, _ -> true }
            val entry = PlaybackEntry(
                    name = name,
                    supportsSource = supportsSource,
                    factory = { source, mimeType, options -> TestPlaybackAny(source, mimeType, options) })
        }
    }

    class TestPlaybackMp4(source: String, mimeType: String? = null, options: Options): Playback(source, mimeType, options, name, supportsSource) {
        companion object {
            const val name = "testplaybackmp4"
            val supportsSource: PlaybackSupportCheck = { source, _ -> source.toLowerCase().endsWith("mp4") }
            val entry = PlaybackEntry(
                    name = name,
                    supportsSource = supportsSource,
                    factory = { source, mimeType, options -> TestPlaybackMp4(source, mimeType, options) })
        }
    }

    class TestPlaybackDash(source: String, mimeType: String? = null, options: Options): Playback(source, mimeType, options, name, supportsSource) {
        companion object {
            const val name = "testplaybackdash"
            val supportsSource: PlaybackSupportCheck = { source, _ -> source.toLowerCase().endsWith("mpd") }
            val entry = PlaybackEntry(
                    name = name,
                    supportsSource = supportsSource,
                    factory = { source, mimeType, options -> TestPlaybackDash(source, mimeType, options) })
        }
    }

    class TestDuplicatePlayback(source: String, mimeType: String? = null, options: Options): Playback(source, mimeType, options, name, supportsSource) {
        companion object  {
            const val name = "testplayback"
            val supportsSource: PlaybackSupportCheck = { _, _ -> true }
            val entry = PlaybackEntry(
                    name = name,
                    supportsSource = supportsSource,
                    factory = { source, mimeType, options -> TestDuplicatePlayback(source, mimeType, options) })
        }
    }

    @Before
    fun setup() {
        BaseObject.applicationContext = ShadowApplication.getInstance().applicationContext

        Loader.clearPlaybacks()
        Loader.clearPlugins()
    }

    @Test
    fun shouldHaveAnEmptyInitialPluginList() {
        val expectedListSize = 0
        
        val corePlugins = Loader.loadPlugins(Core(Options()))
        val containerPlugins = Loader.loadPlugins(Container(Options()))

        assertEquals(expectedListSize, corePlugins.size)
        assertEquals(expectedListSize, containerPlugins.size)
    }

    @Test
    fun shouldAllowRegisteringPlugins() {
        val expectedLoadedPluginsListSize = 1
        val expectedLoadedPluginName = "coreplugin"

        Loader.register(TestCorePlugin.entry)
        
        val loadedPlugins = Loader.loadPlugins(Core(Options()))

        assertEquals(expectedLoadedPluginsListSize, loadedPlugins.size)
        assertEquals(expectedLoadedPluginName, loadedPlugins[0].name)
    }

    @Test
    fun shouldAllowUnregisteringPlugins() {
        val expectedLoadedPluginsListSize = 0

        Loader.register(TestCorePlugin.entry)
        val didUnregistered = Loader.unregisterPlugin(TestCorePlugin.name)
        
        val loadedPlugins = Loader.loadPlugins(Core(Options()))

        assertTrue("plugin still registered", didUnregistered)
        assertEquals(expectedLoadedPluginsListSize, loadedPlugins.size)
    }

    @Test
    fun shouldNotUnregisterNotRegisteredPlugin() {
        val didUnregistered = Loader.unregisterPlugin(TestPlugin.name)

        assertFalse("plugin should not be unregistered", didUnregistered)
    }

    @Test
    fun shouldAddExternalPlugins() {
        val expectedLoadedPluginsListSize = 1
        val expectedLoadedPluginName = "testplugin"

        val loadedPlugins = Loader.loadPlugins(Core(Options()), listOf<PluginEntry>(TestPlugin.entry))

        assertEquals(expectedLoadedPluginsListSize, loadedPlugins.size)
        assertEquals(expectedLoadedPluginName, loadedPlugins[0].name)
    }

    @Test
    fun shouldDisregardExternalPluginsWithoutName() {
        val expectedLoadedPluginsListSize = 0
        val externalPlugins = listOf<PluginEntry>(NoNameTestPlugin.entry)

        val loadedPlugins = Loader.loadPlugins(Core(Options()), externalPlugins)

        assertEquals(expectedLoadedPluginsListSize, loadedPlugins.size)
    }

    @Test
    fun externalPluginShouldReplaceDefaultPlugin() {
        val expectedLoadedPluginsListSize = 1
        val expectedLoadedPluginName = "coreplugin"

        Loader.register(PluginEntry.Core(name = CorePlugin.name, factory = { context -> CorePlugin(context) }))

        
        val loadedPlugins = Loader.loadPlugins(Core(Options()))

        assertEquals(expectedLoadedPluginsListSize, loadedPlugins.size)
        assertEquals(expectedLoadedPluginName, loadedPlugins[0].name)

        val loadedExternalPlugins = Loader.loadPlugins(Core(Options()), listOf<PluginEntry>(TestCorePlugin.entry))

        assertEquals(expectedLoadedPluginsListSize, loadedExternalPlugins.size)
        assertEquals(expectedLoadedPluginName, loadedExternalPlugins[0].name)
        assertTrue("invalid external plugin", TestCorePlugin::class == loadedExternalPlugins[0]::class)
    }

    @Test
    fun shouldOverwritePluginWithDuplicateNames() {
        val expectedLoadedPluginsListSize = 1
        val expectedLoadedPluginName = "testplugin"

        Loader.register(TestPlugin.entry)
        Loader.register(SameNameTestPlugin.entry)

        
        val loadedPlugins = Loader.loadPlugins(Core(Options()))

        assertEquals(expectedLoadedPluginsListSize, loadedPlugins.size)
        assertEquals(expectedLoadedPluginName, loadedPlugins[0].name)
    }

    @Test
    fun shouldHaveAnEmptyInitialPlaybackList() {
        

        val loadedDashPlayback = Loader.loadPlayback("123.mpd", "video", Options())
        val loadedHLSPlayback = Loader.loadPlayback("123.m3u8", "video", Options())
        val loadedMP4Playback = Loader.loadPlayback("123.mp4", "video", Options())

        assertNull("no playback should be loaded", loadedDashPlayback)
        assertNull("no playback should be loaded", loadedHLSPlayback)
        assertNull("no playback should be loaded", loadedMP4Playback)
    }

    @Test
    fun shouldAllowRegisteringPlaybacks() {
        Loader.register(TestPlaybackMp4.entry)

        
        val loadedMP4Playback = Loader.loadPlayback("123.mp4", "video", Options())

        assertNotNull("mp4 playback should not be empty", loadedMP4Playback)
    }

    @Test
    fun shouldOverwritePlaybacksWithDuplicateNames() {
        Loader.register(TestPlaybackMp4.entry)
        Loader.register(TestDuplicatePlayback.entry)

        
        val loadedPlayback = Loader.loadPlayback("123.mp4", "video", Options())

        assertNotNull("playback should not be empty", loadedPlayback)
        assertEquals(TestDuplicatePlayback::class, loadedPlayback!!::class)
    }

    @Test
    fun shouldInstantiatePlayback() {
        Loader.register(TestPlaybackAny.entry)

        
        val playback = Loader.loadPlayback("some-source.mp4", null, Options())

        assertNotNull("should have loaded playback", playback)
    }

    @Test
    fun shouldInstantiatePlaybackWhichCanPlaySource() {
        Loader.register(TestPlaybackMp4.entry)
        Loader.register(TestPlaybackDash.entry)

        
        var playback = Loader.loadPlayback("some-source.mp4", null, Options())

        assertNotNull("should have loaded playback", playback)
        assertTrue("should load mp4 playback", playback is TestPlaybackMp4)

        playback = Loader.loadPlayback("some-source.mpd", null, Options())

        assertNotNull("should have loaded playback", playback)
        assertTrue("should load dash playback", playback is TestPlaybackDash)
    }

    @Test
    fun shouldInstantiateFirstPlaybackInRegisteredListWhenThereAreMoreThanOneThatCanPlaySource() {
        Loader.register(NoOpPlayback.entry)

        
        var playback = Loader.loadPlayback("some-source.mp4", null, Options())

        assertNotNull("should have loaded playback", playback)
        assertTrue("should load no-op playback", playback is NoOpPlayback)

        Loader.register(TestPlaybackMp4.entry)


        playback = Loader.loadPlayback("some-source.mp4", null, Options())

        assertNotNull("should have loaded playback", playback)
        assertTrue("should load mp4 playback", playback is TestPlaybackMp4)
    }

    @Test
    fun shouldReturnNullForNoPlayback() {
        
        val playback = Loader.loadPlayback("some-source.mp4", null, Options())

        assertNull("should not have loaded playback", playback)
    }
}
