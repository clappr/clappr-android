package io.clappr.player.components

import android.annotation.SuppressLint
import androidx.test.core.app.ApplicationProvider
import io.clappr.player.base.*
import io.clappr.player.playback.NoOpPlayback
import io.clappr.player.plugin.Loader
import io.clappr.player.plugin.PluginEntry
import io.clappr.player.plugin.UIPlugin
import io.clappr.player.plugin.container.ContainerPlugin
import io.clappr.player.plugin.container.UIContainerPlugin
import org.junit.Assert.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23], shadows = [ShadowLog::class])
open class ContainerTest {

    class MP4Playback(source: String, mimeType: String?, options: Options) :
            Playback(source, mimeType, options, name = name, supportsSource = supportsSource) {
        companion object {
            const val name: String = "mp4"
            val supportsSource: PlaybackSupportCheck = { source, _ -> source.endsWith(".mp4") }

            val entry = PlaybackEntry(
                    name = name,
                    supportsSource = supportsSource,
                    factory = { source, mimeType, options -> MP4Playback(source, mimeType, options) })
        }
    }

    class TestContainerPlugin(container: Container) : UIContainerPlugin(container) {
        companion object : NamedType {
            override val name = "testContainerPlugin"

            val entry = pluginEntry(name = name, activeInChromelessMode = true, factory = { container -> TestContainerPlugin(container) })
        }

        override val name: String
            get() = Companion.name

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
        BaseObject.applicationContext = ApplicationProvider.getApplicationContext()
        Loader.clearPlugins()
        Loader.clearPlaybacks()
    }

    @Test
    fun shouldLoadPlugins() {
        Loader.register(
                PluginEntry.Container(name = ContainerPlugin.name, factory = { context -> ContainerPlugin(context) }))
        val container = Container(Options())

        assertTrue("no plugins", container.plugins.isNotEmpty())
    }

    @Test
    fun shouldLoadPlaybackForSupportedSource() {
        Loader.register(MP4Playback.entry)
        val container = Container(Options())
        container.load("some_source.mp4")

        assertNotNull("should have created playback", container.playback)
        assertEquals("should have created mp4 playback", container.playback?.name, MP4Playback.name)
    }

    @Test
    fun shouldNotLoadNoOpPlaybackForUnsupportedSource() {
        Loader.register(NoOpPlayback.entry)
        Loader.register(MP4Playback.entry)
        val container = Container(Options("some_unknown_source.mp0"))

        assertNull("should not have created playback", container.playback)
        assertNotEquals("should not have created no-op playback", container.playback?.name, NoOpPlayback.entry.name)
    }

    @Test
    fun shouldNotTriggerPlaybackChangedWhenSameNullPlayback() {
        Loader.register(MP4Playback.entry)
        val container = Container(Options("some_unknown_source.mp0"))

        var callbackWasCalled = false
        container.on(InternalEvent.WILL_CHANGE_PLAYBACK.value) { callbackWasCalled = true }
        container.on(InternalEvent.DID_CHANGE_PLAYBACK.value) { callbackWasCalled = true }

        container.load(source = "some_unknown_source.mp0")
        assertFalse("CHANGE_PLAYBACK triggered " + container.playback, callbackWasCalled)
    }

    @Test
    fun shouldTriggerPlaybackChangedWhenDifferentPlayback() {
        Loader.register(MP4Playback.entry)
        val container = Container(Options("some_unknown_source.mp0"))

        val previousPlayback: Playback? = container.playback
        var callbackWasCalled = false
        container.on(InternalEvent.WILL_CHANGE_PLAYBACK.value) {
            assertFalse("DID_CHANGE_PLAYBACK triggered before WILL_CHANGE_PLAYBACK", callbackWasCalled)
            assertEquals("playback already changed", previousPlayback, container.playback)
        }
        container.on(InternalEvent.DID_CHANGE_PLAYBACK.value) { callbackWasCalled = true }

        container.load(source = "some_source.mp4")
        assertTrue("DID_CHANGE_PLAYBACK not triggered", callbackWasCalled)
    }

    @Test
    fun shouldTriggerLoadSourceEventsOnNewSource() {
        Loader.register(MP4Playback.entry)
        val container = Container(Options("aSource.mp4"))

        var willLoadWasCalled = false
        var didLoadWasCalled = false
        container.on(InternalEvent.WILL_LOAD_SOURCE.value) { willLoadWasCalled = true }
        container.on(InternalEvent.DID_LOAD_SOURCE.value) { didLoadWasCalled = true }

        container.load(source = "anotherSource.mp4")
        assertTrue("Will Load Source was not called", willLoadWasCalled)
        assertTrue("Did Load Source was not called", didLoadWasCalled)
    }

    @Test
    fun shouldTriggerLoadSourceEventsOnSameSource() {
        Loader.register(MP4Playback.entry)
        val container = Container(Options("aSource.mp4"))

        var willLoadWasCalled = false
        var didLoadWasCalled = false
        container.on(InternalEvent.WILL_LOAD_SOURCE.value) { willLoadWasCalled = true }
        container.on(InternalEvent.DID_LOAD_SOURCE.value) { didLoadWasCalled = true }

        container.load(source = "aSource.mp4")
        assertTrue("Will Load Source was not called", willLoadWasCalled)
        assertTrue("Did Load Source was not called", didLoadWasCalled)
    }

    @Test
    fun shouldTriggerDidNotLoadSourceEventsWhenNotSupported() {
        Loader.register(MP4Playback.entry)
        val container = Container(Options("aSource.mp8"))

        var willLoadWasCalled = false
        var didLoadWasCalled = false
        var didNotLoadWasCalled = false

        container.on(InternalEvent.WILL_LOAD_SOURCE.value) { willLoadWasCalled = true }
        container.on(InternalEvent.DID_LOAD_SOURCE.value) { didLoadWasCalled = true }
        container.on(InternalEvent.DID_NOT_LOAD_SOURCE.value) { didNotLoadWasCalled = true }

        container.load(source = "invalid_source.mp8")
        assertTrue("Will Load Source was not called", willLoadWasCalled)
        assertTrue("Did Not Load Source was not called", didNotLoadWasCalled)
        assertFalse("Did Load Source was called", didLoadWasCalled)
    }

    @Test
    fun shouldTriggerDestroyEvents() {
        val container = Container(Options())
        val listenObject = BaseObject()

        var willDestroyCalled = false
        var didDestroyCalled = false

        listenObject.listenTo(container, InternalEvent.WILL_DESTROY.value) { willDestroyCalled = true }
        listenObject.listenTo(container, InternalEvent.DID_DESTROY.value) { didDestroyCalled = true }

        container.destroy()

        assertTrue("Will Destroy was not called", willDestroyCalled)
        assertTrue("Did Destroy was not called", didDestroyCalled)
    }

    @Test
    @Ignore @SuppressLint("IgnoreWithoutReason")
    fun shouldDestroyPlaybackOnDestroy() {
        Loader.register(MP4Playback.entry)
        val container = Container(Options())
        container.load("some_source.mp4")

        assertNotNull("No playback", container.playback)

        var didDestroyCalled = false
        container.listenTo(container.playback!!, InternalEvent.DID_DESTROY.value) { didDestroyCalled = true }

        container.destroy()

        assertTrue("Playback did destroy not called", didDestroyCalled)
    }

    @Test
    fun shouldClearPlaybackOnDestroy() {
        Loader.register(MP4Playback.entry)

        val container = Container(Options())
        container.load("some_source.mp4")

        assertNotNull("No playback", container.playback)

        container.destroy()

        assertNull("Valid playback", container.playback)
    }

    @Test
    fun shouldDestroyPluginsOnDestroy() {
        val (container, testPlugin) = setupTestContainerPlugin()

        var pluginDestroyCalled = false
        testPlugin!!.destroyMethod = { pluginDestroyCalled = true }

        container.destroy()

        assertTrue("Plugin was not destroyed", pluginDestroyCalled)
    }

    @Test
    fun shouldHandlePluginDestroyExceptionOnDestroy() {
        val (container, testPlugin) = setupTestContainerPlugin()

        val expectedLogMessage = "[Container] Plugin ${testPlugin!!.javaClass.simpleName} " +
                                 "crashed during destroy"

        testPlugin.destroyMethod = { throw NullPointerException() }

        container.destroy()

        assertEquals(expectedLogMessage, ShadowLog.getLogsForTag("Clappr")[0].msg)
    }

    @Test
    fun shouldClearPluginsOnDestroy() {
        Loader.register(
                PluginEntry.Container(name = ContainerPlugin.name, factory = { context -> ContainerPlugin(context) }))
        val container = Container(Options())

        assertFalse("No plugins", container.plugins.isEmpty())

        container.destroy()

        assertTrue("Plugins not cleared", container.plugins.isEmpty())
    }

    @Test
    fun shouldStoplisteningOnDestroy() {
        val triggerObject = BaseObject()
        val container = Container(Options())

        var numberOfTriggers = 0
        container.listenTo(triggerObject, "containerTest") { numberOfTriggers++ }

        triggerObject.trigger("containerTest")
        assertEquals("no trigger", 1, numberOfTriggers)

        container.destroy()

        triggerObject.trigger("containerTest")
        assertEquals("trigger", 1, numberOfTriggers)
    }

    @Test
    fun shouldSetPlaybackOptionsWhenLoadContainer() {
        Loader.register(MP4Playback.entry)
        val source = "some_source.mp4"
        val newOptions = Options()
        newOptions[ClapprOption.POSTER.value] = "fake-poster-url"

        val container = Container(options = newOptions).apply { load(source) }

        assertNotNull(container.options)
        assertEquals(container.options, container.playback?.options)
    }

    @Test
    fun shouldTriggerUpdateOptionOnSetOptions() {
        Loader.register(MP4Playback.entry)
        val source = "some_source.mp4"
        val container = Container(options = Options()).apply { load(source) }

        var callbackWasCalled = false
        container.on(InternalEvent.DID_UPDATE_OPTIONS.value) { callbackWasCalled = true }

        container.options = Options(source = "new_source")

        assertTrue("should trigger DID_UPDATE_OPTIONS on set options", callbackWasCalled)
    }

    @Test
    fun shouldRenderPluginsOnRender() {
        val (container, testPlugin) = setupTestContainerPlugin()

        var pluginRenderCalled = false
        testPlugin!!.renderMethod = { pluginRenderCalled = true }

        container.render()

        assertTrue("Plugin was not rendered", pluginRenderCalled)
    }

    @Test
    fun shouldHandlePluginRenderExceptionOnDestroy() {
        val (container, testPlugin) = setupTestContainerPlugin()

        val expectedLogMessage = "[Container] Plugin ${testPlugin!!.javaClass.simpleName} " +
                                 "crashed during render"

        testPlugin.renderMethod = { throw NullPointerException() }

        container.render()

        assertEquals(expectedLogMessage, ShadowLog.getLogsForTag("Clappr")[0].msg)
    }

    @Test
    fun `should not have uiPlugins loaded with chromeless options set`() {
        Loader.register(UIContainerPluginWithEventsBound.entry)

        val options = Options(
            options = hashMapOf(ClapprOption.CHROMELESS.value to true)
        )
        val (container, _) = setupTestContainerPlugin(options)

        assertNull(container.plugins.filterIsInstance(UIContainerPluginWithEventsBound::class.java).firstOrNull())

    }

    @Test
    fun `should have uiPlugins loaded with chromeless options set when PluginEntry activates it`() {

        val options = Options(
            options = hashMapOf(ClapprOption.CHROMELESS.value to true)
        )
        val (container, _) = setupTestContainerPlugin(options)

        assertNotNull(container.plugins.filterIsInstance(TestContainerPlugin::class.java).firstOrNull())

    }

    @Test
    fun `should not bind events to uiPlugins with chromeless options set`() {

        Loader.register(UIContainerPluginWithEventsBound.entry)

        val options = Options(
            options = hashMapOf(ClapprOption.CHROMELESS.value to true)
        )
        val (container, _) = setupTestContainerPlugin(options)

        container.trigger(Event.WILL_LOAD_SOURCE.value)
    }

    @Test
    fun `should have uiPlugins loaded with chromeless options set to false`() {
        val options = Options(
            options = hashMapOf(ClapprOption.CHROMELESS.value to false)
        )
        val (container, _) = setupTestContainerPlugin(options)

        assertTrue(container.plugins.filterIsInstance(UIPlugin::class.java).isNotEmpty())
    }

    private fun setupTestContainerPlugin(options: Options = Options()): Pair<Container, TestContainerPlugin?> {
        Loader.register(TestContainerPlugin.entry)

        val container = Container(options)
        val testPlugin = container.plugins.firstOrNull() as? TestContainerPlugin

        return Pair(container, testPlugin)
    }

    class UIContainerPluginWithEventsBound(container: Container) : UIContainerPlugin(container) {
        companion object : NamedType {
            override val name = "UIContainerPluginWithEventsBound"

            val entry = pluginEntry(name = name, factory = { container -> UIContainerPluginWithEventsBound(container) })
        }

        init {
            listenTo(container, Event.WILL_LOAD_SOURCE.value) {
                fail("should not bind events on chromeless mode")
            }
        }

    }
}