package io.clappr.player.plugin

import android.view.View
import android.widget.LinearLayout
import io.clappr.player.BuildConfig
import io.clappr.player.base.BaseObject
import io.clappr.player.base.ClapprOption
import io.clappr.player.base.Event
import io.clappr.player.base.Options
import io.clappr.player.components.Container
import io.clappr.player.components.Core
import io.clappr.player.components.Playback
import io.clappr.player.components.PlaybackSupportInterface
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [23])
class PosterPluginTest {

    private lateinit var posterPlugin: PosterPlugin
    private lateinit var core: Core
    private lateinit var container: Container

    @Before
    fun setUp() {
        BaseObject.context = ShadowApplication.getInstance().applicationContext

        Loader.registerPlugin(PosterPlugin::class)

        container = Container(Loader(), Options())
        core = Core(Loader(), Options())
        posterPlugin = PosterPlugin(container)

        core.activeContainer = container
        container.playback = PosterPluginTest.FakePlayback()
    }

    @After
    fun tearDown() {
        Loader.clearPlugins()
    }

    @Test
    fun shouldUpdateImageUrlWhenUpdateOptionsIsTriggered() {
        val expectedImageUrl = "image_url"

        val option = Options()
        option[ClapprOption.POSTER.value] = expectedImageUrl
        container.options = option

        assertEquals(expectedImageUrl, posterPlugin.posterImageUrl)
    }

    @Test
    fun shouldSetupPosterLayout() {
        val expectedLayoutParams = LinearLayout.LayoutParams.MATCH_PARENT

        val spinnerLayout = posterPlugin.view as? LinearLayout

        kotlin.test.assertEquals(expectedLayoutParams, spinnerLayout?.layoutParams?.height)
        kotlin.test.assertEquals(expectedLayoutParams, spinnerLayout?.layoutParams?.width)
    }

    @Test
    fun shouldShowPosterWhenStopIsTriggered() {
        setupViewHidden()

        container.playback?.trigger(Event.DID_STOP.value)

        assertVisibleView()
    }

    @Test
    fun shouldShowPosterWhenCompleteIsTriggered() {
        setupViewHidden()

        container.playback?.trigger(Event.DID_COMPLETE.value)

        assertVisibleView()
    }

    @Test
    fun shouldHidePosterWhenCompleteIsTriggered() {
        setupViewVisible()

        container.playback?.trigger(Event.PLAYING.value)

        assertHiddenView()
    }

    @Test
    fun shouldStopListeningOldPlaybackWhenDidChangePlaybackEventIsTriggered() {
        val oldPlayback = container.playback
        setupViewHidden()

        val newPlayback = PosterPluginTest.FakePlayback()
        container.playback = newPlayback

        oldPlayback?.trigger(Event.DID_COMPLETE.value)

        assertHiddenView()
    }

    @Test
    fun shouldStopListeningOldPlaybackAfterDestroy() {
        val oldPlayback = container.playback
        setupViewHidden()

        posterPlugin.destroy()

        oldPlayback?.trigger(Event.DID_COMPLETE.value)

        assertHiddenView()
    }

    private fun setupViewVisible() {
        posterPlugin.view?.visibility = View.VISIBLE
        posterPlugin.visibility = UIPlugin.Visibility.VISIBLE
    }

    private fun assertVisibleView() {
        kotlin.test.assertEquals(View.VISIBLE, posterPlugin.view?.visibility)
        kotlin.test.assertEquals(UIPlugin.Visibility.VISIBLE, posterPlugin.visibility)
    }

    private fun setupViewHidden() {
        posterPlugin.view?.visibility = View.GONE
        posterPlugin.visibility = UIPlugin.Visibility.HIDDEN
    }

    private fun assertHiddenView() {
        kotlin.test.assertEquals(View.GONE, posterPlugin.view?.visibility)
        kotlin.test.assertEquals(UIPlugin.Visibility.HIDDEN, posterPlugin.visibility)
    }
    
    class FakePlayback(source: String = "aSource", mimeType: String? = null, options: Options = Options()) : Playback(source, mimeType, options) {
        companion object : PlaybackSupportInterface {
            override val name: String = "fakePlayback"
            override fun supportsSource(source: String, mimeType: String?) = true
        }
    }
}