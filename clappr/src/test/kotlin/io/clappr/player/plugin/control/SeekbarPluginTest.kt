package io.clappr.player.plugin.control

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import io.clappr.player.BuildConfig
import io.clappr.player.base.*
import io.clappr.player.components.Container
import io.clappr.player.components.Core
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication
import kotlin.test.assertTrue
import io.clappr.player.components.Playback
import io.clappr.player.components.PlaybackSupportInterface
import io.clappr.player.plugin.*
import io.clappr.player.shadows.ClapprShadowView
import org.robolectric.internal.ShadowExtractor
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [23], shadows = [ClapprShadowView::class])
class SeekbarPluginTest {

    private lateinit var core: Core
    private lateinit var container: Container

    private lateinit var seekbarPlugin: SeekbarPlugin

    @Before
    fun setup() {
        BaseObject.context = ShadowApplication.getInstance().applicationContext

        container = Container(Loader(), Options())
        core = Core(Loader(), Options())

        seekbarPlugin = SeekbarPlugin(core)

        core.activeContainer = container

        container.playback = FakePlayback()
    }

    @Test
    fun shouldSetViewTouchListenerWhenRender() {
        val didTouchSeekbar = performTouchActionOnSeekbar(MotionEvent.ACTION_UP)
        assertTrue(didTouchSeekbar)
    }

    @Test
    fun shouldUpdateViewVisibilityToGoneWhenRenderIsCalledAndPlaybackIsIdle() {
        assertViewVisibilityOnRender(Playback.MediaType.VOD, Playback.State.IDLE, View.GONE)
    }

    @Test
    fun shouldUpdateViewVisibilityToGoneWhenRenderIsCalledAndPlaybackIsNone() {
        assertViewVisibilityOnRender(Playback.MediaType.VOD, Playback.State.NONE, View.GONE)
    }

    @Test
    fun shouldUpdateViewVisibilityToGoneWhenRenderIsCalledAndVideoIsLive() {
        assertViewVisibilityOnRender(Playback.MediaType.LIVE, Playback.State.PLAYING, View.GONE)
    }

    @Test
    fun shouldUpdateViewVisibilityToGoneWhenRenderIsCalledAndVideoIsUnknown() {
        assertViewVisibilityOnRender(Playback.MediaType.UNKNOWN, Playback.State.PLAYING, View.GONE)
    }

    @Test
    fun shouldUpdateViewVisibilityToVisibleWhenRenderIsCalledAndVideoIsVOD() {
        seekbarPlugin.view.visibility = View.GONE
        assertViewVisibilityOnRender(Playback.MediaType.VOD, Playback.State.PLAYING, View.VISIBLE)
    }

    @Test
    fun shouldTriggerDidUpdateInteractingEventWhenTouchDownEventHappens() {
        var didUpdateInteractingCalled = false

        core.listenTo(core, InternalEvent.DID_UPDATE_INTERACTING.value,
                Callback.wrap { didUpdateInteractingCalled = true })

        val didTouchSeekbar = performTouchActionOnSeekbar(MotionEvent.ACTION_DOWN)

        assertTrue(didTouchSeekbar)
        assertTrue(didUpdateInteractingCalled)
    }

    @Test
    fun shouldHideSeekbarWhenDidCompleteEventHappens() {
        setupViewVisible(seekbarPlugin)

        core.activePlayback?.trigger(Event.DID_COMPLETE.value)

        assertHiddenView(seekbarPlugin)
    }

    @Test
    fun shouldUpdatePositionBarViewAndScrubberViewWhenTouchActionDownHappens() {
        val expectedPositionBarWidth = 50
        val expectedScrubberViewX = 46.0f

        setupViewWidth(500, 8)

        val didTouchSeekbar = performTouchActionOnSeekbar(MotionEvent.ACTION_DOWN, 50F)

        assertTrue(didTouchSeekbar)
        assertEquals(expectedPositionBarWidth, seekbarPlugin.positionBar.layoutParams.width)
        assertEquals(expectedScrubberViewX, seekbarPlugin.scrubberView.x)
    }

    @Test
    fun shouldUpdatePositionBarViewAndScrubberViewWhenTouchActionMoveHappens() {
        val expectedPositionBarWidth = 100
        val expectedScrubberViewX = 96.0f

        setupViewWidth(500, 8)

        val didTouchSeekbar = performTouchActionOnSeekbar(MotionEvent.ACTION_MOVE, 100F)

        assertTrue(didTouchSeekbar)
        assertEquals(expectedPositionBarWidth, seekbarPlugin.positionBar.layoutParams.width)
        assertEquals(expectedScrubberViewX, seekbarPlugin.scrubberView.x)
    }

    @Test
    fun shouldNotUpdatePositionBarViewAndScrubberViewWhenTouchActionCancelHappens() {
        assertActionNotUpdateView(MotionEvent.ACTION_CANCEL)
    }

    @Test
    fun shouldNotUpdatePositionBarViewAndScrubberViewWhenTouchActionUpHappens() {
        assertActionNotUpdateView(MotionEvent.ACTION_UP)
    }

    @Test
    fun shouldUpdateViewVisibilityToGoneWhenUserTouchSeekbarAndPlaybackIsIdle() {
        assertViewVisibilityWhenTouchEventHappens(View.GONE, Playback.MediaType.VOD, Playback.State.IDLE)
    }

    @Test
    fun shouldUpdateViewVisibilityToGoneWhenUserTouchSeekbarAndPlaybackIsNone() {
        assertViewVisibilityWhenTouchEventHappens(View.GONE, Playback.MediaType.VOD, Playback.State.NONE)
    }

    @Test
    fun shouldUpdateViewVisibilityToGoneWhenUserTouchSeekbarAndVideoIsLive() {
        assertViewVisibilityWhenTouchEventHappens(View.GONE, Playback.MediaType.LIVE, Playback.State.PLAYING)
    }

    @Test
    fun shouldUpdateViewVisibilityToGoneWhenUserTouchSeekbarAndVideoIsUnknown() {
        assertViewVisibilityWhenTouchEventHappens(View.GONE, Playback.MediaType.UNKNOWN, Playback.State.PLAYING)
    }

    @Test
    fun shouldUpdateViewVisibilityToVisibleWhenUserTouchSeekbarAndVideoIsVOD() {
        assertViewVisibilityWhenTouchEventHappens(View.VISIBLE, Playback.MediaType.VOD, Playback.State.PLAYING, View.GONE)
    }

    @Test
    fun shouldStopListeningOldPlaybackAfterDestroy() {
        setupViewVisible(seekbarPlugin)
        val oldPlayback = container.playback

        seekbarPlugin.destroy()

        oldPlayback?.trigger(Event.DID_COMPLETE.value)

        assertVisibleView(seekbarPlugin)
    }

    @Test
    fun shouldRemoveTouchListenerWhenViewIsDestroyed() {
        val motionEvent = MotionEvent.obtain(
                0, 0, MotionEvent.ACTION_DOWN, 0.0f, 0.0f, 0)

        seekbarPlugin.render()
        assertTrue(seekbarPlugin.view.dispatchTouchEvent(motionEvent))

        seekbarPlugin.destroy()
        assertFalse(seekbarPlugin.view.dispatchTouchEvent(motionEvent))
    }

    @Test
    fun shouldUpdateBufferedBarWhenEventHappens() {
        val expectedBufferedBarWidth = 250
        val expectedPercentage = 50.0
        val bundle = Bundle().apply { putDouble("percentage", expectedPercentage) }

        setupViewWidth(500, 8)

        core.activePlayback?.trigger(Event.DID_UPDATE_BUFFER.value, bundle)

        assertEquals(expectedBufferedBarWidth, seekbarPlugin.bufferedBar.layoutParams.width)
    }

    @Test
    fun shouldUpdateBufferedBarWithoutBundleWhenEventHappens() {
        val expectedBufferedBarWidth = 0

        setupViewWidth(500, 8)
        seekbarPlugin.bufferedBar.layoutParams.apply { width = 100 }

        core.activePlayback?.trigger(Event.DID_UPDATE_BUFFER.value)

        assertEquals(expectedBufferedBarWidth, seekbarPlugin.bufferedBar.layoutParams.width)
    }

    @Test
    fun shouldSeekWhenStopDrag() {
        val expectedSeekTime = 60

        setupViewWidth(500, 8)
        setupFakePlayback(duration = 120.0, position = 10.0)

        performTouchActionOnSeekbar(MotionEvent.ACTION_UP, 250.0f)

        assertEquals(expectedSeekTime, (container.playback as FakePlayback).seekTime)
    }

    @Test
    fun shouldNotSeekWhenUserStopDragInTheOldPosition() {
        val expectedSeekTime = 0

        setupViewWidth(500, 8)
        setupFakePlayback(duration = 120.0, position = 60.0)

        performTouchActionOnSeekbar(MotionEvent.ACTION_UP, 250.0f)

        assertEquals(expectedSeekTime, (container.playback as FakePlayback).seekTime)
    }

    @Test
    fun shouldUpdateViewsWhenUpdatePositionEventHappens() {
        val expectedPositionBarWidth = 250
        val expectedScrubberViewX = 246.0f

        val expectedPercentage = 50.0
        val bundle = Bundle().apply { putDouble("percentage", expectedPercentage) }

        setupViewWidth(500, 8)

        core.activePlayback?.trigger(Event.DID_UPDATE_POSITION.value, bundle)

        assertEquals(expectedPositionBarWidth, seekbarPlugin.positionBar.layoutParams.width)
        assertEquals(expectedScrubberViewX, seekbarPlugin.scrubberView.x)
    }

    @Test
    fun shouldUpdateViewsWithoutBundleWhenUpdatePositionEventHappens() {
        val expectedPositionBarWidth = 0
        val expectedScrubberViewX = 0f

        setupViewWidth(500, 8)

        seekbarPlugin.positionBar.layoutParams.width = 100
        seekbarPlugin.scrubberView.x = 100f

        core.activePlayback?.trigger(Event.DID_UPDATE_POSITION.value)

        assertEquals(expectedPositionBarWidth, seekbarPlugin.positionBar.layoutParams.width)
        assertEquals(expectedScrubberViewX, seekbarPlugin.scrubberView.x)
    }

    @Test
    fun shouldNotUpdateViewsWhenUpdatePositionEventHappensButUserIsDragging() {
        val expectedPositionBarWidth = 0
        val expectedScrubberViewX = 0f

        val expectedPercentage = 50.0
        val bundle = Bundle().apply { putDouble("percentage", expectedPercentage) }

        setupViewWidth(500, 8)

        seekbarPlugin.updateDrag(0.0f)
        core.activePlayback?.trigger(Event.DID_UPDATE_POSITION.value, bundle)

        assertEquals(expectedPositionBarWidth, seekbarPlugin.positionBar.layoutParams.width)
        assertEquals(expectedScrubberViewX, seekbarPlugin.scrubberView.x)
    }

    @Test
    fun shouldStopListeningOldPlaybackWhenDidChangePlaybackEventIsTriggered() {
        val oldPlayback = container.playback

        assertEquals(View.VISIBLE, seekbarPlugin.view.visibility)

        container.playback = FakePlayback()

        oldPlayback?.trigger(Event.DID_COMPLETE.value)

        assertEquals(View.VISIBLE, seekbarPlugin.view.visibility)
    }

    private fun assertViewVisibilityWhenTouchEventHappens(expectedViewVisibility: Int,
                                                          mediaType: Playback.MediaType,
                                                          playbackState: Playback.State,
                                                          currentViewVisibility : Int = View.VISIBLE) {
        val motionEvent = MotionEvent.obtain(
                0, 0, MotionEvent.ACTION_DOWN, 0.0f, 0.0f, 0)

        seekbarPlugin.render()
        seekbarPlugin.view.visibility = currentViewVisibility

        setupFakePlayback(mediaType, playbackState)
        seekbarPlugin.view.dispatchTouchEvent(motionEvent)

        assertEquals(expectedViewVisibility, seekbarPlugin.view.visibility)
    }

    private fun performTouchActionOnSeekbar(motionAction: Int, touchXPosition: Float = 0F): Boolean {
        val motionEvent = MotionEvent.obtain(
                0, 0, motionAction, touchXPosition, 0.0f, 0)

        seekbarPlugin.render()

        return seekbarPlugin.view.dispatchTouchEvent(motionEvent)
    }

    private fun assertViewVisibilityOnRender(playbackMediaType: Playback.MediaType,
                                             playbackState: Playback.State,
                                             expectedViewVisibility: Int) {
        setupFakePlayback(playbackMediaType, playbackState)
        seekbarPlugin.render()
        assertEquals(expectedViewVisibility, seekbarPlugin.view.visibility)
    }

    private fun setupViewWidth(backgroundViewWidth: Int, scrubberViewWidth: Int) {
        (ShadowExtractor.extract(seekbarPlugin.backgroundView) as ClapprShadowView).viewWidth = backgroundViewWidth
        (ShadowExtractor.extract(seekbarPlugin.scrubberView) as ClapprShadowView).viewWidth = scrubberViewWidth
    }

    private fun assertActionNotUpdateView(event: Int) {
        val expectedPositionBarWidth = 100
        val expectedScrubberViewX = 96.0f

        setupViewWidth(500, 8)
        seekbarPlugin.updatePosition(20.0, false)

        assertEquals(expectedPositionBarWidth, seekbarPlugin.positionBar.layoutParams.width)
        assertEquals(expectedScrubberViewX, seekbarPlugin.scrubberView.x)

        val didTouchSeekbar = performTouchActionOnSeekbar(event, 200F)

        assertTrue(didTouchSeekbar)
        assertEquals(expectedPositionBarWidth, seekbarPlugin.positionBar.layoutParams.width)
        assertEquals(expectedScrubberViewX, seekbarPlugin.scrubberView.x)
    }

    private fun setupFakePlayback(mediaType: Playback.MediaType = Playback.MediaType.VOD,
                                  state: Playback.State = Playback.State.PLAYING,
                                  duration: Double = 0.0,
                                  position: Double = 0.0) {
        (container.playback as FakePlayback).apply {
            currentMediaType = mediaType
            currentPlaybackState = state
            currentDuration = duration
            currentPosition = position
        }
    }

    class FakePlayback(source: String = "aSource", mimeType: String? = null, options: Options = Options()) : Playback(source, mimeType, options) {
        companion object : PlaybackSupportInterface {
            override val name: String = "fakePlayback"
            override fun supportsSource(source: String, mimeType: String?) = true
        }

        var currentMediaType: MediaType = MediaType.VOD
        var currentPlaybackState: Playback.State = State.PLAYING
        var currentDuration: Double = 0.0
        var currentPosition: Double = 0.0
        var seekTime: Int = 0

        override val state: State
            get() = currentPlaybackState

        override val mediaType: MediaType
            get() = currentMediaType

        override val duration: Double
            get() = currentDuration

        override val position: Double
            get() = currentPosition

        override fun seek(seconds: Int): Boolean {
            seekTime = seconds
            return super.seek(seconds)
        }
    }
}
