package io.clappr.player.app.plugin

import android.app.Application
import android.widget.LinearLayout
import io.clappr.player.BuildConfig
import io.clappr.player.app.R
import io.clappr.player.app.plugin.util.FakePlayback
import io.clappr.player.app.plugin.util.assertHidden
import io.clappr.player.app.plugin.util.assertShown
import io.clappr.player.base.BaseObject
import io.clappr.player.base.Event
import io.clappr.player.base.Options
import io.clappr.player.components.Container
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
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [23], application = Application::class)
class NextVideoPluginTest {

    private lateinit var core: Core

    private lateinit var nextVideoPlugin: NextVideoPlugin

    private val source = "url"

    @Before
    fun setup() {
        BaseObject.applicationContext = ShadowApplication.getInstance().applicationContext

        core = Core(Loader(), Options(source = source))

        nextVideoPlugin = NextVideoPlugin(core)

        //Trigger Container change events
        val container = Container(core.loader, core.options)
        core.activeContainer  = container

        //Trigger Playback change events
        container.playback = FakePlayback("")

        nextVideoPlugin.render()
    }

    @Test
    fun shouldContainVideoItemsAfterRendering() {
        assertTrue(nextVideoPlugin.view.findViewById<LinearLayout>(R.id.video_list).childCount > 0)
    }

    @Test
    fun shouldHideAfterDidChangePlaybackEventIsTriggered() {
        assertHidden(nextVideoPlugin)
    }

    @Test
    fun shouldHideViewWhenWillPlayEventIsTriggered() {
        nextVideoPlugin.show()

        core.activeContainer?.playback?.trigger(Event.WILL_PLAY.value)

        assertHidden(nextVideoPlugin)
    }

    @Test
    fun shouldShowViewWhenDidCompleteEventIsTriggered() {
        core.activeContainer?.playback?.trigger(Event.DID_COMPLETE.value)

        assertShown(nextVideoPlugin)
    }

    @Test
    fun shouldShowViewWhenDidStopEventIsTriggered() {
        core.activeContainer?.playback?.trigger(Event.DID_STOP.value)

        assertShown(nextVideoPlugin)
    }

    @Test
    fun shouldHideViewWhenDidChangePlaybackEventIsTriggered() {
        nextVideoPlugin.show()

        val newPlayback = FakePlayback(source)
        core.activeContainer?.playback = newPlayback

        assertHidden(nextVideoPlugin)
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
}