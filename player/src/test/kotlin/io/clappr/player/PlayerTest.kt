package io.clappr.player

import android.os.Bundle
import com.globo.clappr.base.BaseObject
import com.globo.clappr.base.Callback
import com.globo.clappr.base.Event
import com.globo.clappr.base.Options
import com.globo.clappr.components.Playback
import com.globo.clappr.components.PlaybackSupportInterface
import com.globo.clappr.plugin.Loader
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication

import org.junit.Assert.*

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23))
open class PlayerTest {
    class PlayerTestPlayback(source: String, mimeType: String? = null, options: Options = Options()) : Playback(source, mimeType, options) {
        companion object: PlaybackSupportInterface {
            override val name: String = "player_test"

            override fun supportsSource(source: String, mimeType: String?): Boolean {
                return source.isNotEmpty()
            }
        }

        var internalState = State.NONE
        override val state: State
            get() = internalState

        override val duration: Double = 1.0
        override val position: Double = 0.0

        override fun play(): Boolean { return true }
        override fun stop(): Boolean { return true }
        override fun seek(position: Int): Boolean { return true }

        override fun pause(): Boolean {
            trigger(Event.WILL_PAUSE.value)
            trigger(Event.DID_PAUSE.value)
            return true
        }
    }

    lateinit var player: Player

    @Before
    fun setup() {
        Player.initialize(ShadowApplication.getInstance().applicationContext)
        Loader.clearPlaybacks()
        player = Player()
        Loader.registerPlayback(PlayerTestPlayback::class)
    }

    @Test(expected = IllegalStateException::class)
    fun instatiateWithoutContext() {
        BaseObject.context = null
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

    @Test
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
        player.on(Event.WILL_PAUSE.value, Callback.wrap { bundle: Bundle? -> willPauseCalled = true })
        player.on(Event.DID_PAUSE.value, Callback.wrap { bundle: Bundle? -> didPauseCalled = true })

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

        val testPlayback = player.core?.activePlayback as PlayerTestPlayback

        testPlayback.internalState = Playback.State.ERROR
        assertEquals("invalid state (not ERROR)", Player.State.ERROR, player.state)
        testPlayback.internalState = Playback.State.IDLE
        assertEquals("invalid state (not IDLE)", Player.State.IDLE, player.state)
        testPlayback.internalState = Playback.State.PAUSED
        assertEquals("invalid state (not PAUSED)", Player.State.PAUSED, player.state)
        testPlayback.internalState = Playback.State.PLAYING
        assertEquals("invalid state (not PLAYING)", Player.State.PLAYING, player.state)
        testPlayback.internalState = Playback.State.STALLED
        assertEquals("invalid state (not STALLED)", Player.State.STALLED, player.state)
    }
}