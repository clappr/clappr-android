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
        assertPressedKeyCodeEvent(Key.PLAY, KeyEvent.KEYCODE_MEDIA_PLAY)
    }


    @Test
    fun `should trigger KEY_PRESSED event with DOWN action`() {
        assertPressedAction(Action.DOWN, KeyEvent.ACTION_DOWN)
    }

    @Test
    fun `should trigger KEY_PRESSED event with UP action`() {
        assertPressedAction(Action.UP, KeyEvent.ACTION_UP)
    }

    @Test
    fun `should trigger KEY_PRESSED event for PAUSE button`() {
        assertPressedKeyCodeEvent(Key.PAUSE, KeyEvent.KEYCODE_MEDIA_PAUSE)
    }

    @Test
    fun `should trigger KEY_PRESSED event for STOP button`() {
        assertPressedKeyCodeEvent(Key.STOP, KeyEvent.KEYCODE_MEDIA_STOP)
    }

    @Test
    fun `should trigger KEY_PRESSED event for PLAY_PAUSE button`() {
        assertPressedKeyCodeEvent(Key.PLAY_PAUSE, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
    }

    private fun assertPressedKeyCodeEvent(expectedKeyCode: Key, keyToHold: Int){
        var keyCode: String? = null

        core.on(Event.DID_RECEIVE_INPUT_KEY.value) { bundle ->
            bundle?.let { keyCode = it.getString(EventData.INPUT_KEY_CODE.value) }
        }

        externalInputPlugin.holdKeyEvent(KeyEvent(-1, keyToHold))

        assertEquals(expectedKeyCode.value, keyCode)
    }

    private fun assertPressedAction(expectedActionCode: Action, actionKey: Int){
        var actionCode: String? = null

        core.on(Event.DID_RECEIVE_INPUT_KEY.value) { bundle ->
            bundle?.let { actionCode = it.getString(EventData.INPUT_KEY_ACTION.value) }
        }

        externalInputPlugin.holdKeyEvent(KeyEvent(actionKey, -1))

        assertEquals(expectedActionCode.value, actionCode)
    }
}