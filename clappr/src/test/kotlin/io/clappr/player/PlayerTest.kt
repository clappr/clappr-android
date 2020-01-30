package io.clappr.player

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import androidx.test.core.app.ApplicationProvider
import io.clappr.player.base.*
import io.clappr.player.components.Core
import io.clappr.player.components.Playback
import io.clappr.player.components.PlaybackEntry
import io.clappr.player.components.PlaybackSupportCheck
import io.clappr.player.plugin.Loader
import io.clappr.player.plugin.PluginEntry
import io.clappr.player.plugin.UIPlugin
import io.clappr.player.plugin.core.CorePlugin
import io.clappr.player.plugin.core.externalinput.ExternalInputDevice
import io.clappr.player.plugin.core.externalinput.ExternalInputPlugin
import org.junit.Assert.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.test.assertNull

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
open class PlayerTest {

    private val playerTestEvent = "playerTestEvent"

    lateinit var player: Player

    @Before
    fun setup() {
        Player.initialize(ApplicationProvider.getApplicationContext())

        Loader.clearPlaybacks()
        Loader.register(CoreTestPlugin.entry)
        Loader.register(PlayerTestPlayback.entry)

        PlayerTestPlayback.internalState = Playback.State.NONE
        EventTestPlayback.event = null
        CoreTestPlugin.event = null

        player = Player(playbackEventsToListen = mutableSetOf(playerTestEvent))
    }

    @Test
    fun `should have invalid states before configure`() {
        assertEquals("valid duration", Double.NaN, player.duration, 0.0)
        assertEquals("valid position", Double.NaN, player.position, 0.0)

        assertFalse("play enabled", player.play())
        assertFalse("stop enabled", player.stop())
        assertFalse("pause enabled", player.pause())
        assertFalse("seek enabled", player.seek(0))
        assertFalse("load enabled", player.load(""))
    }

    @SuppressLint("IgnoreWithoutReason") @Ignore @Test
    fun `should have invalid states with unsupported media`() {
        player.configure(Options(source = ""))

        assertEquals("valid duration", Double.NaN, player.duration, 0.0)
        assertEquals("valid position", Double.NaN, player.position, 0.0)

        assertFalse("play enabled", player.play())
        assertFalse("stop enabled", player.stop())
        assertFalse("pause enabled", player.pause())
        assertFalse("seek enabled", player.seek(0))

        assertFalse("load enabled", player.load(""))
        assertTrue("load disabled", player.load("valid"))
    }

    @Test
    fun `configured player`() {
        player.configure(Options(source = "valid"))

        assertNotEquals("invalid duration", Double.NaN, player.duration, 0.0)
        assertNotEquals("invalid position", Double.NaN, player.position, 0.0)

        assertTrue("play disabled", player.play())
        assertTrue("stop disabled", player.stop())
        assertTrue("pause disabled", player.pause())
        assertTrue("seek disabled", player.seek(0))
    }

    @Test
    fun `should trigger events`() {
        var willPauseCalled = false
        var didPauseCalled = false
        player.on(Event.WILL_PAUSE.value) { willPauseCalled = true }
        player.on(Event.DID_PAUSE.value) { didPauseCalled = true }

        player.configure(Options(source = "valid"))
        player.pause()

        assertTrue("WILL_PAUSE not triggered", willPauseCalled)
        assertTrue("DID_PAUSE not triggered", didPauseCalled)
    }

    @Test
    fun `player states`() {
        assertEquals("invalid state (not NONE)", Player.State.NONE, player.state)

        player.configure(Options(source = "valid"))
        assertEquals("invalid state (not NONE)", Player.State.NONE, player.state)

        PlayerTestPlayback.internalState = Playback.State.ERROR
        assertEquals("invalid state (not ERROR)", Player.State.ERROR, player.state)
        PlayerTestPlayback.internalState = Playback.State.IDLE
        assertEquals("invalid state (not IDLE)", Player.State.IDLE, player.state)
        PlayerTestPlayback.internalState = Playback.State.PAUSED
        assertEquals("invalid state (not PAUSED)", Player.State.PAUSED, player.state)
        PlayerTestPlayback.internalState = Playback.State.PLAYING
        assertEquals("invalid state (not PLAYING)", Player.State.PLAYING, player.state)
        PlayerTestPlayback.internalState = Playback.State.STALLING
        assertEquals("invalid state (not STALLING)", Player.State.STALLING, player.state)
    }

    @Test
    fun `should unbind on configure`() {
        var willPauseCalled = false
        var didPauseCalled = false

        player.on(Event.WILL_PAUSE.value) { willPauseCalled = true }
        player.on(Event.DID_PAUSE.value) { didPauseCalled = true }

        player.configure(Options(source = "valid"))
        player.pause()

        assertTrue("WILL_PAUSE not triggered", willPauseCalled)
        assertTrue("DID_PAUSE not triggered", didPauseCalled)

        willPauseCalled = false
        didPauseCalled = false

        player.configure(Options(source = ""))
        player.pause()

        assertFalse("WILL_PAUSE triggered", willPauseCalled)
        assertFalse("DID_PAUSE triggered", didPauseCalled)
    }

    /************************************* DISCLAIMER ***********************************************
     * The following tests use unconventional methods to test core related behaviours in the Player
     * class. Since now the core is protected in Player we can no longer use it to test some
     * behaviours. To ensure testability we should inject core in the Player and not create it
     * there. Until we change our architecture, to maintain the same tests we use some plugins and
     * send some events with data that will normally not be sent. To be easier to understand each
     * test we tried to explain it and add some comments.
     ***********************************************************************************************/

    /**
     * This test use the CoreTestPlugin to send by PlayerTest event the current option passed by
     * options. That way we ensure that we are changing the options when we call a load before a
     * configure.
     */
    @Test
    fun `should core change options on player configure`() {
        val expectedFirstOption = "first option"
        val expectedSecondOption = "second option"

        var option = ""
        player.on(playerTestEvent) { bundle ->
            bundle?.let {
                option = it.getString("option") ?: ""
            }
        }

        player.configure(Options(source = "123", options = hashMapOf("test_option" to expectedFirstOption)))
        player.play()

        assertEquals(expectedFirstOption, option)

        player.configure(Options(source = "321", options = hashMapOf("test_option" to expectedSecondOption)))
        player.play()

        assertEquals(expectedSecondOption, option)
    }

    /**
     * This test use the CoreTestPlugin to send by PlayerTest event the current mime type passed by
     * options. That way we ensure that we are changing the options when we call a load before a
     * configure.
     */
    @Test
    fun `should core change mime type on player configure`() {
        val expectedFirstMimeType = "mimeType"
        val expectedSecondMimeType = "other-mimeType"

        var mimeType = ""
        player.on(playerTestEvent) { bundle ->
            bundle?.let {
                mimeType = it.getString("mimeType") ?: ""
            }
        }

        player.configure(Options(source = "123", mimeType = expectedFirstMimeType))
        player.play()

        assertEquals(expectedFirstMimeType, mimeType)

        player.configure(Options(source = "321", mimeType = expectedSecondMimeType))
        player.play()

        assertEquals(expectedSecondMimeType, mimeType)
    }

    /**
     * This test use the CoreTestPlugin to send by PlayerTest event the current mime type passed by
     * options. That way we ensure that we are changing the options when we call a load before a
     * configure.
     */
    @Test
    fun `should core change mime type on player load`() {
        val expectedFirstMimeType = "mimeType"
        val expectedSecondMimeType = "other-mimeType"

        var mimeType = ""
        player.on(playerTestEvent) { bundle ->
            bundle?.let {
                mimeType = it.getString("mimeType") ?: ""
            }
        }

        player.configure(Options(source = "123", mimeType = expectedFirstMimeType))
        player.play()

        assertEquals(expectedFirstMimeType, mimeType)

        player.load(source = "321", mimeType = expectedSecondMimeType)
        player.play()

        assertEquals(expectedSecondMimeType, mimeType)
    }

    /**
     * This test use the CoreTestPlugin to send by PlayerTest event the current source passed by
     * options. That way we ensure that we are changing the options when we call a load before a
     * configure.
     */
    @Test
    fun `should core change source on player configure`() {
        val expectedFirstSource = "source"
        val expectedSecondSource = "other-source"

        var sourceToPlay = ""
        player.on(playerTestEvent) { bundle ->
            bundle?.let {
                sourceToPlay = it.getString("source") ?: ""
            }
        }

        player.configure(Options(source = expectedFirstSource))
        player.play()

        assertEquals(expectedFirstSource, sourceToPlay)

        player.configure(Options(source = expectedSecondSource))
        player.play()

        assertEquals(expectedSecondSource, sourceToPlay)
    }

    /**
     * This test use the CoreTestPlugin to send by PlayerTest event the current source passed by
     * options. That way we ensure that we are changing the options when we call a load before a
     * configure.
     */
    @Test
    fun `should core change ource on player load`() {
        val expectedFirstSource = "source"
        val expectedSecondSource = "other-source"

        var sourceToPlay = ""
        player.on(playerTestEvent) { bundle ->
            bundle?.let {
                sourceToPlay = it.getString("source") ?: ""
            }
        }

        player.configure(Options(source = expectedFirstSource))
        player.play()

        assertEquals(expectedFirstSource, sourceToPlay)

        player.load(source = expectedSecondSource)
        player.play()

        assertEquals(expectedSecondSource, sourceToPlay)
    }

    /**
     * This test use the CoreTestPlugin to send by PlayerTest event the core id. That way we can
     * compare the hash between two configures performed in Player, ensuring that the same core
     * instance is used between configures. To force the CoreTestPlugin to send the test event we
     * trigger a WILL_PLAY event when the Playback play is called using the PlayerTestPlayback.
     */
    @Test
    fun `should core have same instance on player configure`() {
        val expectedDistinctCoreId = 1

        val coreIdList = mutableSetOf<String>()
        player.on(playerTestEvent) { bundle ->
            bundle?.let {
                it.getString("coreId")?.let {coreId ->
                    coreIdList.add(coreId)
                }
            }
        }

        player.configure(Options(source = "123"))
        player.play()

        player.configure(Options(source = "321"))
        player.play()

        assertEquals(expectedDistinctCoreId, coreIdList.size)
    }

    @Test
    fun `should listen ready event out of player`() {
        assertPlaybackEventWasTriggered(Event.READY.value)
    }

    @Test
    fun `should listen error event out of player`() {
        assertPlaybackEventWasTriggered(Event.ERROR.value)
    }

    @Test
    fun `should listen playing event out of player`() {
        assertPlaybackEventWasTriggered(Event.PLAYING.value)
    }

    @Test
    fun `should listen did complete event out of player`() {
        assertPlaybackEventWasTriggered(Event.DID_COMPLETE.value)
    }

    @Test
    fun `should listen did pause event out of player`() {
        assertPlaybackEventWasTriggered(Event.DID_PAUSE.value)
    }

    @Test
    fun `should listen stalling event out of player`() {
        assertPlaybackEventWasTriggered(Event.STALLING.value)
    }

    @Test
    fun `should listen did stop event out of player`() {
        assertPlaybackEventWasTriggered(Event.DID_STOP.value)
    }

    @Test
    fun `should listen did seek event out of player`() {
        assertPlaybackEventWasTriggered(Event.DID_SEEK.value)
    }

    @Test
    fun `should listen did update buffer event out of player`() {
        assertPlaybackEventWasTriggered(Event.DID_UPDATE_BUFFER.value)
    }

    @Test
    fun `should listen did update position event outOf player`() {
        assertPlaybackEventWasTriggered(Event.DID_UPDATE_POSITION.value)
    }

    @Test
    fun `should listen will play event out of player`() {
        assertPlaybackEventWasTriggered(Event.WILL_PLAY.value)
    }

    @Test
    fun `should listen will pause event out of player`() {
        assertPlaybackEventWasTriggered(Event.WILL_PAUSE.value)
    }

    @Test
    fun `should listen will week event out of player`() {
        assertPlaybackEventWasTriggered(Event.WILL_SEEK.value)
    }

    @Test
    fun `should listen will stop event out of player`() {
        assertPlaybackEventWasTriggered(Event.WILL_STOP.value)
    }

    @Test
    fun `should listen did change DVR status event out fo player`() {
        assertPlaybackEventWasTriggered(Event.DID_CHANGE_DVR_STATUS.value)
    }

    @Test
    fun `should listen did change DVR availability event out of player`() {
        assertPlaybackEventWasTriggered(Event.DID_CHANGE_DVR_AVAILABILITY.value)
    }

    @Test
    fun `should listen did updaten bitrate event out of player`() {
        assertPlaybackEventWasTriggered(Event.DID_UPDATE_BITRATE.value)
    }

    @Test
    fun `should listen media option update event out of player`() {
        assertPlaybackEventWasTriggered(Event.MEDIA_OPTIONS_UPDATE.value)
    }

    @Test
    fun `should listen media option selected event out of player triggered by core`() {
        assertCoreEventWasTriggered(Event.MEDIA_OPTIONS_SELECTED.value)
    }

    @Test
    fun `should listen did select audio event out of player triggered by core`() {
        assertCoreEventWasTriggered(Event.DID_SELECT_AUDIO.value)
    }

    @Test
    fun `should listen did select subtitle event out of player triggered by core`() {
        assertCoreEventWasTriggered(Event.DID_SELECT_SUBTITLE.value)
    }

    @Test
    fun `should listen request fullscreen event out of player triggered by core`() {
        assertCoreEventWasTriggered(Event.REQUEST_FULLSCREEN.value)
    }

    @Test
    fun `should listen exit fullscreen event out of player triggered by core`() {
        assertCoreEventWasTriggered(Event.EXIT_FULLSCREEN.value)
    }

    @Test
    fun `should pass key event to external input class`(){
        val expectedKeyEvent = KeyEvent(1, 2, 3, 4, 5)

        ExternalInputPluginTest.keyEvent = null

        Loader.register(ExternalInputPluginTest.entry)

        player = Player()
        player.configure(Options(source = "valid"))

        player.holdKeyEvent(expectedKeyEvent)

        assertEquals(expectedKeyEvent, ExternalInputPluginTest.keyEvent)
    }

    private fun assertCoreEventWasTriggered(event: String) {
        var eventWasCalled = false

        CoreTestPlugin.event = event

        player.on(event) { bundle ->
            eventWasCalled = bundle?.getBoolean(CoreTestPlugin.eventBundle) ?: false
        }

        player.configure(Options(source = "valid"))
        player.play()

        assertTrue(eventWasCalled)
    }

    private fun assertPlaybackEventWasTriggered(event: String){
        var eventWasCalled = false
        Loader.register(EventTestPlayback.entry)

        EventTestPlayback.event = event

        player = Player(playbackEventsToListen = mutableSetOf(playerTestEvent))

        player.on(event) { bundle ->
            eventWasCalled = bundle?.getBoolean(EventTestPlayback.eventBundle) ?: false
        }

        player.configure(Options(source = "valid"))
        player.play()

        assertTrue(eventWasCalled)
    }

    class EventTestPlayback(source: String, mimeType: String? = null, options: Options = Options()):
            Playback(source, mimeType, options, name = name, supportsSource = supportsSource) {

        companion object {
            const val name: String = "player_test"
            val supportsSource: PlaybackSupportCheck = { source, _ -> source.isNotEmpty() }

            val entry = PlaybackEntry(
                    name = name,
                    supportsSource = supportsSource,
                    factory = { source, mimeType, options -> EventTestPlayback(source, mimeType, options) })

            const val eventBundle = "test-bundle"
            var event: String? = null
        }

        override fun play(): Boolean {
            event?.let {
                Bundle().apply {
                    putBoolean(eventBundle, true)
                    trigger(it, this)
                }
            }
            return super.play()
        }
    }

    class PlayerTestPlayback(source: String, mimeType: String? = null, options: Options = Options()) :
            Playback(source, mimeType, options, name = name, supportsSource = supportsSource) {
        companion object {
            const val name: String = "player_test"
            val supportsSource: PlaybackSupportCheck = { source, _ -> source.isNotEmpty() }

            val entry = PlaybackEntry(
                    name = name,
                    supportsSource = supportsSource,
                    factory = { source, mimeType, options -> PlayerTestPlayback(source, mimeType, options) })

            var internalState = State.NONE
        }

        override val state: State
            get() = internalState

        override val duration: Double = 1.0
        override val position: Double = 0.0

        override fun stop(): Boolean {
            return true
        }

        override fun seek(seconds: Int): Boolean {
            return true
        }

        override fun play(): Boolean {
            trigger(Event.WILL_PLAY.value)
            return true
        }

        override fun pause(): Boolean {
            trigger(Event.WILL_PAUSE.value)
            trigger(Event.DID_PAUSE.value)
            return true
        }
    }

    @Test
    fun `should not have UIPlugins loaded with chromeless options set`() {
        val options = Options(
            options = hashMapOf(ClapprOption.CHROMELESS.value to true)
        )
        player = Player()
        player.configure(options)

        assertNull(player.getCore().plugins.filterIsInstance(UIPlugin::class.java).firstOrNull())
        assertNull(
            player.getCore().containers.firstOrNull()?.plugins?.filterIsInstance(
                UIPlugin::class.java
            )?.firstOrNull()
        )
    }

    private fun Player.getCore(): Core =
        Player::class.memberProperties.first { it.name == "core" }.run {
            isAccessible = true
            get(this@getCore) as Core
        }


    class CoreTestPlugin(core: Core) : CorePlugin(core) {
        companion object : NamedType {
            override val name = "coreTestPlugin"

            val entry = PluginEntry.Core(name = name, factory = { core -> CoreTestPlugin(core) })
            const val eventBundle = "test-bundle"
            var event: String? = null
        }

        init {
            listenTo(core, InternalEvent.DID_CHANGE_ACTIVE_PLAYBACK.value) { bindPlaybackEvents() }
        }

        private fun bindPlaybackEvents() {
            core.activePlayback?.let { playback ->
                listenTo(playback, Event.WILL_PLAY.value) {
                    triggerTestEvent(playback)
                    triggerCoreEvent()
                }
            }
        }

        private fun triggerTestEvent(playback: Playback) {
            playback.trigger("playerTestEvent", Bundle().apply {
                putString("source", core.options.source)
                putString("mimeType", core.options.mimeType)
                putString("coreId", core.id)
                core.options.options["test_option"]?.let { testOption ->
                    putString("option", testOption as String)
                }
            })
        }

        private fun triggerCoreEvent() {
            event?.let {
                Bundle().apply {
                    putBoolean(eventBundle, true)
                    core.trigger(it, this)
                }
            }
        }
    }

    class ExternalInputPluginTest(core: Core) : CorePlugin(core, name = name), ExternalInputDevice {
        companion object : NamedType {
            override val name = ExternalInputPlugin.name

            val entry = PluginEntry.Core(
                name = name,
                factory = { core -> ExternalInputPluginTest(core) })

            var keyEvent: KeyEvent? = null
        }

        override fun holdKeyEvent(event: KeyEvent) {
            keyEvent = event
        }
    }
}