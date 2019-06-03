package io.clappr.player.plugin

import android.widget.LinearLayout
import androidx.test.core.app.ApplicationProvider
import io.clappr.player.base.BaseObject
import io.clappr.player.base.ClapprOption
import io.clappr.player.base.Event
import io.clappr.player.base.Options
import io.clappr.player.components.Container
import io.clappr.player.components.Core
import io.clappr.player.components.Playback
import io.clappr.player.components.PlaybackSupportCheck
import io.clappr.player.shadows.ShadowUri
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23], shadows = [ShadowUri::class])
class PosterPluginTest {

    private lateinit var posterPlugin: PosterPlugin
    private lateinit var core: Core
    private lateinit var container: Container

    @Before
    fun setUp() {
        BaseObject.applicationContext = ApplicationProvider.getApplicationContext()

        Loader.register(PosterPlugin.entry)

        container = Container(Options())
        core = Core(Options())
        posterPlugin = PosterPlugin(container)

        core.activeContainer = container
        container.playback = PosterPluginTest.FakePlayback()
    }

    @After
    fun tearDown() {
        Loader.clearPlugins()
        ShadowUri.urlToParse = ""
    }

    @Test
    fun shouldUpdateImageUrlWhenUpdateOptionsIsTriggered() {
        val expectedImageUrl = "image_url"

        container.options = Options().apply {
            options[ClapprOption.POSTER.value] = expectedImageUrl
        }
        container.trigger(Event.REQUEST_POSTER_UPDATE.value)

        assertEquals(expectedImageUrl, ShadowUri.urlToParse)
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
        setupViewHidden(posterPlugin)

        container.playback?.trigger(Event.DID_STOP.value)

        assertVisibleView(posterPlugin)
    }

    @Test
    fun shouldShowPosterWhenCompleteIsTriggered() {
        setupViewHidden(posterPlugin)

        container.playback?.trigger(Event.DID_COMPLETE.value)

        assertVisibleView(posterPlugin)
    }

    @Test
    fun shouldHidePosterWhenCompleteIsTriggered() {
        setupViewVisible(posterPlugin)

        container.playback?.trigger(Event.PLAYING.value)

        assertHiddenView(posterPlugin)
    }

    @Test
    fun shouldStopListeningOldPlaybackWhenDidChangePlaybackEventIsTriggered() {
        val oldPlayback = container.playback
        setupViewHidden(posterPlugin)

        val newPlayback = PosterPluginTest.FakePlayback()
        container.playback = newPlayback

        oldPlayback?.trigger(Event.DID_COMPLETE.value)

        assertHiddenView(posterPlugin)
    }

    @Test
    fun shouldStopListeningOldPlaybackAfterDestroy() {
        val oldPlayback = container.playback
        setupViewHidden(posterPlugin)

        posterPlugin.destroy()

        oldPlayback?.trigger(Event.DID_COMPLETE.value)

        assertHiddenView(posterPlugin)
    }

    class FakePlayback(source: String = "aSource", mimeType: String? = null, options: Options = Options()) : Playback(source, mimeType, options, name, supportsSource) {
        companion object {
            const val name = "fakePlayback"
            val supportsSource: PlaybackSupportCheck = { _, _ -> true }
        }
    }
}