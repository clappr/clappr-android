package io.clappr.player.components

import android.view.OrientationEventListener.ORIENTATION_UNKNOWN
import androidx.test.core.app.ApplicationProvider
import io.clappr.player.base.BaseObject
import io.clappr.player.base.Event
import io.clappr.player.base.Options
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23], shadows = [ShadowLog::class])
class OrientationChangeListenerTest {

    @Before
    fun setup() {
        BaseObject.applicationContext = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun `should not trigger DID_CHANGE_SCREEN_ORIENTATION on first orientation change detection`() {
        val core = Core(Options())
        var didCall = false
        core.on(Event.DID_CHANGE_SCREEN_ORIENTATION.value) { didCall = true }

        val orientationChangeListener = OrientationChangeListener(core)
        orientationChangeListener.onOrientationChanged(0)

        assertFalse("should have triggered event", didCall)
    }

    @Test
    fun `should trigger DID_CHANGE_SCREEN_ORIENTATION with PORTRAIT when orientation changes to 0 degrees`() {
        val core = Core(Options())

        var orientation: Orientation? = null

        core.on(Event.DID_CHANGE_SCREEN_ORIENTATION.value) {
            orientation = it?.get("orientation") as Orientation?
        }

        val orientationChangeListener = orientationChangeListenerInitialized(core)
        orientationChangeListener.onOrientationChanged(0)

        assertEquals(Orientation.PORTRAIT, orientation)
    }

    @Test
    fun `should trigger DID_CHANGE_SCREEN_ORIENTATION with LANDSCAPE when orientation changes to 90 degrees`() {
        val core = Core(Options())

        var orientation: Orientation? = null

        core.on(Event.DID_CHANGE_SCREEN_ORIENTATION.value) {
            orientation = it?.get("orientation") as Orientation?
        }

        val orientationChangeListener = orientationChangeListenerInitialized(core)
        orientationChangeListener.onOrientationChanged(90)

        assertEquals(Orientation.LANDSCAPE, orientation)
    }

    @Test
    fun `should trigger DID_CHANGE_SCREEN_ORIENTATION with PORTRAIT when orientation changes to 180 degrees`() {
        val core = Core(Options())

        var orientation: Orientation? = null

        core.on(Event.DID_CHANGE_SCREEN_ORIENTATION.value) {
            orientation = it?.get("orientation") as Orientation?
        }

        val orientationChangeListener = orientationChangeListenerInitialized(core)
        orientationChangeListener.onOrientationChanged(180)

        assertEquals(Orientation.PORTRAIT, orientation)
    }

    @Test
    fun `should trigger DID_CHANGE_SCREEN_ORIENTATION with LANDSCAPE when orientation changes to 270 degrees`() {
        val core = Core(Options())

        var orientation: Orientation? = null

        core.on(Event.DID_CHANGE_SCREEN_ORIENTATION.value) {
            orientation = it?.get("orientation") as Orientation?
        }

        val orientationChangeListener = orientationChangeListenerInitialized(core)
        orientationChangeListener.onOrientationChanged(270)

        assertEquals(Orientation.LANDSCAPE, orientation)
    }

    @Test
    fun `should trigger DID_CHANGE_SCREEN_ORIENTATION with UNKNOWN when orientation cannot be determined`() {
        val core = Core(Options())

        var orientation: Orientation? = null

        core.on(Event.DID_CHANGE_SCREEN_ORIENTATION.value) {
            orientation = it?.get("orientation") as Orientation?
        }

        val orientationChangeListener = orientationChangeListenerInitialized(core)
        orientationChangeListener.onOrientationChanged(ORIENTATION_UNKNOWN)

        assertEquals(Orientation.UNKNOWN, orientation)
    }

    private fun orientationChangeListenerInitialized(core: Core): OrientationChangeListener =
        OrientationChangeListener(core).apply {
            onOrientationChanged(0)
        }
}