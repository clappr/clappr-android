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
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class ExternalInputPluginTest {

    private lateinit var core: Core

    private lateinit var externalInputPlugin: ExternalInputPlugin

    private val anyKeyCode = -1
    private val anyKeyAction = -1

    @Before
    fun setUp() {
        BaseObject.applicationContext = ApplicationProvider.getApplicationContext()

        core = Core(Options())

        externalInputPlugin = ExternalInputPlugin(core)
    }

    @Test
    fun `should trigger DID_RECEIVE_INPUT_KEY event for PLAY button`() {
        assertPressedKeyCodeEvent(Key.PLAY, KeyEvent.KEYCODE_MEDIA_PLAY)
    }

    @Test
    fun `should trigger DID_RECEIVE_INPUT_KEY event with DOWN action`() {
        assertPressedAction(Action.DOWN, KeyEvent.ACTION_DOWN)
    }

    @Test
    fun `should trigger DID_RECEIVE_INPUT_KEY event with UP action`() {
        assertPressedAction(Action.UP, KeyEvent.ACTION_UP)
    }

    @Test
    fun `should trigger DID_RECEIVE_INPUT_KEY event for PAUSE button`() {
        assertPressedKeyCodeEvent(Key.PAUSE, KeyEvent.KEYCODE_MEDIA_PAUSE)
    }

    @Test
    fun `should trigger DID_RECEIVE_INPUT_KEY event for STOP button`() {
        assertPressedKeyCodeEvent(Key.STOP, KeyEvent.KEYCODE_MEDIA_STOP)
    }

    @Test
    fun `should trigger DID_RECEIVE_INPUT_KEY event for PLAY_PAUSE button`() {
        assertPressedKeyCodeEvent(Key.PLAY_PAUSE, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
    }

    @Test
    fun `should trigger DID_RECEIVE_INPUT_KEY event for UP button`() {
        assertPressedKeyCodeEvent(Key.UP, KeyEvent.KEYCODE_DPAD_UP)
    }

    @Test
    fun `should trigger DID_RECEIVE_INPUT_KEY event for DOWN button`() {
        assertPressedKeyCodeEvent(Key.DOWN, KeyEvent.KEYCODE_DPAD_DOWN)
    }

    @Test
    fun `should trigger DID_RECEIVE_INPUT_KEY event for LEFT button`() {
        assertPressedKeyCodeEvent(Key.LEFT, KeyEvent.KEYCODE_DPAD_LEFT)
    }

    @Test
    fun `should trigger DID_RECEIVE_INPUT_KEY event for RIGHT button`() {
        assertPressedKeyCodeEvent(Key.RIGHT, KeyEvent.KEYCODE_DPAD_RIGHT)
    }

    @Test
    fun `should trigger DID_RECEIVE_INPUT_KEY event for BACK button`() {
        assertPressedKeyCodeEvent(Key.BACK, KeyEvent.KEYCODE_BACK)
    }

    @Test
    fun `should trigger DID_RECEIVE_INPUT_KEY event with key event is long press flag`() {
        val keyEventMock = mockk<KeyEvent>()
        var isLongPress = false

        core.on(Event.DID_RECEIVE_INPUT_KEY.value) { bundle ->
            bundle?.let { isLongPress = it.getBoolean(EventData.INPUT_KEY_IS_LONG_PRESS.value) }
        }

        every { keyEventMock.keyCode } returns anyKeyCode
        every { keyEventMock.action } returns anyKeyAction
        every { keyEventMock.isLongPress } returns true

        externalInputPlugin.holdKeyEvent(keyEventMock)

        assertTrue(isLongPress, "Is long press flag should be true")
    }

    private fun assertPressedKeyCodeEvent(expectedKeyCode: Key, keyToHold: Int){
        var keyCode: String? = null

        core.on(Event.DID_RECEIVE_INPUT_KEY.value) { bundle ->
            bundle?.let { keyCode = it.getString(EventData.INPUT_KEY_CODE.value) }
        }

        externalInputPlugin.holdKeyEvent(KeyEvent(anyKeyCode, keyToHold))

        assertEquals(expectedKeyCode.value, keyCode)
    }

    private fun assertPressedAction(expectedActionCode: Action, actionKey: Int){
        var actionCode: String? = null

        core.on(Event.DID_RECEIVE_INPUT_KEY.value) { bundle ->
            bundle?.let { actionCode = it.getString(EventData.INPUT_KEY_ACTION.value) }
        }

        externalInputPlugin.holdKeyEvent(KeyEvent(actionKey, anyKeyAction))

        assertEquals(expectedActionCode.value, actionCode)
    }
}