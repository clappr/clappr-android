package io.clappr.player

import android.os.Bundle
import io.clappr.player.base.*
import io.clappr.player.components.Core
import io.clappr.player.components.Playback
import io.clappr.player.components.PlaybackSupportInterface
import io.clappr.player.plugin.Loader
import io.clappr.player.plugin.core.CorePlugin
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication

import org.junit.Assert.*
import org.junit.Ignore

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [23])
open class PlayerTest {

    private val playerTestEvent = "playerTestEvent"

    lateinit var player: Player

    @Before
    fun setup() {
        Player.initialize(ShadowApplication.getInstance().applicationContext)

        Loader.clearPlaybacks()
        Loader.registerPlugin(CoreTestPlugin::class)
        Loader.registerPlayback(PlayerTestPlayback::class)

        PlayerTestPlayback.internalState = Playback.State.NONE

        player = Player(playbackEventsToListen = mutableSetOf(playerTestEvent))
    }

    @Test(expected = IllegalStateException::class)
    fun instantiateWithoutContext() {
        BaseObject.applicationContext = null
        val invalidPlayer = Player()
    }

    @Test
    fun shouldHaveInvalidStatesBeforeConfigure() {
        assertEquals("valid duration", Double.NaN, player.duration, 0.0)
        assertEquals("valid position", Double.NaN, player.position, 0.0)

        assertFalse("play enabled", player.play())
        assertFalse("stop enabled", player.stop())
        assertFalse("pause enabled", player.pause())
        assertFalse("seek enabled", player.seek(0))
        assertFalse("load enabled", player.load(""))
    }

    @Ignore @Test
    fun shouldHaveInvalidStatesWithUnsupportedMedia() {
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
    fun configuredPlayer() {
        player.configure(Options(source = "valid"))

        assertNotEquals("invalid duration", Double.NaN, player.duration, 0.0)
        assertNotEquals("invalid position", Double.NaN, player.position, 0.0)

        assertTrue("play disabled", player.play())
        assertTrue("stop disabled", player.stop())
        assertTrue("pause disabled", player.pause())
        assertTrue("seek disabled", player.seek(0))
    }

    @Test
    fun shouldTriggerEvents() {
        var willPauseCalled = false
        var didPauseCalled = false
        player.on(Event.WILL_PAUSE.value, Callback.wrap { willPauseCalled = true })
        player.on(Event.DID_PAUSE.value, Callback.wrap { didPauseCalled = true })

        player.configure(Options(source = "valid"))
        player.pause()

        assertTrue("WILL_PAUSE not triggered", willPauseCalled)
        assertTrue("DID_PAUSE not triggered", didPauseCalled)
    }

    @Test
    fun playerStates() {
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
    fun shouldUnbindOnConfigure() {
        var willPauseCalled = false
        var didPauseCalled = false

        player.on(Event.WILL_PAUSE.value, Callback.wrap { willPauseCalled = true })
        player.on(Event.DID_PAUSE.value, Callback.wrap { didPauseCalled = true })

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
    fun shouldCoreChangeOptionsOnPlayerConfigure() {
        val expectedFirstOption = "first option"
        val expectedSecondOption = "second option"

        var option = ""
        player.on(playerTestEvent, Callback.wrap { bundle ->
            bundle?.let { option = it.getString("option") }
        })

        player.configure(Options(source="123", options=hashMapOf("test_option" to expectedFirstOption)))
        player.play()

        assertEquals(expectedFirstOption, option)

        player.configure(Options(source="321", options=hashMapOf("test_option" to expectedSecondOption)))
        player.play()

        assertEquals(expectedSecondOption, option)
    }

    /**
     * This test use the CoreTestPlugin to send by PlayerTest event the current mime type passed by
     * options. That way we ensure that we are changing the options when we call a load before a
     * configure.
     */
    @Test
    fun shouldCoreChangeMimeTypeOnPlayerConfigure() {
        val expectedFirstMimeType = "mimeType"
        val expectedSecondMimeType = "other-mimeType"

        var mimeType = ""
        player.on(playerTestEvent, Callback.wrap { bundle ->
            bundle?.let {
                mimeType = it.getString("mimeType")
            }
        })

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
    fun shouldCoreChangeMimeTypeOnPlayerLoad() {
        val expectedFirstMimeType = "mimeType"
        val expectedSecondMimeType = "other-mimeType"

        var mimeType = ""
        player.on(playerTestEvent, Callback.wrap { bundle ->
            bundle?.let {
                mimeType = it.getString("mimeType")
            }
        })

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
    fun shouldCoreChangeSourceOnPlayerConfigure() {
        val expectedFirstSource = "source"
        val expectedSecondSource = "other-source"

        var sourceToPlay = ""
        player.on(playerTestEvent, Callback.wrap { bundle ->
            bundle?.let {
                sourceToPlay = it.getString("source")
            }
        })

        player.configure(Options(source=expectedFirstSource))
        player.play()

        assertEquals(expectedFirstSource, sourceToPlay)

        player.configure(Options(source=expectedSecondSource))
        player.play()

        assertEquals(expectedSecondSource, sourceToPlay)
    }

    /**
     * This test use the CoreTestPlugin to send by PlayerTest event the current source passed by
     * options. That way we ensure that we are changing the options when we call a load before a
     * configure.
     */
    @Test
    fun shouldCoreChangeSourceOnPlayerLoad() {
        val expectedFirstSource = "source"
        val expectedSecondSource = "other-source"

        var sourceToPlay = ""
        player.on(playerTestEvent, Callback.wrap { bundle ->
            bundle?.let {
                sourceToPlay = it.getString("source")
            }
        })

        player.configure(Options(source=expectedFirstSource))
        player.play()

        assertEquals(expectedFirstSource, sourceToPlay)

        player.load(source=expectedSecondSource)
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
    fun shouldCoreHaveSameInstanceOnPlayerConfigure() {
        val expectedDistinctCoreId = 1

        val coreIdList = mutableSetOf<String>()
        player.on(playerTestEvent, Callback.wrap { bundle ->
            bundle?.let { coreIdList.add(it.getString("coreId")) }
        })

        player.configure(Options(source = "123"))
        player.play()

        player.configure(Options(source = "321"))
        player.play()

        assertEquals(expectedDistinctCoreId, coreIdList.size)
    }

    class PlayerTestPlayback(source: String, mimeType: String? = null, options: Options = Options()) : Playback(source, mimeType, options) {
        companion object: PlaybackSupportInterface {
            override val name: String = "player_test"
            var internalState = State.NONE

            override fun supportsSource(source: String, mimeType: String?): Boolean {
                return source.isNotEmpty()
            }
        }

        override val state: State
            get() = internalState

        override val duration: Double = 1.0
        override val position: Double = 0.0

        override fun stop(): Boolean { return true }
        override fun seek(seconds: Int): Boolean { return true }

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

    class CoreTestPlugin(core: Core) : CorePlugin(core) {
        companion object : NamedType {
            override val name: String?
                get() = "coreTestPlugin"
        }

        init {
            listenTo(core, InternalEvent.DID_CHANGE_ACTIVE_PLAYBACK.value, Callback.wrap { bindPlaybackEvents() })
        }

        private fun bindPlaybackEvents() {
            core.activePlayback?.let {
                listenTo(it, Event.WILL_PLAY.value, Callback.wrap { _ ->
                    it.trigger("playerTestEvent", Bundle().apply {
                        putString("source", core.options.source)
                        putString("mimeType", core.options.mimeType)
                        putString("coreId", core.id)
                        core.options.options["test_option"]?.let { testOption ->
                            putString("option", testOption as String)
                        }
                    })
                })
            }
        }
    }
}