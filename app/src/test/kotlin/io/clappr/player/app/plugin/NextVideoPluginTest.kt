package io.clappr.player.app.plugin

import android.app.Application
import android.view.View
import android.widget.LinearLayout
import io.clappr.player.BuildConfig
import io.clappr.player.app.R
import io.clappr.player.base.BaseObject
import io.clappr.player.base.Event
import io.clappr.player.base.Options
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
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [23], application = Application::class)
class NextVideoPluginTest {

    private lateinit var core: Core

    private lateinit var nextVideoPlugin: NextVideoPlugin

    private val source = "url"

    @Before
    fun setup() {
        BaseObject.context = ShadowApplication.getInstance().applicationContext

        Loader.registerPlayback(FakePlayback::class)
        Loader.registerPlugin(NextVideoPlugin::class)

        core = Core(Loader(), Options(source = source))

        nextVideoPlugin = NextVideoPlugin(core)

        core.load()
    }

    @Test
    fun shouldHideAfterDidChangePlaybackEventIsTriggered() {
        assertHidden()
    }

    @Test
    fun shouldHideViewWhenWillPlayEventIsTriggered() {
        nextVideoPlugin.show()

        core.activeContainer?.playback?.trigger(Event.WILL_PLAY.value)

        assertHidden()
    }

    @Test
    fun shouldShowViewWhenDidCompleteEventIsTriggered() {
        core.activeContainer?.playback?.trigger(Event.DID_COMPLETE.value)

        assertShown()
    }

    @Test
    fun shouldShowViewWhenDidStopEventIsTriggered() {
        core.activeContainer?.playback?.trigger(Event.DID_STOP.value)

        assertShown()
    }

    @Test
    fun shouldHideViewWhenDidChangePlaybackEventIsTriggered() {
        nextVideoPlugin.show()

        val newPlayback = FakePlayback(source)
        core.activeContainer?.playback = newPlayback

        assertHidden()
    }

    @Test
    fun shouldStopListeningOldPlaybackWhenDidChangePlaybackEventIsTriggered() {
        val oldPlayback = core.activePlayback

        assertEquals(UIPlugin.Visibility.HIDDEN, nextVideoPlugin.visibility)

        val newPlayback = FakePlayback(source)
        core.activeContainer?.playback = newPlayback

        oldPlayback?.trigger(Event.DID_COMPLETE.value)

        assertEquals(UIPlugin.Visibility.HIDDEN, nextVideoPlugin.visibility)
    }

    @Test
    fun shouldStopListeningOldPlaybackWhenPluginIsDestroyed() {
        val oldPlayback = core.activePlayback

        nextVideoPlugin.destroy()

        oldPlayback?.trigger(Event.DID_COMPLETE.value)

        assertEquals(UIPlugin.Visibility.HIDDEN, nextVideoPlugin.visibility)
    }

    @Test
    fun shouldContainVideoItemsAfterRender() {
        nextVideoPlugin.render()
        assertTrue(nextVideoPlugin.view.findViewById<LinearLayout>(R.id.video_list).childCount > 0)
    }

    private fun assertHidden() {
        assertEquals(UIPlugin.Visibility.HIDDEN, nextVideoPlugin.visibility)
        assertEquals(View.GONE, nextVideoPlugin.view.visibility)
    }

    private fun assertShown() {
        assertEquals(UIPlugin.Visibility.VISIBLE, nextVideoPlugin.visibility)
        assertEquals(View.VISIBLE, nextVideoPlugin.view.visibility)
    }

    internal class FakePlayback(source: String, mimeType: String? = null, options: Options = Options()) : Playback(source, mimeType, options) {
        companion object : PlaybackSupportInterface {
            override val name: String = "fakePlayback"
            override fun supportsSource(source: String, mimeType: String?) = true
        }
    }
}