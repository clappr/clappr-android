package io.clappr.player

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.clappr.player.base.*
import io.clappr.player.components.Core
import io.clappr.player.components.Playback
import io.clappr.player.log.Logger
import io.clappr.player.playback.ExoPlayerPlayback
import io.clappr.player.playback.NoOpPlayback
import io.clappr.player.plugin.Loader
import io.clappr.player.plugin.LoadingPlugin

/**
 *  Main Player class.
 *
 * Once instantiated it should be [configured][configure] and added to a view hierarchy before playback can begin.
 */
open class Player(private val base: BaseObject = BaseObject()) : Fragment(), EventInterface by base {
    companion object {
        val playbackEvents = mutableSetOf<String>()
        val playbackEventsIds = mutableSetOf<String>()

        val containerEvents = mutableSetOf<String>()
        val containerEventsIds = mutableSetOf<String>()

        init {
            // TODO - Add default plugins and playbacks
            Loader.registerPlugin(LoadingPlugin::class)
            Loader.registerPlayback(NoOpPlayback::class)
            Loader.registerPlayback(ExoPlayerPlayback::class)

            Event.values().forEach { playbackEvents.add(it.value) }
        }

        /**
         * Initialize Player for the application. This method need to be called before any Player instantiation.
         */
        @JvmStatic
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

    var core: Core? = null
        private set(value) {
            core?.stopListening()
            field = value
            core?.let {
                it.on(InternalEvent.WILL_CHANGE_ACTIVE_PLAYBACK.value, Callback.wrap { bundle: Bundle? -> unbindPlaybackEvents() })
                it.on(InternalEvent.DID_CHANGE_ACTIVE_PLAYBACK.value, Callback.wrap { bundle: Bundle? -> bindPlaybackEvents() })
                it.on(InternalEvent.WILL_CHANGE_ACTIVE_CONTAINER.value, Callback.wrap { bundle: Bundle? -> unbindContainerEvents() })
                it.on(InternalEvent.DID_CHANGE_ACTIVE_CONTAINER.value, Callback.wrap { bundle: Bundle? -> bindContainerEvents() })
                it.on(Event.REQUEST_FULLSCREEN.value, Callback.wrap { bundle: Bundle? -> trigger(Event.REQUEST_FULLSCREEN.value, bundle) })
                it.on(Event.EXIT_FULLSCREEN.value, Callback.wrap { bundle: Bundle? -> trigger(Event.EXIT_FULLSCREEN.value, bundle) })

                if (it.activeContainer != null) {
                    bindContainerEvents()
                }

                if (it.activePlayback != null) {
                    bindPlaybackEvents()
                }
            }
        }

    internal val loader = Loader()

    /**
     * Media current position in seconds.
     */
    val position: Double
        get() = core?.activePlayback?.position ?: Double.NaN
    /**
     * Media duration in seconds.
     */
    val duration: Double
        get() = core?.activePlayback?.duration ?: Double.NaN

    /**
     * Whether the player is in fullscreen mode
     */
    var fullscreen = false
        set(value) {
            core?.fullscreenState = if (value) Core.FullscreenState.FULLSCREEN else Core.FullscreenState.EMBEDDED
        }

    /**
     * Current Player state.
     */
    val state: State
        get() =
        when (core?.activePlayback?.state ?: Playback.State.NONE) {
            Playback.State.NONE -> State.NONE
            Playback.State.IDLE -> State.IDLE
            Playback.State.PLAYING -> State.PLAYING
            Playback.State.PAUSED -> State.PAUSED
            Playback.State.STALLED -> State.STALLED
            Playback.State.ERROR -> State.ERROR
            else -> State.NONE
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val playerViewGroup = inflater.inflate(R.layout.player_fragment, container, false) as ViewGroup
        core?.let { playerViewGroup.addView(it.render().view) }
        return playerViewGroup
    }

    /**
     * Configure Player. This configuration must be performed before adding fragment to a view hierarchy.
     *
     * @param options
     *          a map of key-value options.
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
    fun load(source: String, mimeType: String? = null): Boolean {
        return core?.activeContainer?.load(source, mimeType) ?: false
    }

    fun load(source: String): Boolean {
        return load(source, null)
    }

    /**
     * Start or resume media playing.
     *
     * @return If the operation was accepted
     */
    fun play(): Boolean {
        return core?.activePlayback?.play() ?: false
    }

    /**
     * Pause media playing. Media playback may be resumed.
     *
     * @return If the operation was accepted
     */
    fun pause(): Boolean {
        return core?.activePlayback?.pause() ?: false
    }

    /**
     * Stop media playing. Media playback is ended.
     *
     * @return If the operation was accepted
     */
    fun stop(): Boolean {
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
    fun seek(position: Int): Boolean {
        return core?.activePlayback?.seek(position) ?: false
    }

    private fun bindPlaybackEvents() {
        core?.activePlayback?.let {
            playbackEvents.mapTo(playbackEventsIds) { event -> listenTo(it, event, Callback.wrap { bundle: Bundle? -> trigger(event, bundle) }) }
        }
    }

    private fun unbindPlaybackEvents() {
        playbackEventsIds.forEach {
            core?.activePlayback?.stopListening(it)
        }
        playbackEventsIds.clear()
    }

    protected open fun bindContainerEvents() {

    }

    private fun unbindContainerEvents() {
        core?.activeContainer?.stopListening()
    }
}
