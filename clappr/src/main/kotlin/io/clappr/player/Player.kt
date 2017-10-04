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
import io.clappr.player.playback.ExoPlayerPlayback
import io.clappr.player.playback.NoOpPlayback
import io.clappr.player.plugin.Loader
import io.clappr.player.plugin.LoadingPlugin
import io.clappr.player.plugin.PosterPlugin

/**
 *  Main Player class.
 *
 * Once instantiated it should be [configured][configure] and added to a view hierarchy before playback can begin.
 */
open class Player(private val base: BaseObject = BaseObject()) : Fragment(), EventInterface by base {
    companion object {
        val playbackEventsToListen = mutableSetOf<String>()
        val containerEventsToListen = mutableSetOf<String>()

        init {
            // TODO - Add default plugins and playbacks
            Loader.registerPlugin(PosterPlugin::class)
            Loader.registerPlugin(LoadingPlugin::class)
            Loader.registerPlayback(NoOpPlayback::class)
            Loader.registerPlayback(ExoPlayerPlayback::class)

            Event.values().forEach { playbackEventsToListen.add(it.value) }
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
            playerViewGroup?.removeView(core?.view)
            unbindPlaybackEvents()
            unbindContainerEvents()
            core?.destroy()

            field = value
            updateCoreFullScreenStatus()
            core?.let {
                it.on(InternalEvent.WILL_CHANGE_ACTIVE_PLAYBACK.value, Callback.wrap { unbindPlaybackEvents() })
                it.on(InternalEvent.DID_CHANGE_ACTIVE_PLAYBACK.value, Callback.wrap { bindPlaybackEvents() })
                it.on(InternalEvent.WILL_CHANGE_ACTIVE_CONTAINER.value, Callback.wrap { unbindContainerEvents() })
                it.on(InternalEvent.DID_CHANGE_ACTIVE_CONTAINER.value, Callback.wrap { bindContainerEvents() })
                it.on(Event.REQUEST_FULLSCREEN.value, Callback.wrap { bundle: Bundle? -> trigger(Event.REQUEST_FULLSCREEN.value, bundle) })
                it.on(Event.EXIT_FULLSCREEN.value, Callback.wrap { bundle: Bundle? -> trigger(Event.EXIT_FULLSCREEN.value, bundle) })

                if (it.activeContainer != null) {
                    bindContainerEvents()
                }

                if (it.activePlayback != null) {
                    bindPlaybackEvents()
                }

                playerViewGroup?.addView(it.render().view)
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
            field = value
            updateCoreFullScreenStatus()
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

    val playbackEventsIds = mutableSetOf<String>()

    val containerEventsIds = mutableSetOf<String>()

    var playerViewGroup: ViewGroup? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        playerViewGroup = inflater.inflate(R.layout.player_fragment, container, false) as ViewGroup
        core?.let { playerViewGroup?.addView(it.render().view) }
        return (playerViewGroup as View)
    }

    override fun onPause() {
        super.onPause()
        pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stop()
        playerViewGroup?.removeView(core?.view)
        playerViewGroup = null
        core = null
    }

    /**
     * Configure Player. This configuration must be performed before adding fragment to a view hierarchy.
     * @param options
     *          a map of key-value options.
     *
     */
    fun configure(options: Options) {
        core = Core(loader, options)
        core?.load()
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
        // TODO: Use container load instead of a new Player configuration
        core?.let {
            val options = it.options
            options.source = source
            options.mimeType = mimeType
            configure(options)
            return true
        }

        return false
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
            playbackEventsToListen.mapTo(playbackEventsIds) { event -> listenTo(it, event, Callback.wrap { bundle: Bundle? -> trigger(event, bundle) }) }
        }
    }

    private fun unbindPlaybackEvents() {
        playbackEventsIds.forEach {
            stopListening(it)
        }
        playbackEventsIds.clear()
    }

    private fun bindContainerEvents() {
        core?.activeContainer?.let {
            containerEventsToListen.mapTo(containerEventsIds) { event -> listenTo(it, event, Callback.wrap { bundle: Bundle? -> trigger(event, bundle) }) }
        }
    }

    private fun unbindContainerEvents() {
        containerEventsIds.forEach {
            stopListening(it)
        }
        containerEventsIds.clear()
    }

    private fun updateCoreFullScreenStatus() {
        core?.fullscreenState = if (this.fullscreen) Core.FullscreenState.FULLSCREEN else Core.FullscreenState.EMBEDDED
    }
}
