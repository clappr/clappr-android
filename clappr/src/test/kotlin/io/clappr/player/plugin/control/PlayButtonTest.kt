package io.clappr.player.plugin.control

import android.view.View
import io.clappr.player.BuildConfig
import io.clappr.player.base.BaseObject
import io.clappr.player.base.Event
import io.clappr.player.base.Options
import io.clappr.player.components.Container
import io.clappr.player.components.Core
import io.clappr.player.components.Playback
import io.clappr.player.components.PlaybackSupportInterface
import io.clappr.player.plugin.Loader
import io.clappr.player.plugin.UIPlugin
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [23])
class PlayButtonTest {

    private lateinit var core: Core
    private lateinit var container: Container

    private lateinit var playButton: PlayButton

    @Before
    fun setup() {
        BaseObject.context = ShadowApplication.getInstance().applicationContext

        container = Container(Loader(), Options())
        core = Core(Loader(), Options())
        container.playback = FakePlayback()

        playButton = PlayButton(core)

        core.activeContainer = container
    }

    @Test
    fun shouldPlayButtonBeHiddenWhenStalled() {
        setupFakePlayback(state = Playback.State.STALLED)
        playButton.render()

        assertEquals(View.GONE, playButton.view.visibility)
        assertEquals(UIPlugin.Visibility.HIDDEN, playButton.visibility)
    }

    @Test
    fun shouldPlayButtonBeVisibleWhilePlayingAndCanPause() {
        setupFakePlayback(state = Playback.State.PLAYING, canPause = true)
        playButton.render()

        assertEquals(View.VISIBLE, playButton.view.visibility)
        assertEquals(UIPlugin.Visibility.VISIBLE, playButton.visibility)
    }

    @Test
    fun shouldPlayButtonBeHideWhilePlayingAndCantPause() {
        setupFakePlayback(state = Playback.State.PLAYING, canPause = false)
        playButton.render()

        assertEquals(View.GONE, playButton.view.visibility)
        assertEquals(UIPlugin.Visibility.HIDDEN, playButton.visibility)
    }

    @Test
    fun shouldPlayButtonBeVisibleWhileNotPlayingOrStalled() {
        setupFakePlayback(state = Playback.State.PAUSED)
        playButton.render()

        assertEquals(View.VISIBLE, playButton.view.visibility)
        assertEquals(UIPlugin.Visibility.VISIBLE, playButton.visibility)
    }

    @Test
    fun shouldPlayButtonBeVisibleWhenClickAndStateIsPlaying() {
        setupFakePlayback(state = Playback.State.PLAYING, canPause = true)
        playButton.onClick()

        assertEquals(View.VISIBLE, playButton.view.visibility)
        assertEquals(UIPlugin.Visibility.VISIBLE, playButton.visibility)
        assertTrue(playButton.view.isSelected)
    }

    @Test
    fun shouldPlayButtonBeVisibleWhenClickAndStateIsPaused() {
        setupFakePlayback(state = Playback.State.PAUSED)
        playButton.onClick()

        assertEquals(View.VISIBLE, playButton.view.visibility)
        assertEquals(UIPlugin.Visibility.VISIBLE, playButton.visibility)
        assertFalse(playButton.view.isSelected)
    }


    @Test
    fun shouldShowPlayWhenComplete() {
        setupFakePlayback(state = Playback.State.PAUSED)

        core.activePlayback?.trigger(Event.DID_COMPLETE.value)

        assertEquals(View.VISIBLE, playButton.view.visibility)
        assertEquals(UIPlugin.Visibility.VISIBLE, playButton.visibility)
        assertFalse(playButton.view.isSelected)
    }

    private fun setupFakePlayback(state: Playback.State = Playback.State.PLAYING,
                                  canPause: Boolean = false) {
        container.playback = FakePlayback(playbackState = state, playbackCanPause = canPause)
    }

    class FakePlayback(source: String = "aSource", mimeType: String? = null, options: Options = Options(),
                       private val playbackState: Playback.State = State.PLAYING,
                       private val playbackCanPause: Boolean = false) : Playback(source, mimeType, options) {
        companion object : PlaybackSupportInterface {
            override val name: String = "fakePlayback"
            override fun supportsSource(source: String, mimeType: String?) = true
        }

        override val state: State
            get() = playbackState

        override val canPause: Boolean
            get() = playbackCanPause
    }

}