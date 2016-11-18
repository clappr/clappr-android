package com.globo.clappr.base

import android.os.Bundle
import com.globo.clappr.BuildConfig
import com.globo.clappr.components.Core
import com.globo.clappr.playback.NoOpPlayback
import com.globo.clappr.plugin.core.CorePlugin
import com.globo.clappr.plugin.Loader
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23))
open class CoreTest {
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
        Loader.registerPlayback(NoOpPlayback::class)
        val core = Core(Loader(), options = Options(source = "some_source"))

        assertNotNull("no active playback", core.activePlayback)
    }

    @Test
    fun shouldTriggerActiveContainerChanged() {
        val core = Core(Loader(), Options())

        var callbackWasCalled = false
        core.on(InternalEvent.ACTIVE_CONTAINER_CHANGED.value, Callback.wrap { bundle: Bundle? -> callbackWasCalled = true })

        assertNotNull("invalid container", core.activeContainer)

        core.activeContainer = core.activeContainer
        assertFalse("should not trigger ACTIVE_CONTAINER_CHANGED for same value", callbackWasCalled)

        core.activeContainer = null
        assertTrue("should trigger ACTIVE_CONTAINER_CHANGED for different value", callbackWasCalled)
    }

    @Test
    fun shouldTriggerActivePlaybackChanged() {
        Loader.registerPlayback(NoOpPlayback::class)
        val core = Core(Loader(), Options())

        var callbackWasCalled = false
        core.on(InternalEvent.ACTIVE_PLAYBACK_CHANGED.value, Callback.wrap { bundle: Bundle? -> callbackWasCalled = true })

        assertNull("valid playback for no source", core.activePlayback)

        core.activeContainer?.load(source = "some_media")
        assertTrue("should trigger ACTIVE_PLAYBACK_CHANGED for different value", callbackWasCalled)

        callbackWasCalled = false
        core.activeContainer?.playback = core.activeContainer?.playback
        assertFalse("should not trigger ACTIVE_PLAYBACK_CHANGED for same value", callbackWasCalled)

        core.activeContainer?.playback = null
        assertTrue("should trigger ACTIVE_PLAYBACK_CHANGED for different value", callbackWasCalled)
    }

}