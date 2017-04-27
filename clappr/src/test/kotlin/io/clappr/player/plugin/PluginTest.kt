package io.clappr.player.plugin

import io.clappr.player.BuildConfig
import io.clappr.player.base.BaseObject
import io.clappr.player.base.Callback
import io.clappr.player.base.InternalEvent
import org.junit.Assert.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23))
class PluginTest {
    class TestPlugin: Plugin(BaseObject())

    @Before
    fun setup() {
        BaseObject.context = ShadowApplication.getInstance().applicationContext
    }

    @Test
    fun shouldStartDisabled() {
        val plugin = TestPlugin()
        assertTrue("plugin enabled", plugin.state == Plugin.State.DISABLED)
    }

    @Test
    fun shouldStopListeningOnDestroy() {
        val triggerObject = BaseObject()
        val plugin = TestPlugin()

        var numberOfTriggers = 0
        plugin.listenTo(triggerObject, "pluginTest", Callback.wrap { numberOfTriggers++ })

        triggerObject.trigger("pluginTest")
        assertEquals("no trigger", 1, numberOfTriggers)

        plugin.destroy()
        triggerObject.trigger("pluginTest")
        assertEquals("no trigger", 1, numberOfTriggers)
    }

    @Test @Ignore
    fun shouldTriggerEventsOnDestroy() {
        val listenObject = BaseObject()
        val plugin = TestPlugin()

        var willDestroyCalled = false
        var didDestroyCalled = false
        listenObject.listenTo(plugin, InternalEvent.WILL_DESTROY.value, Callback.wrap { willDestroyCalled = true })
        listenObject.listenTo(plugin, InternalEvent.DID_DESTROY.value, Callback.wrap { didDestroyCalled = true })

        plugin.destroy()

        assertTrue("Will destroy not triggered", willDestroyCalled)
        assertTrue("Did destroy not triggered", didDestroyCalled)
    }

}