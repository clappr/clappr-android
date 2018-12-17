package io.clappr.player.plugin

import android.view.View
import android.widget.LinearLayout
import io.clappr.player.BuildConfig
import io.clappr.player.base.BaseObject
import io.clappr.player.base.Event
import io.clappr.player.base.Options
import io.clappr.player.components.Container
import io.clappr.player.components.Core
import io.clappr.player.components.Playback
import io.clappr.player.components.PlaybackSupportInterface
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [23])
class LoadingPluginTest {

    private lateinit var loadingPlugin: LoadingPlugin
    private lateinit var core: Core
    private lateinit var container: Container

    @Before
    fun setUp() {
        BaseObject.applicationContext = ShadowApplication.getInstance().applicationContext

        container = Container(Loader(), Options())
        core = Core(Loader(), Options())
        loadingPlugin = LoadingPlugin(container)

        core.activeContainer = container
        container.playback = FakePlayback()
    }

    @Test
    fun shouldInitWithStateEnabled() {
        assertEquals(Plugin.State.ENABLED, loadingPlugin.state)
    }

    @Test
    fun shouldStopListeningOldPlaybackWhenDidChangePlaybackEventIsTriggered() {
        val oldPlayback = container.playback

        assertEquals(UIPlugin.Visibility.HIDDEN, loadingPlugin.visibility)

        val newPlayback = FakePlayback()
        container.playback = newPlayback

        oldPlayback?.trigger(Event.STALLING.value)

        assertEquals(UIPlugin.Visibility.HIDDEN, loadingPlugin.visibility)
    }

    @Test
    fun shouldSetupSpinnerLayout() {
        val expectedLayoutParams = LinearLayout.LayoutParams.MATCH_PARENT
        val expectedAlpha = 0.7f

        val spinnerLayout = loadingPlugin.view as? LinearLayout

        assertEquals(expectedLayoutParams, spinnerLayout?.layoutParams?.height)
        assertEquals(expectedLayoutParams, spinnerLayout?.layoutParams?.width)
        assertEquals(expectedAlpha, spinnerLayout?.alpha)
    }

    @Test
    fun shouldStartAnimationWhenStallingIsTriggered() {
        setupViewHidden()

        container.playback?.trigger(Event.STALLING.value)

        assertVisibleView(loadingPlugin)
    }

    @Test
    fun shouldStartAnimationWhenWillPlayIsTriggered() {
        setupViewHidden()

        container.playback?.trigger(Event.WILL_PLAY.value)

        assertVisibleView(loadingPlugin)
    }

    @Test
    fun shouldStopAnimationWhenPlayingIsTriggered() {
        setupViewVisible(loadingPlugin)

        container.playback?.trigger(Event.PLAYING.value)

        assertHiddenView()
    }

    @Test
    fun shouldStopAnimationWhenDidStopIsTriggered() {
        setupViewVisible(loadingPlugin)

        container.playback?.trigger(Event.DID_STOP.value)

        assertHiddenView()
    }

    @Test
    fun shouldStopAnimationWhenDidPauseIsTriggered() {
        setupViewVisible(loadingPlugin)

        container.playback?.trigger(Event.DID_PAUSE.value)

        assertHiddenView()
    }

    @Test
    fun shouldStopAnimationWhenDidCompleteIsTriggered() {
        setupViewVisible(loadingPlugin)

        container.playback?.trigger(Event.DID_COMPLETE.value)

        assertHiddenView()
    }

    @Test
    fun shouldStopAnimationWhenErrorTriggered() {
        setupViewVisible(loadingPlugin)

        container.playback?.trigger(Event.ERROR.value)

        assertHiddenView()
    }

    @Test
    fun shouldStopListeningOldPlaybackAfterDestroy() {
        val oldPlayback = container.playback
        setupViewHidden()

        loadingPlugin.destroy()

        oldPlayback?.trigger(Event.WILL_PLAY.value)

        assertHiddenView()
    }

    private fun setupViewHidden() {
        loadingPlugin.view?.visibility = View.INVISIBLE
        loadingPlugin.visibility = UIPlugin.Visibility.HIDDEN
    }

    private fun assertHiddenView() {
        assertEquals(View.INVISIBLE, loadingPlugin.view?.visibility)
        assertEquals(UIPlugin.Visibility.HIDDEN, loadingPlugin.visibility)
    }

    class FakePlayback(source: String = "aSource", mimeType: String? = null, options: Options = Options()) : Playback(source, mimeType, options) {
        companion object : PlaybackSupportInterface {
            override val name: String = "fakePlayback"
            override fun supportsSource(source: String, mimeType: String?) = true
        }
    }
}