package io.clappr.player.app.plugin

import android.app.Application
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.test.core.app.ApplicationProvider
import io.clappr.player.app.R
import io.clappr.player.app.plugin.assertPlugin.assertUiPluginHidden
import io.clappr.player.app.plugin.assertPlugin.assertUiPluginShown
import io.clappr.player.app.plugin.util.FakePlayback
import io.clappr.player.base.BaseObject
import io.clappr.player.base.Event
import io.clappr.player.base.Options
import io.clappr.player.components.Container
import io.clappr.player.components.Core
import io.clappr.player.plugin.Loader
import io.clappr.player.plugin.PluginEntry
import io.clappr.player.plugin.UIPlugin
import io.clappr.player.plugin.core.UICorePlugin
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23], application = Application::class)
class NextVideoPluginTest {

    private lateinit var core: Core

    private lateinit var nextVideoPlugin: NextVideoPlugin

    private val source = "url"

    @Before
    fun setup() {
        BaseObject.applicationContext = ApplicationProvider.getApplicationContext()

        Loader.register(NextVideoPlugin.entry)
        Loader.register(FooUICorePlugin.entry)
        core = Core(Options(source = source))

        //Trigger Container change events
        val container = Container(core.options)
        core.activeContainer  = container

        //Trigger Playback change events
        container.playback = FakePlayback("")

        core.render()
        nextVideoPlugin = core.plugins.filterIsInstance(NextVideoPlugin::class.java).first()
    }

    @Test
    fun shouldContainVideoItemsAfterRendering() {
        assertTrue(nextVideoPlugin.view.findViewById<LinearLayout>(R.id.video_list).childCount > 0)
    }

    @Test
    fun shouldHideAfterDidChangePlaybackEventIsTriggered() {
        assertUiPluginHidden(nextVideoPlugin)
    }

    @Test
    fun shouldHideViewWhenWillPlayEventIsTriggered() {
        nextVideoPlugin.show()

        core.activeContainer?.playback?.trigger(Event.WILL_PLAY.value)

        assertUiPluginHidden(nextVideoPlugin)
    }

    @Test
    fun shouldShowViewWhenDidCompleteEventIsTriggered() {
        core.activeContainer?.playback?.trigger(Event.DID_COMPLETE.value)

        assertUiPluginShown(nextVideoPlugin)
    }

    @Test
    fun shouldShowNextVideoPluginAboveOtherCorePlugins(){
        core.activeContainer?.playback?.trigger(Event.DID_COMPLETE.value)

        with(core.view as ViewGroup){
            assertEquals(getChildAt(childCount - 1), nextVideoPlugin.view)
        }
    }

    @Test
    fun shouldShowViewWhenDidStopEventIsTriggered() {
        core.activeContainer?.playback?.trigger(Event.DID_STOP.value)

        assertUiPluginShown(nextVideoPlugin)
    }

    @Test
    fun shouldHideViewWhenDidChangePlaybackEventIsTriggered() {
        nextVideoPlugin.show()

        val newPlayback = FakePlayback(source)
        core.activeContainer?.playback = newPlayback

        assertUiPluginHidden(nextVideoPlugin)
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

class FooUICorePlugin(core: Core): UICorePlugin(core, name = name){
    companion object {
        const val name = "FooUICorePlugin"

        val entry = PluginEntry.Core(name, factory = { core -> FooUICorePlugin(core) })
    }

    override val view: View?
        get() = LinearLayout(applicationContext)
}