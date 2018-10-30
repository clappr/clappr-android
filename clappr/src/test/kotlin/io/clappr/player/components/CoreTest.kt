package io.clappr.player.base
import io.clappr.player.BuildConfig
import io.clappr.player.components.Container
import io.clappr.player.components.Core
import io.clappr.player.components.Playback
import io.clappr.player.components.PlaybackSupportInterface
import io.clappr.player.plugin.core.CorePlugin
import io.clappr.player.plugin.Loader
import io.clappr.player.plugin.core.UICorePlugin
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication
import org.robolectric.shadows.ShadowLog

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [23], shadows = [ShadowLog::class])
open class CoreTest {
    class CoreTestPlayback(source: String, mimeType: String? = null, options: Options = Options()) : Playback(source, mimeType, options) {
        companion object : PlaybackSupportInterface {
            override val name: String = "container_test"

            override fun supportsSource(source: String, mimeType: String?): Boolean {
                return source.isNotEmpty()
            }
        }
    }

    class TestCorePlugin(core: Core) : UICorePlugin(core) {
        companion object : NamedType {
            override val name: String?
                get() = "testCorePlugin"
        }

        var destroyMethod: (() -> Unit)? = null
        var renderMethod: (() -> Unit)? = null

        override fun destroy() { destroyMethod?.invoke() }
        override fun render() { renderMethod?.invoke() }
    }

    @Before
    fun setup() {
        BaseObject.context = ShadowApplication.getInstance().applicationContext
    }

    @Test
    fun shouldLoadPlugins() {
        Loader.registerPlugin(CorePlugin::class)
        val core = Core(Loader(), Options()).apply { load() }

        assertTrue("no plugins", core.plugins.isNotEmpty())
        assertTrue("no containers", core.containers.isNotEmpty())
        assertNotNull("no active container", core.activeContainer)
    }

    @Test
    fun shouldLoadPlayback() {
        Loader.registerPlayback(CoreTestPlayback::class)
        val core = Core(Loader(), options = Options(source = "some_source")).apply { load() }

        assertNotNull("no active playback", core.activePlayback)
    }

    @Test
    fun shouldNotTriggerActiveContainerChangedForSameContainer() {
        val core = Core(Loader(), Options()).apply { load() }

        var callbackWasCalled = false
        core.on(InternalEvent.WILL_CHANGE_ACTIVE_CONTAINER.value, Callback.wrap { callbackWasCalled = true })
        core.on(InternalEvent.DID_CHANGE_ACTIVE_CONTAINER.value, Callback.wrap { callbackWasCalled = true })

        assertNotNull("invalid container", core.activeContainer)

        core.activeContainer = core.activeContainer
        assertFalse("should not trigger CHANGE_ACTIVE_CONTAINER for same value", callbackWasCalled)
    }

    @Test
    fun shouldTriggerActiveContainerChanged() {
        val core = Core(Loader(), Options()).apply { load() }

        var callbackWasCalled = false
        val previousActiveContainer: Container? = core.activeContainer
        core.on(InternalEvent.WILL_CHANGE_ACTIVE_CONTAINER.value, Callback.wrap {
            assertFalse("DID_CHANGE_ACTIVE_CONTAINER triggered before WILL_CHANGE_ACTIVE_CONTAINER", callbackWasCalled)
            assertEquals("container already changed", previousActiveContainer, core.activeContainer)
        })
        core.on(InternalEvent.DID_CHANGE_ACTIVE_CONTAINER.value, Callback.wrap { callbackWasCalled = true })

        assertNotNull("invalid container", core.activeContainer)

        core.activeContainer = null
        assertTrue("should trigger CHANGE_ACTIVE_CONTAINER for different value", callbackWasCalled)
    }

    @Test
    fun shouldNotTriggerActivePlaybackChangedForSamePlayback() {
        Loader.registerPlayback(CoreTestPlayback::class)
        val core = Core(Loader(), Options()).apply { load() }

        var callbackWasCalled = false
        core.on(InternalEvent.WILL_CHANGE_ACTIVE_PLAYBACK.value, Callback.wrap { callbackWasCalled = true })
        core.on(InternalEvent.DID_CHANGE_ACTIVE_PLAYBACK.value, Callback.wrap { callbackWasCalled = true })

        assertNull("valid playback for no source", core.activePlayback)
        core.activeContainer?.playback = null
        assertFalse("should not trigger CHANGE_ACTIVE_PLAYBACK for same value", callbackWasCalled)

        core.activeContainer?.load(source = "some_media")
        callbackWasCalled = false
        assertNotNull("invalid playback for valid source", core.activePlayback)
        core.activeContainer?.playback = core.activeContainer?.playback
        assertFalse("should not trigger CHANGE_ACTIVE_PLAYBACK for same value", callbackWasCalled)
    }

    @Test
    fun shouldTriggerActivePlaybackChanged() {
        Loader.registerPlayback(CoreTestPlayback::class)
        val core = Core(Loader(), Options()).apply { load() }

        var callbackWasCalled = false
        var previousActivePlayback: Playback? = core.activePlayback
        core.on(InternalEvent.WILL_CHANGE_ACTIVE_CONTAINER.value, Callback.wrap {
            assertFalse("DID_CHANGE_ACTIVE_PLAYBACK triggered before WILL_CHANGE_ACTIVE_CONTAINER", callbackWasCalled)
            assertEquals("container already changed", previousActivePlayback, core.activePlayback)
        })
        core.on(InternalEvent.DID_CHANGE_ACTIVE_PLAYBACK.value, Callback.wrap { callbackWasCalled = true })

        assertNull("valid playback for no source", core.activePlayback)

        core.activeContainer?.load(source = "some_media")
        assertTrue("should trigger DID_CHANGE_ACTIVE_PLAYBACK for different value", callbackWasCalled)
    }

    @Test
    fun shouldTriggerDestroyEvents() {
        val core = Core(Loader(), Options())
        val listenObject = BaseObject()

        var willDestroyCalled = false
        var didDestroyCalled = false

        listenObject.listenTo(core, InternalEvent.WILL_DESTROY.value, Callback.wrap { willDestroyCalled = true })
        listenObject.listenTo(core, InternalEvent.DID_DESTROY.value, Callback.wrap { didDestroyCalled = true })

        core.destroy()

        assertTrue("Will Destroy was not called", willDestroyCalled)
        assertTrue("Did Destroy was not called", didDestroyCalled)
    }

    @Test
    fun shouldDestroyContainersOnDestroy() {
        val core = Core(Loader(), Options())

        assertFalse("No container", core.containers.isEmpty())

        var didDestroyCalled = false
        core.listenTo(core.containers.first(), InternalEvent.DID_DESTROY.value, Callback.wrap { didDestroyCalled = true })

        core.destroy()

        assertTrue("Container did destroy not called", didDestroyCalled)
    }

    @Test
    fun shouldClearContainersOnDestroy() {
        val core = Core(Loader(), Options())

        assertFalse("No container", core.containers.isEmpty())

        core.destroy()

        assertTrue("Valid container", core.containers.isEmpty())
    }

    @Test
    fun shouldDestroyPluginsOnDestroy() {
        val (core, testPlugin) = setupTestCorePlugin()

        var pluginDestroyCalled = false
        testPlugin.destroyMethod = { pluginDestroyCalled = true }

        core.destroy()

        assertTrue("Plugin was not destroyed", pluginDestroyCalled)
    }

    @Test
    fun shouldHandlePluginDestroyExceptionOnDestroy() {
        val (core, testPlugin) = setupTestCorePlugin()

        val expectedLogMessage = "[Core] Plugin ${testPlugin.javaClass.simpleName} " +
                "crashed during destroy"

        testPlugin.destroyMethod = { throw NullPointerException() }

        core.destroy()

        assertEquals(expectedLogMessage, ShadowLog.getLogs()[0].msg)
    }

    @Test
    fun shouldClearPluginsOnDestroy() {
        Loader.registerPlugin(CorePlugin::class)
        val core = Core(Loader(), Options())

        assertFalse("No plugins", core.plugins.isEmpty())

        core.destroy()

        assertTrue("Valid plugin", core.plugins.isEmpty())
    }

    @Test
    fun shouldStoplisteningOnDestroy() {
        val triggerObject = BaseObject()
        val core = Core(Loader(), Options())

        var numberOfTriggers = 0
        core.listenTo(triggerObject, "coreTest", Callback.wrap { numberOfTriggers++ })

        triggerObject.trigger("coreTest")
        assertEquals("no trigger", 1, numberOfTriggers)

        core.destroy()

        triggerObject.trigger("coreTest")
        assertEquals("trigger", 1, numberOfTriggers)
    }

    @Test
    fun shouldSetContainerOptionsWhenSetOptions() {
        val core = Core(Loader(), options = Options(source = "some_source")).apply { load() }
        core.options = Options(source = "new_source")

        assertEquals(core.options, core.activeContainer?.options)
    }

    @Test
    fun shouldTriggerUpdateOptionOnSetOptions() {
        val core = Core(Loader(), options = Options(source = "some_source")).apply { load() }

        var callbackWasCalled = false
        core.on(InternalEvent.DID_UPDATE_OPTIONS.value, Callback.wrap { callbackWasCalled = true })

        core.options = Options(source = "new_source")

        assertTrue("should trigger DID_UPDATE_OPTIONS on set options", callbackWasCalled)
    }

    @Test
    fun shouldRenderPluginsOnRender() {
        val (core, testPlugin) = setupTestCorePlugin()

        var pluginRenderCalled = false
        testPlugin.renderMethod = { pluginRenderCalled = true }

        core.render()

        assertTrue("Plugin was not rendered", pluginRenderCalled)
    }

    @Test
    fun shouldHandlePluginRenderExceptionOnDestroy() {
        val (core, testPlugin) = setupTestCorePlugin()

        val expectedLogMessage = "[Core] Plugin ${testPlugin.javaClass.simpleName} " +
                "crashed during render"

        testPlugin.renderMethod = { throw NullPointerException() }

        core.render()

        assertEquals(expectedLogMessage, ShadowLog.getLogs()[0].msg)
    }

    private fun setupTestCorePlugin(): Pair<Core, TestCorePlugin> {
        Loader.registerPlugin(TestCorePlugin::class)

        val core = Core(Loader(), Options())
        val testPlugin = core.plugins[0] as TestCorePlugin

        return Pair(core, testPlugin)
    }
}