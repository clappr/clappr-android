package io.clappr.player.plugin

import android.view.KeyEvent
import androidx.test.core.app.ApplicationProvider
import io.clappr.player.base.BaseObject
import io.clappr.player.base.Event
import io.clappr.player.base.EventData
import io.clappr.player.base.Options
import io.clappr.player.base.keys.Action
import io.clappr.player.base.keys.Key
import io.clappr.player.components.Core
import io.clappr.player.plugin.core.externalinput.ExternalInputPlugin
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class ExternalInputPluginTest {

    lateinit var core: Core

    lateinit var externalInputPlugin: ExternalInputPlugin

    @Before
    fun setUp() {
        BaseObject.applicationContext = ApplicationProvider.getApplicationContext()

        core = Core(Options())

        externalInputPlugin = ExternalInputPlugin(core)
    }


    @Test
    fun `should trigger KEY_PRESSED event for PLAY button`() {
        val expectedKeyCode = Key.PLAY.value

        var keyCode: String? = null

        core.on(Event.KEY_PRESSED.value) { bundle ->
            bundle?.let { keyCode = it.getString(EventData.PRESSED_KEY_CODE.value) }
        }

        externalInputPlugin.holdKeyEvent(KeyEvent(-1, KeyEvent.KEYCODE_MEDIA_PLAY))

        assertEquals(expectedKeyCode, keyCode)
    }


    @Test
    fun `should trigger KEY_PRESSED event with DOWN action`() {
        val expectedActionCode = Action.DOWN.value

        var actionCode: String? = null

        core.on(Event.KEY_PRESSED.value) { bundle ->
            bundle?.let { actionCode = it.getString(EventData.PRESSED_KEY_ACTION.value) }
        }

        externalInputPlugin.holdKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, -1))

        assertEquals(expectedActionCode, actionCode)
    }

    @Test
    fun `should trigger KEY_PRESSED event with UP action`() {
        val expectedActionCode = Action.UP.value

        var actionCode: String? = null

        core.on(Event.KEY_PRESSED.value) { bundle ->
            bundle?.let { actionCode = it.getString(EventData.PRESSED_KEY_ACTION.value) }
        }

        externalInputPlugin.holdKeyEvent(KeyEvent(KeyEvent.ACTION_UP, -1))

        assertEquals(expectedActionCode, actionCode)
    }

    @Test
    fun `should trigger KEY_PRESSED event for PAUSE button`() {
        val expectedKeyCode = Key.PAUSE.value

        var keyCode: String? = null

        core.on(Event.KEY_PRESSED.value) { bundle ->
            bundle?.let { keyCode = it.getString(EventData.PRESSED_KEY_CODE.value) }
        }

        externalInputPlugin.holdKeyEvent(KeyEvent(-1, KeyEvent.KEYCODE_MEDIA_PAUSE))

        assertEquals(expectedKeyCode, keyCode)
    }
}