package io.clappr.player.components

import android.view.View
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import com.nhaarman.mockito_kotlin.*
import io.clappr.player.base.BaseObject
import io.clappr.player.base.InternalEvent
import io.clappr.player.base.NamedType
import io.clappr.player.base.Options
import io.clappr.player.plugin.Loader
import io.clappr.player.plugin.PluginEntry
import io.clappr.player.plugin.core.CorePlugin
import io.clappr.player.plugin.core.UICorePlugin
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23], shadows = [ShadowLog::class])
open class CoreTest {
    class CoreTestPlayback(source: String, mimeType: String? = null, options: Options = Options()) :
            Playback(source, mimeType, options, name, supportsSource) {
        companion object {
            const val name = "core_test"
            val supportsSource: PlaybackSupportCheck = { source, _ -> source.isNotEmpty() }
            val entry = PlaybackEntry(
                    name = name,
                    supportsSource = supportsSource,
                    factory = { source, mimeType, options -> CoreTestPlayback(source, mimeType, options) })
        }
    }

    class TestCorePlugin(core: Core) : UICorePlugin(core) {
        companion object : NamedType {
            override val name = "testCorePlugin"

            val entry = PluginEntry.Core(name = name, factory = { core -> TestCorePlugin(core) })
        }

        var destroyMethod: (() -> Unit)? = null
        var renderMethod: (() -> Unit)? = null
        var didResizeWasCalled = false

        init {
            listenTo(core, InternalEvent.DID_RESIZE.value) { didResizeWasCalled = true }
        }

        override fun destroy() {
            destroyMethod?.invoke()
        }

        override fun render() {
            renderMethod?.invoke()
        }
    }

    @Mock
    private lateinit var frameLayoutMock: FrameLayout

    @Captor
    private lateinit var onLayoutChangeListenerCapture: ArgumentCaptor<View.OnLayoutChangeListener>

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        BaseObject.applicationContext = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun shouldLoadPlugins() {
        Loader.register(PluginEntry.Core(name = CorePlugin.name, factory = { context -> CorePlugin(context) }))
        val core = Core(Options()).apply { load() }

        assertTrue("no plugins", core.plugins.isNotEmpty())
        assertTrue("no containers", core.containers.isNotEmpty())
        assertNotNull("no active container", core.activeContainer)
    }

    @Test
    fun shouldLoadPlayback() {
        Loader.register(CoreTestPlayback.entry)
        val core = Core(options = Options(source = "some_source")).apply { load() }

        assertNotNull("no active playback", core.activePlayback)
    }

    @Test
    fun shouldNotTriggerActiveContainerChangedForSameContainer() {
        val core = Core(Options()).apply { load() }

        var callbackWasCalled = false
        core.on(InternalEvent.WILL_CHANGE_ACTIVE_CONTAINER.value) { callbackWasCalled = true }
        core.on(InternalEvent.DID_CHANGE_ACTIVE_CONTAINER.value) { callbackWasCalled = true }

        assertNotNull("invalid container", core.activeContainer)

        core.activeContainer = core.activeContainer
        assertFalse("should not trigger CHANGE_ACTIVE_CONTAINER for same value", callbackWasCalled)
    }

    @Test
    fun shouldTriggerActiveContainerChanged() {
        val core = Core(Options()).apply { load() }

        var callbackWasCalled = false
        val previousActiveContainer: Container? = core.activeContainer
        core.on(InternalEvent.WILL_CHANGE_ACTIVE_CONTAINER.value) {
            assertFalse("DID_CHANGE_ACTIVE_CONTAINER triggered before WILL_CHANGE_ACTIVE_CONTAINER", callbackWasCalled)
            assertEquals("container already changed", previousActiveContainer, core.activeContainer)
        }
        core.on(InternalEvent.DID_CHANGE_ACTIVE_CONTAINER.value) { callbackWasCalled = true }

        assertNotNull("invalid container", core.activeContainer)

        core.activeContainer = null
        assertTrue("should trigger CHANGE_ACTIVE_CONTAINER for different value", callbackWasCalled)
    }

    @Test
    fun shouldNotTriggerActivePlaybackChangedForSamePlayback() {
        Loader.register(CoreTestPlayback.entry)
        val core = Core(Options()).apply { load() }

        var callbackWasCalled = false
        core.on(InternalEvent.WILL_CHANGE_ACTIVE_PLAYBACK.value) { callbackWasCalled = true }
        core.on(InternalEvent.DID_CHANGE_ACTIVE_PLAYBACK.value) { callbackWasCalled = true }

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
        Loader.register(CoreTestPlayback.entry)
        val core = Core(Options()).apply { load() }

        var callbackWasCalled = false
        val previousActivePlayback: Playback? = core.activePlayback
        core.on(InternalEvent.WILL_CHANGE_ACTIVE_CONTAINER.value) {
            assertFalse("DID_CHANGE_ACTIVE_PLAYBACK triggered before WILL_CHANGE_ACTIVE_CONTAINER", callbackWasCalled)
            assertEquals("container already changed", previousActivePlayback, core.activePlayback)
        }
        core.on(InternalEvent.DID_CHANGE_ACTIVE_PLAYBACK.value) { callbackWasCalled = true }

        assertNull("valid playback for no source", core.activePlayback)

        core.activeContainer?.load(source = "some_media")
        assertTrue("should trigger DID_CHANGE_ACTIVE_PLAYBACK for different value", callbackWasCalled)
    }

    @Test
    fun shouldTriggerDestroyEvents() {
        val core = Core(Options())
        val listenObject = BaseObject()

        var willDestroyCalled = false
        var didDestroyCalled = false

        listenObject.listenTo(core, InternalEvent.WILL_DESTROY.value) { willDestroyCalled = true }
        listenObject.listenTo(core, InternalEvent.DID_DESTROY.value) { didDestroyCalled = true }

        core.destroy()

        assertTrue("Will Destroy was not called", willDestroyCalled)
        assertTrue("Did Destroy was not called", didDestroyCalled)
    }

    @Test
    fun shouldDestroyContainersOnDestroy() {
        val core = Core(Options())

        assertFalse("No container", core.containers.isEmpty())

        var didDestroyCalled = false
        core.listenTo(core.containers.first(), InternalEvent.DID_DESTROY.value) { didDestroyCalled = true }

        core.destroy()

        assertTrue("Container did destroy not called", didDestroyCalled)
    }

    @Test
    fun shouldClearContainersOnDestroy() {
        val core = Core(Options())

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

        assertEquals(expectedLogMessage, ShadowLog.getLogsForTag("Clappr")[0].msg)
    }

    @Test
    fun shouldClearPluginsOnDestroy() {
        Loader.register(PluginEntry.Core(name = CorePlugin.name, factory = { context -> CorePlugin(context) }))
        val core = Core(Options())

        assertFalse("No plugins", core.plugins.isEmpty())

        core.destroy()

        assertTrue("Valid plugin", core.plugins.isEmpty())
    }

    @Test
    fun shouldStoplisteningOnDestroy() {
        val triggerObject = BaseObject()
        val core = Core(Options())

        var numberOfTriggers = 0
        core.listenTo(triggerObject, "coreTest") { numberOfTriggers++ }

        triggerObject.trigger("coreTest")
        assertEquals("no trigger", 1, numberOfTriggers)

        core.destroy()

        triggerObject.trigger("coreTest")
        assertEquals("trigger", 1, numberOfTriggers)
    }

    @Test
    fun shouldSetContainerOptionsWhenSetOptions() {
        val core = Core(options = Options(source = "some_source")).apply { load() }
        core.options = Options(source = "new_source")

        assertEquals(core.options, core.activeContainer?.options)
    }

    @Test
    fun shouldTriggerUpdateOptionOnSetOptions() {
        val core = Core(options = Options(source = "some_source")).apply { load() }

        var callbackWasCalled = false
        core.on(InternalEvent.DID_UPDATE_OPTIONS.value) { callbackWasCalled = true }

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

        assertEquals(expectedLogMessage, ShadowLog.getLogsForTag("Clappr")[0].msg)
    }

    @Test
    fun shouldRemoveLayoutChangeListenerOnDestroy() {
        val core = Core(options = Options(source = "source")).apply { view = frameLayoutMock }

        core.destroy()

        verify(frameLayoutMock).removeOnLayoutChangeListener(any())
    }

    @Test
    fun shouldAddLayoutChangeListenerOnRender() {
        val core = Core(options = Options(source = "source")).apply { view = frameLayoutMock }

        core.render()

        verify(frameLayoutMock).addOnLayoutChangeListener(any())
    }

    @Test
    fun shouldRemoveBeforeAddLayoutChangeListenerOnRender() {
        val core = Core(options = Options(source = "source")).apply { view = frameLayoutMock }

        core.render()

        with(inOrder(frameLayoutMock)) {
            verify(frameLayoutMock).removeOnLayoutChangeListener(any())
            verify(frameLayoutMock).addOnLayoutChangeListener(any())
        }
    }

    @Test
    fun shouldRemoveAndAddTheSameLayoutChangeListenerOnRender() {
        val core = Core(options = Options(source = "source")).apply { view = frameLayoutMock }

        core.render()

        verify(frameLayoutMock).removeOnLayoutChangeListener(capture(onLayoutChangeListenerCapture))
        verify(frameLayoutMock).addOnLayoutChangeListener(onLayoutChangeListenerCapture.value)
    }

    @Test
    fun shouldRemoveTheSameLayoutChangeListenerOnDestroy() {
        val core = Core(options = Options(source = "source")).apply { view = frameLayoutMock }

        core.render()
        verify(frameLayoutMock).addOnLayoutChangeListener(capture(onLayoutChangeListenerCapture))

        core.destroy()
        verify(frameLayoutMock, times(2))
                .removeOnLayoutChangeListener(onLayoutChangeListenerCapture.value)
    }

    @Test
    fun shouldTriggerDidResizeWhenLayoutChangeSizeHorizontallyToRight() {
        val (core, testPlugin) = setupTestCorePlugin().apply { first.view = frameLayoutMock }

        core.render()
        verify(frameLayoutMock).addOnLayoutChangeListener(capture(onLayoutChangeListenerCapture))

        val right = 1920
        val oldRight = 1080
        onLayoutChangeListenerCapture.value.onLayoutChange(
                frameLayoutMock, 0, 0, right, 0, 0, 0, oldRight, 0)

        assertTrue(testPlugin.didResizeWasCalled)
    }

    @Test
    fun shouldTriggerDidResizeWhenLayoutChangeSizeHorizontallyToLeft() {
        val (core, testPlugin) = setupTestCorePlugin().apply { first.view = frameLayoutMock }

        core.render()
        verify(frameLayoutMock).addOnLayoutChangeListener(capture(onLayoutChangeListenerCapture))

        val left = 1920
        val oldLeft = 1080
        onLayoutChangeListenerCapture.value.onLayoutChange(
                frameLayoutMock, left, 0, 0, 0, oldLeft, 0, 0, 0)

        assertTrue(testPlugin.didResizeWasCalled)
    }

    @Test
    fun shouldTriggerDidResizeWhenLayoutChangeSizeVerticallyToTop() {
        val (core, testPlugin) = setupTestCorePlugin().apply { first.view = frameLayoutMock }

        core.render()
        verify(frameLayoutMock).addOnLayoutChangeListener(capture(onLayoutChangeListenerCapture))

        val top = 1920
        val oldTop = 1080
        onLayoutChangeListenerCapture.value.onLayoutChange(
                frameLayoutMock, 0, top, 0, 0, 0, oldTop, 0, 0)

        assertTrue(testPlugin.didResizeWasCalled)
    }

    @Test
    fun shouldTriggerDidResizeWhenLayoutChangeSizeVerticallyToBottom() {
        val (core, testPlugin) = setupTestCorePlugin().apply { first.view = frameLayoutMock }

        core.render()
        verify(frameLayoutMock).addOnLayoutChangeListener(capture(onLayoutChangeListenerCapture))

        val bottom = 1920
        val oldBottom = 1080
        onLayoutChangeListenerCapture.value.onLayoutChange(
                frameLayoutMock, 0, 0, 0, bottom, 0, 0, 0, oldBottom)

        assertTrue(testPlugin.didResizeWasCalled)
    }

    @Test
    fun shouldNotTriggerDidResizeWhenLayoutNotChangeSize() {
        val (core, testPlugin) = setupTestCorePlugin().apply { first.view = frameLayoutMock }

        core.render()
        verify(frameLayoutMock).addOnLayoutChangeListener(capture(onLayoutChangeListenerCapture))

        onLayoutChangeListenerCapture.value.onLayoutChange(
                frameLayoutMock, 100, 100, 100, 100, 100, 100, 100, 100)

        assertFalse(testPlugin.didResizeWasCalled)
    }

    private fun setupTestCorePlugin(): Pair<Core, TestCorePlugin> {
        Loader.register(TestCorePlugin.entry)

        val core = Core(Options())
        val testPlugin = core.plugins[0] as TestCorePlugin

        return Pair(core, testPlugin)
    }
}