package io.clappr.player.components

import android.hardware.SensorManager.SENSOR_DELAY_NORMAL
import android.os.Bundle
import android.view.OrientationEventListener
import io.clappr.player.base.BaseObject.Companion.applicationContext
import io.clappr.player.base.Event.DID_CHANGE_SCREEN_ORIENTATION
import io.clappr.player.base.EventData.ORIENTATION

class OrientationChangeListener(private val core: Core) :
    OrientationEventListener(applicationContext, SENSOR_DELAY_NORMAL) {

    private var lastOrientation: Orientation? = null

    override fun onOrientationChanged(degrees: Int) {
        val orientation = Orientation.from(degrees)
        if (hasNotChanged(orientation)) {
            orientation?.let { lastOrientation = it }
            return
        }

        orientation?.let { lastOrientation = it }

        val bundle = Bundle().apply { putSerializable(ORIENTATION.value, orientation) }

        core.trigger(DID_CHANGE_SCREEN_ORIENTATION.value, bundle)
    }

    private fun hasNotChanged(orientation: Orientation?) =
        orientation == null || lastOrientation == null || lastOrientation == orientation

    override fun enable() {
        if (canDetectOrientation()) {
            super.enable()
        }
    }
}

enum class Orientation {
    PORTRAIT, LANDSCAPE;

    companion object {
        fun from(orientation: Int) = when (orientation) {
            0, 180 -> PORTRAIT
            90, 270 -> LANDSCAPE
            else -> null
        }
    }
}