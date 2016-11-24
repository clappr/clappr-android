package com.globo.clappr

import com.globo.clappr.plugin.Loader
import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.globo.clappr.base.*
import com.globo.clappr.components.Core
import com.globo.clappr.components.Playback
import com.globo.clappr.playback.NoOpPlayback

/**
 *  Main Player class.
 *
 * Once instantiated it should be attached to ViewGroup before playback can begin.
 */
class Player(private val base : BaseObject = BaseObject()) : Fragment(), EventInterface by base {
    companion object {
        /**
         * Initialize Player for the application. This method need to be called before any Player instantiation.
         */
        fun initialize(context: Context) {
            BaseObject.context = context
        }
    }

    /**
     * Player state
     */
    enum class State {
        /**
         * Player is uninitialized and not ready.
         */
        NONE,
        /**
         * Player is ready but no media is loaded.
         */
        IDLE,
        /**
         * Playing media.
         */
        PLAYING,
        /**
         * Media playback is paused.
         */
        PAUSED,
        /**
         * Media playback is stalled.
         */
        STALLED,
        /**
         * Player or Media error
         */
        ERROR
    }

    var core : Core? = null
        set(value) {
            if (value != null) {
                value.on(InternalEvent.WILL_CHANGE_ACTIVE_PLAYBACK.value, Callback.wrap { bundle: Bundle? -> unbindPlaybackEvents() })
                value.on(InternalEvent.DID_CHANGE_ACTIVE_PLAYBACK.value, Callback.wrap { bundle: Bundle? -> bindPlaybackEvents() })
                if (value.activePlayback != null) { bindPlaybackEvents() }
            } else {
                core?.stopListening()
            }
            field = value
        }
    val loader = Loader()

    /**
     * Media current position in seconds.
     */
    val position: Double = core?.activePlayback?.position ?: Double.NaN
    /**
     * Media duration in seconds.
     */
    val duration: Double = core?.activePlayback?.duration ?: Double.NaN
    /**
     * Current Player state.
     */
    val state: State
        get() =
            when(core?.activePlayback?.state ?: Playback.State.NONE) {
                Playback.State.NONE -> State.NONE
                Playback.State.IDLE -> State.IDLE
                Playback.State.PLAYING -> State.PLAYING
                Playback.State.PAUSED -> State.PAUSED
                Playback.State.STALLED -> State.STALLED
                Playback.State.ERROR -> State.ERROR
                else -> State.NONE
            }

    init {
        Loader.registerPlayback(NoOpPlayback::class)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val playerViewGroup = inflater.inflate(R.layout.player_fragment, container, false) as ViewGroup
        core?.let { playerViewGroup?.addView(core!!.view) }
        return playerViewGroup
    }

    /**
     * Configure Player. This configuration must be performed before adding fragment to a view hierarchy.
     *
     * @param options
     *          a map of key-value options. Available values:
     *
     */
    fun configure(options: Options) {
        core = Core(loader, options)
    }

    /**
     * Load a new media
     *
     * @param source
     *          valid media url
     * @param mimeType
     *          (Optional) media mime type.
     */
    fun load(source: String, mimeType: String? = null) : Boolean {
        return core?.activeContainer?.load(source, mimeType) ?: false
    }

    /**
     * Start or resume media playing.
     *
     * @return If the operation was accepted
     */
    fun play() : Boolean {
        trigger(Event.PLAYING.value)
        return core?.activePlayback?.play() ?: false
    }

    /**
     * Pause media playing. Media playback may be resumed.
     *
     * @return If the operation was accepted
     */
    fun pause() : Boolean {
        return core?.activePlayback?.pause() ?: false
    }

    /**
     * Stop media playing. Media playback is ended.
     *
     * @return If the operation was accepted
     */
    fun stop() : Boolean {
        return core?.activePlayback?.stop() ?: false
    }

    /**
     * Move current playback position.
     *
     * @param position
     *          new media position in seconds
     *
     * @return If the operation was accepted
     */
    fun seek(position: Int) : Boolean {
        return core?.activePlayback?.seek(position) ?: false
    }

    private fun bindPlaybackEvents() {
        for (event in Event.values()) {
            core?.activePlayback?.on(event.value, Callback.wrap { bundle: Bundle? -> trigger(event.value, bundle) })
        }
    }

    private fun unbindPlaybackEvents() {
        core?.activePlayback?.stopListening()
    }
}
