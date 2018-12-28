package io.clappr.player.app.plugin

import android.app.Application
import android.widget.TextView
import io.clappr.player.BuildConfig
import io.clappr.player.app.R
import io.clappr.player.app.plugin.util.FakePlayback
import io.clappr.player.app.plugin.assertPlugin.assertUiPluginHidden
import io.clappr.player.app.plugin.assertPlugin.assertUiPluginShown
import io.clappr.player.base.BaseObject
import io.clappr.player.base.Event
import io.clappr.player.base.Options
import io.clappr.player.components.Container
import io.clappr.player.plugin.Loader
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [23], application = Application::class)
class PlaybackStatusPluginTest {

    private lateinit var container: Container

    private lateinit var playbackStatusPlugin: PlaybackStatusPlugin

    private val source = "url"

    @Before
    fun setup() {
        BaseObject.context = ShadowApplication.getInstance().applicationContext

        container = Container(Loader(), Options(source = source))

        playbackStatusPlugin = PlaybackStatusPlugin(container)

        //Trigger Playback change events
        container.playback = FakePlayback("")

        playbackStatusPlugin.render()
    }

    @Test
    fun shouldHideViewAfterDidChangePlaybackEventIsTriggered() {
        val newPlayback = FakePlayback(source)
        container.playback = newPlayback

        assertUiPluginHidden(playbackStatusPlugin)
    }

    @Test
    fun shouldShowViewWhenStallingEventIsTriggered() {
        assertLabelOnEvent(Event.STALLING.value)
    }

    @Test
    fun shouldShowViewWhenDidPauseEventIsTriggered() {
        assertLabelOnEvent(Event.DID_PAUSE.value)
    }

    @Test
    fun shouldShowViewWheDidStopEventIsTriggered() {
        assertLabelOnEvent(Event.DID_STOP.value)
    }

    @Test
    fun shouldShowViewWhenDidCompleteEventIsTriggered() {
        assertLabelOnEvent(Event.DID_COMPLETE.value)
    }

    @Test
    fun shouldShowViewWhenErrorEventIsTriggered() {
        assertLabelOnEvent(Event.ERROR.value)
    }

    @Test
    fun shouldStopListeningOldPlaybackWhenDidChangePlaybackEventIsTriggered() {
        val oldPlayback = container.playback

        assertUiPluginHidden(playbackStatusPlugin)

        val newPlayback = FakePlayback(source)
        container.playback = newPlayback

        oldPlayback?.trigger(Event.WILL_PLAY.value)

        assertUiPluginHidden(playbackStatusPlugin)
    }

    @Test
    fun shouldStopListeningOldPlaybackWhenPluginIsDestroyed() {
        val oldPlayback = container.playback

        playbackStatusPlugin.destroy()

        oldPlayback?.trigger(Event.WILL_PLAY.value)

        assertUiPluginHidden(playbackStatusPlugin)
    }

    private fun assertLabelOnEvent(eventName: String) {
        container.playback?.trigger(eventName)

        assertText(eventName)
        assertUiPluginShown(playbackStatusPlugin)
    }

    private fun assertText(text: String) {
        playbackStatusPlugin.view.findViewById<TextView>(R.id.status).let {
            assertNotNull(it)
            assertEquals(text, it.text.toString())
        }
    }
}