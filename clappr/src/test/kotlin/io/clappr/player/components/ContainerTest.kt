package io.clappr.player.base

import io.clappr.player.BuildConfig
import io.clappr.player.PlayerTest
import io.clappr.player.components.Container
import io.clappr.player.components.Playback
import io.clappr.player.components.PlaybackEntry
import io.clappr.player.components.PlaybackSupportInterface
import io.clappr.player.playback.NoOpPlayback
import io.clappr.player.plugin.Loader
import io.clappr.player.plugin.Plugin
import io.clappr.player.plugin.PluginEntry
import io.clappr.player.plugin.container.ContainerPlugin
import io.clappr.player.plugin.container.UIContainerPlugin
import org.junit.Assert.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication
import org.robolectric.shadows.ShadowLog

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [23], shadows = [ShadowLog::class])
open class ContainerTest {

    class MP4Playback(source: String, mimeType: String?, options: Options) : Playback(source, mimeType, options) {
        companion object : PlaybackSupportInterface {
            override fun supportsSource(source: String, mimeType: String?): Boolean {
                return source.endsWith(".mp4")
            }

            override val name: String
                get() = "mp4"
        }
    }

    class TestContainerPlugin(container: Container) : UIContainerPlugin(container) {
        companion object : NamedType {
            override val name: String
                get() = "testContainerPlugin"
        }

        var destroyMethod: (() -> Unit)? = null
        var renderMethod: (() -> Unit)? = null

        override fun destroy() {
            destroyMethod?.invoke()
        }

        override fun render() {
            renderMethod?.invoke()
        }
    }

    @Before
    fun setup() {
        BaseObject.applicationContext = ShadowApplication.getInstance().applicationContext
        Loader.clearPlugins()
        Loader.clearPlaybacks()
    }

    @Test
    fun shouldLoadPlugins() {
        Loader.registerPlugin(PluginEntry.Container(name = ContainerPlugin.name, factory = { context -> ContainerPlugin(context) }))
        val container = Container(Loader(), Options())

        assertTrue("no plugins", container.plugins.isNotEmpty())
    }

    @Test
    fun shouldLoadPlaybackForSupportedSource() {
        Loader.registerPlayback(
                PlaybackEntry(
                        name = MP4Playback.name,
                        supportsSource = { source, mimeType -> MP4Playback.supportsSource(source, mimeType) },
                        factory = { source, mimeType, options -> MP4Playback(source, mimeType, options) }))
        val container = Container(Loader(), Options())
        container.load("some_source.mp4")

        assertNotNull("should have created playback", container.playback)
        assertEquals("should have created mp4 playback", container.playback?.name, MP4Playback.name)
    }

    @Test
    fun shouldNotLoadNoOpPlaybackForUnsupportedSource() {
        Loader.registerPlayback(PlaybackEntry(
                name = NoOpPlayback.name,
                supportsSource = { source, mimeType -> NoOpPlayback.supportsSource(source, mimeType) },
                factory = { source, mimeType, options -> NoOpPlayback(source, mimeType, options) }))
        Loader.registerPlayback(PlaybackEntry(
                name = MP4Playback.name,
                supportsSource = { source, mimeType -> MP4Playback.supportsSource(source, mimeType) },
                factory = { source, mimeType, options -> MP4Playback(source, mimeType, options) }))
        val container = Container(Loader(), Options("some_unknown_source.mp0"))

        assertNull("should not have created playback", container.playback)
        assertNotEquals("should not have created no-op playback", container.playback?.name, NoOpPlayback.name)
    }

    @Test
    fun shouldNotTriggerPlaybackChangedWhenSameNullPlayback() {
        Loader.registerPlayback(PlaybackEntry(
                name = MP4Playback.name,
                supportsSource = { source, mimeType -> MP4Playback.supportsSource(source, mimeType) },
                factory = { source, mimeType, options -> MP4Playback(source, mimeType, options) }))
        val container = Container(Loader(), Options("some_unknown_source.mp0"))

        var callbackWasCalled = false
        container.on(InternalEvent.WILL_CHANGE_PLAYBACK.value, Callback.wrap { callbackWasCalled = true })
        container.on(InternalEvent.DID_CHANGE_PLAYBACK.value, Callback.wrap { callbackWasCalled = true })

        container.load(source = "some_unknown_source.mp0")
        assertFalse("CHANGE_PLAYBACK triggered " + container.playback, callbackWasCalled)
    }

    @Test
    fun shouldTriggerPlaybackChangedWhenDifferentPlayback() {
        Loader.registerPlayback(PlaybackEntry(
                name = MP4Playback.name,
                supportsSource = { source, mimeType -> MP4Playback.supportsSource(source, mimeType) },
                factory = { source, mimeType, options -> MP4Playback(source, mimeType, options) }))
        val container = Container(Loader(), Options("some_unknown_source.mp0"))

        val previousPlayback: Playback? = container.playback
        var callbackWasCalled = false
        container.on(InternalEvent.WILL_CHANGE_PLAYBACK.value, Callback.wrap {
            assertFalse("DID_CHANGE_PLAYBACK triggered before WILL_CHANGE_PLAYBACK", callbackWasCalled)
            assertEquals("playback already changed", previousPlayback, container.playback)
        })
        container.on(InternalEvent.DID_CHANGE_PLAYBACK.value, Callback.wrap { callbackWasCalled = true })

        container.load(source = "some_source.mp4")
        assertTrue("DID_CHANGE_PLAYBACK not triggered", callbackWasCalled)
    }

    @Test
    fun shouldTriggerLoadSourceEventsOnNewSource() {
        Loader.registerPlayback(PlaybackEntry(
                name = MP4Playback.name,
                supportsSource = { source, mimeType -> MP4Playback.supportsSource(source, mimeType) },
                factory = { source, mimeType, options -> MP4Playback(source, mimeType, options) }))
        val container = Container(Loader(), Options("aSource.mp4"))

        var willLoadWasCalled = false
        var didLoadWasCalled = false
        container.on(InternalEvent.WILL_LOAD_SOURCE.value, Callback.wrap { willLoadWasCalled = true })
        container.on(InternalEvent.DID_LOAD_SOURCE.value, Callback.wrap { didLoadWasCalled = true })

        container.load(source = "anotherSource.mp4")
        assertTrue("Will Load Source was not called", willLoadWasCalled)
        assertTrue("Did Load Source was not called", didLoadWasCalled)
    }

    @Test
    fun shouldTriggerLoadSourceEventsOnSameSource() {
        Loader.registerPlayback(PlaybackEntry(
                name = MP4Playback.name,
                supportsSource = { source, mimeType -> MP4Playback.supportsSource(source, mimeType) },
                factory = { source, mimeType, options -> MP4Playback(source, mimeType, options) }))
        val container = Container(Loader(), Options("aSource.mp4"))

        var willLoadWasCalled = false
        var didLoadWasCalled = false
        container.on(InternalEvent.WILL_LOAD_SOURCE.value, Callback.wrap { willLoadWasCalled = true })
        container.on(InternalEvent.DID_LOAD_SOURCE.value, Callback.wrap { didLoadWasCalled = true })

        container.load(source = "aSource.mp4")
        assertTrue("Will Load Source was not called", willLoadWasCalled)
        assertTrue("Did Load Source was not called", didLoadWasCalled)
    }

    @Test
    fun shouldTriggerDidNotLoadSourceEventsWhenNotSupported() {
        Loader.registerPlayback(PlaybackEntry(
                name = MP4Playback.name,
                supportsSource = { source, mimeType -> MP4Playback.supportsSource(source, mimeType) },
                factory = { source, mimeType, options -> MP4Playback(source, mimeType, options) }))
        val container = Container(Loader(), Options("aSource.mp8"))

        var willLoadWasCalled = false
        var didLoadWasCalled = false
        var didNotLoadWasCalled = false

        container.on(InternalEvent.WILL_LOAD_SOURCE.value, Callback.wrap { willLoadWasCalled = true })
        container.on(InternalEvent.DID_LOAD_SOURCE.value, Callback.wrap { didLoadWasCalled = true })
        container.on(InternalEvent.DID_NOT_LOAD_SOURCE.value, Callback.wrap { didNotLoadWasCalled = true })

        container.load(source = "invalid_source.mp8")
        assertTrue("Will Load Source was not called", willLoadWasCalled)
        assertTrue("Did Not Load Source was not called", didNotLoadWasCalled)
        assertFalse("Did Load Source was called", didLoadWasCalled)
    }

    @Test
    fun shouldTriggerDestroyEvents() {
        val container = Container(Loader(), Options())
        val listenObject = BaseObject()

        var willDestroyCalled = false
        var didDestroyCalled = false

        listenObject.listenTo(container, InternalEvent.WILL_DESTROY.value, Callback.wrap { willDestroyCalled = true })
        listenObject.listenTo(container, InternalEvent.DID_DESTROY.value, Callback.wrap { didDestroyCalled = true })

        container.destroy()

        assertTrue("Will Destroy was not called", willDestroyCalled)
        assertTrue("Did Destroy was not called", didDestroyCalled)
    }

    @Test
    @Ignore
    fun shouldDestroyPlaybackOnDestroy() {
        Loader.registerPlayback(PlaybackEntry(
                name = MP4Playback.name,
                supportsSource = { source, mimeType -> MP4Playback.supportsSource(source, mimeType) },
                factory = { source, mimeType, options -> MP4Playback(source, mimeType, options) }))
        val container = Container(Loader(), Options())
        container.load("some_source.mp4")

        assertNotNull("No playback", container.playback)

        var didDestroyCalled = false
        container.listenTo(container.playback!!, InternalEvent.DID_DESTROY.value, Callback.wrap { didDestroyCalled = true })

        container.destroy()

        assertTrue("Playback did destroy not called", didDestroyCalled)
    }

    @Test
    fun shouldClearPlaybackOnDestroy() {
        Loader.registerPlayback(PlaybackEntry(
                name = MP4Playback.name,
                supportsSource = { source, mimeType -> MP4Playback.supportsSource(source, mimeType) },
                factory = { source, mimeType, options -> MP4Playback(source, mimeType, options) }))

        val container = Container(Loader(), Options())
        container.load("some_source.mp4")

        assertNotNull("No playback", container.playback)

        container.destroy()

        assertNull("Valid playback", container.playback)
    }

    @Test
    fun shouldDestroyPluginsOnDestroy() {
        val (container, testPlugin) = setupTestContainerPlugin()

        var pluginDestroyCalled = false
        testPlugin.destroyMethod = { pluginDestroyCalled = true }

        container.destroy()

        assertTrue("Plugin was not destroyed", pluginDestroyCalled)
    }

    @Test
    fun shouldHandlePluginDestroyExceptionOnDestroy() {
        val (container, testPlugin) = setupTestContainerPlugin()

        val expectedLogMessage = "[Container] Plugin ${testPlugin.javaClass.simpleName} " +
                "crashed during destroy"

        testPlugin.destroyMethod = { throw NullPointerException() }

        container.destroy()

        assertEquals(expectedLogMessage, ShadowLog.getLogs()[0].msg)
    }

    @Test
    fun shouldClearPluginsOnDestroy() {
        Loader.registerPlugin(PluginEntry.Container(name = ContainerPlugin.name, factory = { context -> ContainerPlugin(context) }))
        val container = Container(Loader(), Options())

        assertFalse("No plugins", container.plugins.isEmpty())

        container.destroy()

        assertTrue("Plugins not cleared", container.plugins.isEmpty())
    }

    @Test
    fun shouldStoplisteningOnDestroy() {
        val triggerObject = BaseObject()
        val container = Container(Loader(), Options())

        var numberOfTriggers = 0
        container.listenTo(triggerObject, "containerTest", Callback.wrap { numberOfTriggers++ })

        triggerObject.trigger("containerTest")
        assertEquals("no trigger", 1, numberOfTriggers)

        container.destroy()

        triggerObject.trigger("containerTest")
        assertEquals("trigger", 1, numberOfTriggers)
    }

    @Test
    fun shouldSetPlaybackOptionsWhenLoadContainer() {
        Loader.registerPlayback(PlaybackEntry(
                name = MP4Playback.name,
                supportsSource = { source, mimeType -> MP4Playback.supportsSource(source, mimeType) },
                factory = { source, mimeType, options -> MP4Playback(source, mimeType, options) }))
        val source = "some_source.mp4"
        val newOptions = Options()
        newOptions.put(ClapprOption.POSTER.value, "fake-poster-url")

        val container = Container(Loader(), options = newOptions).apply { load(source) }

        assertNotNull(container.options)
        assertEquals(container.options, container.playback?.options)
    }

    @Test
    fun shouldTriggerUpdateOptionOnSetOptions() {
        Loader.registerPlayback(PlaybackEntry(
                name = MP4Playback.name,
                supportsSource = { source, mimeType -> MP4Playback.supportsSource(source, mimeType) },
                factory = { source, mimeType, options -> MP4Playback(source, mimeType, options) }))
        val source = "some_source.mp4"
        val container = Container(Loader(), options = Options()).apply { load(source) }

        var callbackWasCalled = false
        container.on(InternalEvent.DID_UPDATE_OPTIONS.value, Callback.wrap { callbackWasCalled = true })

        container.options = Options(source = "new_source")

        assertTrue("should trigger DID_UPDATE_OPTIONS on set options", callbackWasCalled)
    }

    @Test
    fun shouldRenderPluginsOnRender() {
        val (container, testPlugin) = setupTestContainerPlugin()

        var pluginRenderCalled = false
        testPlugin.renderMethod = { pluginRenderCalled = true }

        container.render()

        assertTrue("Plugin was not rendered", pluginRenderCalled)
    }

    @Test
    fun shouldHandlePluginRenderExceptionOnDestroy() {
        val (container, testPlugin) = setupTestContainerPlugin()

        val expectedLogMessage = "[Container] Plugin ${testPlugin.javaClass.simpleName} " +
                "crashed during render"

        testPlugin.renderMethod = { throw NullPointerException() }

        container.render()

        assertEquals(expectedLogMessage, ShadowLog.getLogs()[0].msg)
    }

    private fun setupTestContainerPlugin(): Pair<Container, TestContainerPlugin> {
        Loader.registerPlugin(PluginEntry.Container(name = TestContainerPlugin.name, factory = { context -> TestContainerPlugin(context) } ))

        val container = Container(Loader(), Options())
        val testPlugin = container.plugins[0] as TestContainerPlugin

        return Pair(container, testPlugin)
    }
}