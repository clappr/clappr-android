package io.clappr.player.plugin.core.externalinput

import android.view.KeyEvent

interface ExternalInputDevice {
    fun holdKeyEvent(keyCode: Int, event: KeyEvent)
}