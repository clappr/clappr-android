package io.clappr.player.components

import android.hardware.SensorManager.SENSOR_DELAY_NORMAL
import android.os.Bundle
import android.view.OrientationEventListener
import io.clappr.player.base.BaseObject.Companion.applicationContext
import io.clappr.player.base.Event.DID_CHANGE_SCREEN_ORIENTATION
import io.clappr.player.base.EventData.ORIENTATION

class OrientationChangeListener(private val core: Core) :
    OrientationEventListener(applicationContext, SENSOR_DELAY_NORMAL) {

    var initialized = false

    override fun onOrientationChanged(degrees: Int) {
        if (!initialized) {
            initialized = true
            return
        }

        val bundle =
            Bundle().apply { putSerializable(ORIENTATION.value, Orientation.from(degrees)) }

        core.trigger(DID_CHANGE_SCREEN_ORIENTATION.value, bundle)
    }
}

enum class Orientation {
    PORTRAIT, LANDSCAPE, UNKNOWN;

    companion object {
        fun from(orientation: Int) = when (orientation) {
            0, 180 -> PORTRAIT
            90, 270 -> LANDSCAPE
            else -> UNKNOWN
        }
    }
}