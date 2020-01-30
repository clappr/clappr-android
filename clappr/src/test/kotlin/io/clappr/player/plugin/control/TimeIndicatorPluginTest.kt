package io.clappr.player.plugin.control

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import io.clappr.player.base.*
import io.clappr.player.components.Container
import io.clappr.player.components.Core
import io.clappr.player.components.Playback
import io.clappr.player.components.PlaybackSupportCheck
import io.clappr.player.plugin.UIPlugin
import io.clappr.player.plugin.assertVisibleView
import io.clappr.player.plugin.setupViewVisible
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class TimeIndicatorPluginTest {

    private lateinit var timeIndicatorPlugin: TimeIndicatorPlugin
    private lateinit var core: Core
    private lateinit var container: Container

    @Before
    fun setUp() {
        BaseObject.applicationContext = ApplicationProvider.getApplicationContext()

        container = Container(Options())
        core = Core(Options())

        timeIndicatorPlugin = TimeIndicatorPlugin(core)

        core.activeContainer = container

        container.playback = FakePlayback()
    }

    @Test
    fun shouldNotLoadItselfInChromelessMode() {
        core = Core(Options(options = hashMapOf(ClapprOption.CHROMELESS.value to true)))

        assertNull(TimeIndicatorPlugin.entry.factory(core))
    }

    @Test
    fun shouldShowTimeIndicatorWhenPlaybackMediaTypeIsVODAndPlaybackIsNotIdle() {
        setupFakePlayback(Playback.MediaType.VOD, Playback.State.PLAYING)

        assertNotNull(timeIndicatorPlugin.view, "Time Indicator View should not be null")
        assertEquals(View.VISIBLE, timeIndicatorPlugin.view?.visibility)
    }

    @Test
    fun shouldNotShowTimeIndicatorWhenPlaybackMediaTypeIsLive() {
        setupFakePlayback(Playback.MediaType.LIVE, Playback.State.PLAYING)

        assertNotNull(timeIndicatorPlugin.view, "Time Indicator View should not be null")
        assertEquals(View.GONE, timeIndicatorPlugin.view?.visibility)
    }

    @Test
    fun shouldNotShowTimeIndicatorWhenPlaybackMediaTypeIsUnknown() {
        setupFakePlayback(Playback.MediaType.UNKNOWN, Playback.State.PLAYING)

        assertNotNull(timeIndicatorPlugin.view, "Time Indicator View should not be null")
        assertEquals(View.GONE, timeIndicatorPlugin.view?.visibility)
    }

    @Test
    fun shouldNotShowTimeIndicatorWhenPlaybackIsIdle() {
        setupFakePlayback(Playback.MediaType.VOD, Playback.State.IDLE)

        assertNotNull(timeIndicatorPlugin.view, "Time Indicator View should not be null")
        assertEquals(View.GONE, timeIndicatorPlugin.view?.visibility)
    }

    @Test
    fun shouldUpdateTextWhenPositionUpdateEventHappens() {
        val expectedTime = "10:00 / 20:00"
        val expectedPosition = 600.0
        val expectedDuration = 1200.0

        setupFakePlayback(duration = expectedDuration)

        val bundle = Bundle().apply { putDouble("time", expectedPosition) }

        container.playback?.trigger(Event.DID_UPDATE_POSITION.value, bundle)

        assertNotNull(timeIndicatorPlugin.view, "Time Indicator View should not be null")
        assertEquals(expectedTime,  (timeIndicatorPlugin.view as TextView).text)
    }

    @Test
    fun shouldUseDefaultPositionValueWhenBundleDoNotHaveTimeInformation() {
        val expectedTime = "00:00 / 20:00"
        val expectedDuration = 1200.0

        setupFakePlayback(duration = expectedDuration)

        container.playback?.trigger(Event.DID_UPDATE_POSITION.value, Bundle())

        assertNotNull(timeIndicatorPlugin.view, "Time Indicator View should not be null")
        assertEquals(expectedTime,  (timeIndicatorPlugin.view as TextView).text)
    }

    @Test
    fun shouldUseDefaultPositionValueWhenBundleIsNull() {
        val expectedTime = "00:00 / 20:00"
        val expectedDuration = 1200.0

        setupFakePlayback(duration = expectedDuration)

        container.playback?.trigger(Event.DID_UPDATE_POSITION.value, null)

        assertNotNull(timeIndicatorPlugin.view, "Time Indicator View should not be null")
        assertEquals(expectedTime,  (timeIndicatorPlugin.view as TextView).text)
    }

    @Test
    fun shouldUpdateTextVisibilityWhenPositionUpdateEventHappens() {
        setupFakePlayback(Playback.MediaType.VOD, Playback.State.PLAYING)

        assertEquals(View.VISIBLE, timeIndicatorPlugin.view?.visibility)

        (container.playback as FakePlayback).apply {
            currentMediaType = Playback.MediaType.LIVE
            currentPlaybackState = Playback.State.PLAYING
        }

        container.playback?.trigger(Event.DID_UPDATE_POSITION.value)

        assertEquals(View.GONE, timeIndicatorPlugin.view?.visibility)
    }

    @Test
    fun shouldUpdateTextVisibilityWhenDidChangeActiveContainerEventHappens() {
        setupFakePlayback(Playback.MediaType.VOD, Playback.State.PLAYING)

        assertEquals(View.VISIBLE, timeIndicatorPlugin.view?.visibility)

        timeIndicatorPlugin = TimeIndicatorPlugin(core)

        (container.playback as FakePlayback).apply {
            currentMediaType = Playback.MediaType.LIVE
            currentPlaybackState = Playback.State.PLAYING
        }

        core.trigger(InternalEvent.DID_CHANGE_ACTIVE_PLAYBACK.value)

        assertEquals(View.GONE, timeIndicatorPlugin.view?.visibility)
    }

    @Test
    fun shouldUpdateTextVisibilityWhenDidChangePlaybackEventHappens() {
        setupFakePlayback(Playback.MediaType.VOD, Playback.State.PLAYING)

        assertEquals(View.VISIBLE, timeIndicatorPlugin.view?.visibility)

        (container.playback as FakePlayback).apply {
            currentMediaType = Playback.MediaType.LIVE
            currentPlaybackState = Playback.State.PLAYING
        }

        container.trigger(InternalEvent.DID_CHANGE_PLAYBACK.value)

        assertEquals(View.GONE, timeIndicatorPlugin.view?.visibility)
    }

    @Test
    fun shouldHideTimeIndicatorWhenDidCompleteEventHappens() {
        setupFakePlayback()

        container.playback?.trigger(Event.DID_COMPLETE.value)

        assertEquals(UIPlugin.Visibility.HIDDEN, timeIndicatorPlugin.visibility)
        assertEquals(View.GONE, timeIndicatorPlugin.view?.visibility)
    }

    @Test
    fun shouldChangeTimeIndicatorLayoutParamsOnRender() {
        val height = 32
        val width = ViewGroup.LayoutParams.WRAP_CONTENT
        val expectedLayoutParams = LinearLayout.LayoutParams(width, height)

        timeIndicatorPlugin.render()

        assertEquals(expectedLayoutParams.height, timeIndicatorPlugin.view?.layoutParams?.height)
        assertEquals(expectedLayoutParams.width, timeIndicatorPlugin.view?.layoutParams?.width)
    }

    @Test
    fun shouldChangeTimeIndicatorTextOnRender() {
        val expectedTimeIndicatorText = "00:00 / 00:00"

        timeIndicatorPlugin.render()

        assertEquals(expectedTimeIndicatorText, (timeIndicatorPlugin.view as TextView).text)
    }

    @Test
    fun shouldStopListeningOldPlaybackAfterDestroy() {
        setupViewVisible(timeIndicatorPlugin)
        val oldPlayback = container.playback

        timeIndicatorPlugin.destroy()

        oldPlayback?.trigger(Event.DID_COMPLETE.value)

        assertVisibleView(timeIndicatorPlugin)
    }

    @Test
    fun shouldStopListeningOldPlaybackWhenDidChangePlaybackEventIsTriggered() {
        val oldPlayback = container.playback

        assertEquals(View.VISIBLE, timeIndicatorPlugin.view?.visibility)

        container.playback = FakePlayback()

        oldPlayback?.trigger(Event.DID_COMPLETE.value)

        assertEquals(View.VISIBLE, timeIndicatorPlugin.view?.visibility)
    }

    private fun setupFakePlayback(mediaType: Playback.MediaType = Playback.MediaType.VOD,
                                  state: Playback.State = Playback.State.PLAYING,
                                  duration: Double = 0.0) {
        container.playback = FakePlayback().apply {
            currentMediaType = mediaType
            currentPlaybackState = state
            currentDuration = duration
        }
    }

    class FakePlayback(source: String = "aSource", mimeType: String? = null, options: Options = Options()) : Playback(source, mimeType, options, name, supportsSource) {
        companion object {
            const val name = "fakePlayback"
            val supportsSource: PlaybackSupportCheck = { _, _ -> true }
        }

        var currentMediaType: MediaType = MediaType.VOD
        var currentPlaybackState: State = State.PLAYING
        var currentDuration: Double = -1.0

        override val state: State
            get() = currentPlaybackState

        override val mediaType: MediaType
            get() = currentMediaType

        override val duration: Double
            get() = currentDuration
    }
}
