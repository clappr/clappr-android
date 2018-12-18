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
    class TestPlugin(core: Core) : CorePlugin(core) {
        companion object : NamedType {
            override val name = "testplugin"
        }
        override val name: String
            get() = Companion.name
    }

    class SameNameTestPlugin(core: Core) : CorePlugin(core) {
        companion object : NamedType {
            override val name = "testplugin"
        }
        override val name: String
            get() = Companion.name
    }

    class NoNameTestPlugin(core: Core) : CorePlugin(core)

    class TestCorePlugin(core: Core) : CorePlugin(core) {
        companion object: NamedType {
            override val name = "coreplugin"
        }
        override val name: String
            get() = Companion.name
    }

    class TestPlaybackAny(source: String, mimeType: String? = null, options: Options): Playback(source, mimeType, options) {
        companion object: PlaybackSupportInterface  {
            override val name = "testplayback"
            override fun supportsSource(source: String, mimeType: String?): Boolean { return true }
        }
        override fun supportsSource(source: String, mimeType: String?): Boolean = Companion.supportsSource(source, mimeType)
    }

    class TestPlaybackMp4(source: String, mimeType: String? = null, options: Options): Playback(source, mimeType, options) {
        companion object: PlaybackSupportInterface  {
            override val name = "testplayback"
            override fun supportsSource(source: String, mimeType: String?): Boolean { return source.toLowerCase().endsWith("mp4") }
        }
        override fun supportsSource(source: String, mimeType: String?): Boolean = Companion.supportsSource(source, mimeType)
    }

    class TestPlaybackDash(source: String, mimeType: String? = null, options: Options): Playback(source, mimeType, options) {
        companion object: PlaybackSupportInterface  {
            override val name = "testplaybackdash"
            override fun supportsSource(source: String, mimeType: String?): Boolean { return source.toLowerCase().endsWith("mpd") }
        }
        override fun supportsSource(source: String, mimeType: String?): Boolean = Companion.supportsSource(source, mimeType)
    }

    class TestDuplicatePlayback(source: String, mimeType: String? = null, options: Options): Playback(source, mimeType, options) {
        companion object: PlaybackSupportInterface  {
            override val name = "testplayback"
            override fun supportsSource(source: String, mimeType: String?): Boolean { return true }
        }
        override fun supportsSource(source: String, mimeType: String?): Boolean = Companion.supportsSource(source, mimeType)
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

        Loader.registerPlugin(PluginEntry.Core(name = TestCorePlugin.name, factory = { context -> TestCorePlugin(context) }))

        val loader = Loader()
        val loadedPlugins = loader.loadPlugins(Core(loader, Options()))

        assertEquals(expectedLoadedPluginsListSize, loadedPlugins.size)
        assertEquals(expectedLoadedPluginName, loadedPlugins[0].name)
    }

    @Test
    fun shouldAllowUnregisteringPlugins() {
        val expectedLoadedPluginsListSize = 0

        Loader.registerPlugin(PluginEntry.Core(name = TestCorePlugin.name, factory = { context -> TestCorePlugin(context) }))
        val didUnregistered = Loader.unregisterPlugin(TestCorePlugin.name)

        val loader = Loader()
        val loadedPlugins = loader.loadPlugins(Core(loader, Options()))

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

        val loaderExternal = Loader(listOf<PluginEntry>(PluginEntry.Core(name = TestPlugin.name, factory = { context -> TestPlugin(context) })))
        val loadedPlugins = loaderExternal.loadPlugins(Core(loaderExternal, Options()))

        assertEquals(expectedLoadedPluginsListSize, loadedPlugins.size)
        assertEquals(expectedLoadedPluginName, loadedPlugins[0].name)
    }

    @Test
    fun shouldDisregardExternalPluginsWithoutName() {
        val expectedLoadedPluginsListSize = 0
        val externalPlugins = listOf<PluginEntry>(PluginEntry.Core(name = "", factory = { context -> NoNameTestPlugin(context) }))

        val loaderExternal = Loader(externalPlugins)
        val loadedPlugins = loaderExternal.loadPlugins(Core(loaderExternal, Options()))

        assertEquals(expectedLoadedPluginsListSize, loadedPlugins.size)
    }

    @Test
    fun externalPluginShouldReplaceDefaultPlugin() {
        val expectedLoadedPluginsListSize = 1
        val expectedLoadedPluginName = "coreplugin"

        Loader.registerPlugin(PluginEntry.Core(name = CorePlugin.name, factory = { context -> CorePlugin(context) }))

        val loader = Loader()
        val loadedPlugins = loader.loadPlugins(Core(loader, Options()))

        assertEquals(expectedLoadedPluginsListSize, loadedPlugins.size)
        assertEquals(expectedLoadedPluginName, loadedPlugins[0].name)

        val loaderExternal = Loader(listOf<PluginEntry>(PluginEntry.Core(name = TestCorePlugin.name, factory = { context -> TestCorePlugin(context) })))
        val loadedExternalPlugins = loaderExternal.loadPlugins(Core(loaderExternal, Options()))

        assertEquals(expectedLoadedPluginsListSize, loadedExternalPlugins.size)
        assertEquals(expectedLoadedPluginName, loadedExternalPlugins[0].name)
        assertTrue("invalid external plugin", TestCorePlugin::class == loadedExternalPlugins[0]::class)
    }

    @Test
    fun shouldOverwritePluginWithDuplicateNames() {
        val expectedLoadedPluginsListSize = 1
        val expectedLoadedPluginName = "testplugin"

        Loader.registerPlugin(PluginEntry.Core(name = TestPlugin.name, factory = { context -> TestPlugin(context) }))
        Loader.registerPlugin(PluginEntry.Core(name = SameNameTestPlugin.name, factory = { context -> SameNameTestPlugin(context) }))

        val loader = Loader()
        val loadedPlugins = loader.loadPlugins(Core(loader, Options()))

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
        Loader.registerPlayback(PlaybackEntry(
                name = TestPlaybackMp4.name,
                supportsSource = { source, mimeType -> TestPlaybackMp4.supportsSource(source, mimeType) },
                factory = { source, mimeType, options -> TestPlaybackMp4(source, mimeType, options) }))

        val loader = Loader()
        val loadedMP4Playback = loader.loadPlayback("123.mp4", "video", Options())

        assertNotNull("mp4 playback should not be empty", loadedMP4Playback)
    }

    @Test
    fun shouldOverwritePlaybacksWithDuplicateNames() {
        Loader.registerPlayback(PlaybackEntry(
                name = TestPlaybackMp4.name,
                supportsSource = { source, mimeType -> TestPlaybackMp4.supportsSource(source, mimeType) },
                factory = { source, mimeType, options -> TestPlaybackMp4(source, mimeType, options) }))
        Loader.registerPlayback(PlaybackEntry(
                name = TestDuplicatePlayback.name,
                supportsSource = { source, mimeType -> TestDuplicatePlayback.supportsSource(source, mimeType) },
                factory = { source, mimeType, options -> TestDuplicatePlayback(source, mimeType, options) }))

        val loader = Loader()
        val loadedPlayback = loader.loadPlayback("123.mp4", "video", Options())

        assertNotNull("playback should not be empty", loadedPlayback)
        assertEquals(TestDuplicatePlayback::class, loadedPlayback!!::class)
    }

    @Test
    fun shouldInstantiatePlayback() {
        Loader.registerPlayback(PlaybackEntry(
                name = TestPlaybackAny.name,
                supportsSource = { source, mimeType -> TestPlaybackAny.supportsSource(source, mimeType) },
                factory = { source, mimeType, options -> TestPlaybackAny(source, mimeType, options) }))

        val loader = Loader()
        val playback = loader.loadPlayback("some-source.mp4", null, Options())

        assertNotNull("should have loaded playback", playback)
    }

    @Test
    fun shouldInstantiatePlaybackWhichCanPlaySource() {
        Loader.registerPlayback(PlaybackEntry(
                name = TestPlaybackMp4.name,
                supportsSource = { source, mimeType -> TestPlaybackMp4.supportsSource(source, mimeType) },
                factory = { source, mimeType, options -> TestPlaybackMp4(source, mimeType, options) }))
        Loader.registerPlayback(PlaybackEntry(
                name = TestPlaybackDash.name,
                supportsSource = { source, mimeType -> TestPlaybackDash.supportsSource(source, mimeType) },
                factory = { source, mimeType, options -> TestPlaybackDash(source, mimeType, options) }))

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
        Loader.registerPlayback(PlaybackEntry(
                name = NoOpPlayback.name,
                supportsSource = { source, mimeType -> NoOpPlayback.supportsSource(source, mimeType) },
                factory = { source, mimeType, options -> NoOpPlayback(source, mimeType, options) }))

        val loader = Loader()
        var playback = loader.loadPlayback("some-source.mp4", null, Options())

        assertNotNull("should have loaded playback", playback)
        assertTrue("should load no-op playback", playback is NoOpPlayback)

        Loader.registerPlayback(PlaybackEntry(
                name = TestPlaybackMp4.name,
                supportsSource = { source, mimeType -> TestPlaybackMp4.supportsSource(source, mimeType) },
                factory = { source, mimeType, options -> TestPlaybackMp4(source, mimeType, options) }))


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
