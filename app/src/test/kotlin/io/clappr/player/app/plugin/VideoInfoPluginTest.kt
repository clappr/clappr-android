package io.clappr.player.app.plugin

import android.app.Application
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import io.clappr.player.BuildConfig
import io.clappr.player.app.R
import io.clappr.player.app.plugin.util.FakePlayback
import io.clappr.player.app.plugin.util.assertHidden
import io.clappr.player.app.plugin.util.assertShown
import io.clappr.player.base.BaseObject
import io.clappr.player.base.Event
import io.clappr.player.base.Options
import io.clappr.player.components.Core
import io.clappr.player.plugin.Loader
import io.clappr.player.plugin.UIPlugin
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [23], application = Application::class)
class VideoInfoPluginTest {

    private lateinit var core: Core

    private lateinit var videoInfoPlugin: VideoInfoPlugin

    private val source = "url"
    private val title = "title"
    private val subtitle = "subtitle"

    @Before
    fun setup() {
        BaseObject.context = ShadowApplication.getInstance().applicationContext

        Loader.registerPlayback(FakePlayback::class)
        Loader.registerPlugin(VideoInfoPlugin::class)

        val options = HashMap<String, Any>()
        options[VideoInfoPlugin.Option.TITLE.value] = title
        options[VideoInfoPlugin.Option.SUBTITLE.value] = subtitle

        core = Core(Loader(), Options(source = source, options = options))

        videoInfoPlugin = VideoInfoPlugin(core).apply {
            render()
        }

        core.load()
    }

    @Test
    fun shouldContainTitleAndSubtitleAfterRendering() {
        videoInfoPlugin.view.findViewById<TextView>(R.id.title_label).let {
            assertNotNull(it)
            assertEquals(title, it.text.toString())
        }

        videoInfoPlugin.view.findViewById<TextView>(R.id.subtitle_label).let {
            assertNotNull(it)
            assertEquals(subtitle, it.text.toString())
        }
    }

    @Test
    fun shouldShowTitleAndHideSubtitleOnEmbedded() {
        core.fullscreenState = Core.FullscreenState.EMBEDDED

        videoInfoPlugin.view.findViewById<TextView>(R.id.title_label).apply {
            assertEquals(View.VISIBLE, this.visibility)
        }

        videoInfoPlugin.view.findViewById<TextView>(R.id.subtitle_label).apply {
            assertEquals(View.GONE, this.visibility)
        }
    }

    @Test
    fun shouldShowTitleAndHideSubtitleOnFullScreen() {
        core.fullscreenState = Core.FullscreenState.FULLSCREEN

        videoInfoPlugin.view.findViewById<TextView>(R.id.title_label).apply {
            assertEquals(View.VISIBLE, this.visibility)
        }

        videoInfoPlugin.view.findViewById<TextView>(R.id.subtitle_label).apply {
            assertEquals(View.VISIBLE, this.visibility)
        }
    }

    @Test
    fun shouldShowViewAfterDidChangePlaybackEventIsTriggered() {
        val newPlayback = FakePlayback(source)
        core.activeContainer?.playback = newPlayback

        assertShown(videoInfoPlugin)
    }

    @Test
    fun shouldHideViewWhenDidCompleteEventIsTriggered() {
        core.activeContainer?.playback?.trigger(Event.DID_COMPLETE.value)

        assertHidden(videoInfoPlugin)
    }

    @Test
    fun shouldStopListeningOldPlaybackWhenDidChangePlaybackEventIsTriggered() {
        val oldPlayback = core.activePlayback

        assertEquals(UIPlugin.Visibility.VISIBLE, videoInfoPlugin.visibility)

        val newPlayback = FakePlayback(source)
        core.activeContainer?.playback = newPlayback

        oldPlayback?.trigger(Event.DID_COMPLETE.value)

        assertEquals(UIPlugin.Visibility.VISIBLE, videoInfoPlugin.visibility)
    }

    @Test
    fun shouldStopListeningOldPlaybackWhenPluginIsDestroyed() {
        val oldPlayback = core.activePlayback

        videoInfoPlugin.destroy()

        oldPlayback?.trigger(Event.DID_COMPLETE.value)

        assertEquals(UIPlugin.Visibility.VISIBLE, videoInfoPlugin.visibility)
    }
}