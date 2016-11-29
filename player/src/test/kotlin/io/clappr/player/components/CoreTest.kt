package io.clappr.player.base
import android.os.Bundle
import io.clappr.player.BuildConfig
import io.clappr.player.components.Container
import io.clappr.player.components.Core
import io.clappr.player.components.Playback
import io.clappr.player.components.PlaybackSupportInterface
import io.clappr.player.plugin.core.CorePlugin
import io.clappr.player.plugin.Loader
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23))
open class CoreTest {
    class CoreTestPlayback(source: String, mimeType: String? = null, options: Options = Options()) : Playback(source, mimeType, options) {
        companion object : PlaybackSupportInterface {
            override val name: String = "container_test"

            override fun supportsSource(source: String, mimeType: String?): Boolean {
                return source.isNotEmpty()
            }
        }
    }

    @Before
    fun setup() {
        BaseObject.context = ShadowApplication.getInstance().applicationContext
    }

    @Test
    fun shouldLoadPlugins() {
        Loader.registerPlugin(CorePlugin::class)
        val core = Core(Loader(), Options())

        assertTrue("no plugins", core.plugins.isNotEmpty())
        assertTrue("no containers", core.containers.isNotEmpty())
        assertNotNull("no active container", core.activeContainer)
    }

    @Test
    fun shouldLoadPlayback() {
        Loader.registerPlayback(CoreTestPlayback::class)
        val core = Core(Loader(), options = Options(source = "some_source"))

        assertNotNull("no active playback", core.activePlayback)
    }

    @Test
    fun shouldNotTriggerActiveContainerChangedForSameContainer() {
        val core = Core(Loader(), Options())

        var callbackWasCalled = false
        core.on(InternalEvent.WILL_CHANGE_ACTIVE_CONTAINER.value, Callback.wrap { bundle: Bundle? -> callbackWasCalled = true })
        core.on(InternalEvent.DID_CHANGE_ACTIVE_CONTAINER.value, Callback.wrap { bundle: Bundle? -> callbackWasCalled = true })

        assertNotNull("invalid container", core.activeContainer)

        core.activeContainer = core.activeContainer
        assertFalse("should not trigger CHANGE_ACTIVE_CONTAINER for same value", callbackWasCalled)
    }

    @Test
    fun shouldTriggerActiveContainerChanged() {
        val core = Core(Loader(), Options())

        var callbackWasCalled = false
        val previousActiveContainer: Container? = core.activeContainer
        core.on(InternalEvent.WILL_CHANGE_ACTIVE_CONTAINER.value, Callback.wrap { bundle: Bundle? ->
            assertFalse("DID_CHANGE_ACTIVE_CONTAINER triggered before WILL_CHANGE_ACTIVE_CONTAINER", callbackWasCalled)
            assertEquals("container already changed", previousActiveContainer, core.activeContainer)
        })
        core.on(InternalEvent.DID_CHANGE_ACTIVE_CONTAINER.value, Callback.wrap { bundle: Bundle? -> callbackWasCalled = true })

        assertNotNull("invalid container", core.activeContainer)

        core.activeContainer = null
        assertTrue("should trigger CHANGE_ACTIVE_CONTAINER for different value", callbackWasCalled)
    }

    @Test
    fun shouldNotTriggerActivePlaybackChangedForSamePlayback() {
        Loader.registerPlayback(CoreTestPlayback::class)
        val core = Core(Loader(), Options())

        var callbackWasCalled = false
        core.on(InternalEvent.WILL_CHANGE_ACTIVE_PLAYBACK.value, Callback.wrap { bundle: Bundle? -> callbackWasCalled = true })
        core.on(InternalEvent.DID_CHANGE_ACTIVE_PLAYBACK.value, Callback.wrap { bundle: Bundle? -> callbackWasCalled = true })

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
        val core = Core(Loader(), Options())

        var callbackWasCalled = false
        var previousActivePlayback: Playback? = core.activePlayback
        core.on(InternalEvent.WILL_CHANGE_ACTIVE_CONTAINER.value, Callback.wrap { bundle: Bundle? ->
            assertFalse("DID_CHANGE_ACTIVE_PLAYBACK triggered before WILL_CHANGE_ACTIVE_CONTAINER", callbackWasCalled)
            assertEquals("container already changed", previousActivePlayback, core.activePlayback)
        })
        core.on(InternalEvent.DID_CHANGE_ACTIVE_PLAYBACK.value, Callback.wrap { bundle: Bundle? -> callbackWasCalled = true })

        assertNull("valid playback for no source", core.activePlayback)

        core.activeContainer?.load(source = "some_media")
        assertTrue("should trigger DID_CHANGE_ACTIVE_PLAYBACK for different value", callbackWasCalled)
    }
}