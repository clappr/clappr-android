package io.clappr.player.components

import android.view.OrientationEventListener.ORIENTATION_UNKNOWN
import androidx.test.core.app.ApplicationProvider
import io.clappr.player.base.BaseObject
import io.clappr.player.base.Event
import io.clappr.player.base.Options
import org.hamcrest.CoreMatchers.hasItems
import org.junit.Assert.*
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

        val orientationChangeListener = orientationChangeListenerInitialized(core, 90)
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

        val orientationChangeListener = orientationChangeListenerInitialized(core, 0)
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

        val orientationChangeListener = orientationChangeListenerInitialized(core, 90)
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

        val orientationChangeListener = orientationChangeListenerInitialized(core, 0)
        orientationChangeListener.onOrientationChanged(270)

        assertEquals(Orientation.LANDSCAPE, orientation)
    }

    @Test
    fun `should trigger DID_CHANGE_SCREEN_ORIENTATION when orientation changes multiple times`() {
        val core = Core(Options())

        val orientations: MutableList<Orientation?> = mutableListOf()

        core.on(Event.DID_CHANGE_SCREEN_ORIENTATION.value) {
            orientations += it?.get("orientation") as Orientation?
        }

        val orientationChangeListener = orientationChangeListenerInitialized(core, 0)
        orientationChangeListener.onOrientationChanged(270)
        orientationChangeListener.onOrientationChanged(0)

        assertThat(orientations, hasItems(Orientation.LANDSCAPE, Orientation.PORTRAIT))

    }

    @Test
    fun `should not trigger DID_CHANGE_SCREEN_ORIENTATION when orientation does not change`() {
        val core = Core(Options())

        val orientationChangeListener = orientationChangeListenerInitialized(core, 270)

        var orientation: Orientation? = null
        core.on(Event.DID_CHANGE_SCREEN_ORIENTATION.value) {
            orientation = it?.get("orientation") as Orientation?
        }

        orientationChangeListener.onOrientationChanged(270)


        assertNull(orientation)
    }

    @Test
    fun `should not trigger DID_CHANGE_SCREEN_ORIENTATION when orientation goes from something to unknown and back to the same`() {
        val core = Core(Options())

        val orientationChangeListener = orientationChangeListenerInitialized(core, 270)

        var orientation: Orientation? = null
        core.on(Event.DID_CHANGE_SCREEN_ORIENTATION.value) {
            orientation = it?.get("orientation") as Orientation?
        }

        orientationChangeListener.onOrientationChanged(ORIENTATION_UNKNOWN)
        orientationChangeListener.onOrientationChanged(270)


        assertNull(orientation)
    }

    @Test
    fun `should trigger DID_CHANGE_SCREEN_ORIENTATION when orientation changes while in unknown state`() {
        val core = Core(Options())

        val orientationChangeListener = orientationChangeListenerInitialized(core, 270)

        var orientation: Orientation? = null
        core.on(Event.DID_CHANGE_SCREEN_ORIENTATION.value) {
            orientation = it?.get("orientation") as Orientation?
        }

        orientationChangeListener.onOrientationChanged(ORIENTATION_UNKNOWN)
        orientationChangeListener.onOrientationChanged(0)


        assertEquals(Orientation.PORTRAIT, orientation)
    }


    private fun orientationChangeListenerInitialized(
        core: Core,
        initialDegrees: Int
    ): OrientationChangeListener =
        OrientationChangeListener(core).apply {
            onOrientationChanged(initialDegrees)
        }
}