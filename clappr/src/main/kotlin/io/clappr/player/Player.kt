package io.clappr.player

import android.annotation.TargetApi
import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.clappr.player.base.*
import io.clappr.player.components.Core
import io.clappr.player.components.Playback
import io.clappr.player.extensions.context.isRunningInAndroidTvDevice
import io.clappr.player.plugin.PlaybackConfig
import io.clappr.player.plugin.PluginConfig
import io.clappr.player.plugin.core.externalinput.ExternalInputDevice
import io.clappr.player.plugin.core.externalinput.ExternalInputPlugin

/**
 *  Main Player class.
 *
 * Once instantiated it should be [configured][configure] and added to a view hierarchy before playback can begin.
 */
open class Player(
    private val base: BaseObject = BaseObject(),
    private val coreEventsToListen: MutableSet<String> = mutableSetOf(),
    private val playbackEventsToListen: MutableSet<String> = mutableSetOf(),
    private val containerEventsToListen: MutableSet<String> = mutableSetOf()
) : Fragment(), EventInterface by base {

    companion object {
        init {
            PluginConfig.register()
            PlaybackConfig.register()
        }

        /**
         * Initialize Player for the application. This method need to be called before any Player instantiation.
         */
        @JvmStatic
        fun initialize(applicationContext: Context) {
            BaseObject.applicationContext = applicationContext
        }
    }

    private val externalInputDevice: ExternalInputDevice?
        get() = (core?.plugins?.firstOrNull { it.name == ExternalInputPlugin.name }) as? ExternalInputDevice

    init {
        Event.values().forEach { playbackEventsToListen.add(it.value) }
        coreEventsToListen.addAll(
            listOf(
                Event.REQUEST_FULLSCREEN.value,
                Event.EXIT_FULLSCREEN.value,
                Event.MEDIA_OPTIONS_SELECTED.value
            )
        )
    }

    private var receiver: BroadcastReceiver? = null

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
         * Media playback is stalling.
         */
        STALLING,
        /**
         * Player or Media error
         */
        ERROR
    }

    protected var core: Core? = null
        private set(value) {
            playerViewGroup?.removeView(core?.view)
            unbindPlaybackEvents()
            unbindContainerEvents()
            unbindCoreEvents()
            core?.destroy()

            field = value
            updateCoreFullScreenStatus()
            bindCoreEvents()

            core?.let {
                it.on(InternalEvent.WILL_CHANGE_ACTIVE_PLAYBACK.value) { unbindPlaybackEvents() }
                it.on(InternalEvent.DID_CHANGE_ACTIVE_PLAYBACK.value) { bindPlaybackEvents() }
                it.on(InternalEvent.WILL_CHANGE_ACTIVE_CONTAINER.value) { unbindContainerEvents() }
                it.on(InternalEvent.DID_CHANGE_ACTIVE_CONTAINER.value) { bindContainerEvents() }
                it.on(Event.REQUEST_FULLSCREEN.value) { bundle: Bundle? ->
                    trigger(
                        Event.REQUEST_FULLSCREEN.value,
                        bundle
                    )
                }
                it.on(Event.EXIT_FULLSCREEN.value) { bundle: Bundle? -> trigger(Event.EXIT_FULLSCREEN.value, bundle) }
                it.on(Event.MEDIA_OPTIONS_SELECTED.value) { bundle: Bundle? ->
                    trigger(
                        Event.MEDIA_OPTIONS_SELECTED.value,
                        bundle
                    )
                }
                it.on(Event.DID_SELECT_AUDIO.value) { bundle: Bundle? -> trigger(Event.DID_SELECT_AUDIO.value, bundle) }
                it.on(Event.DID_SELECT_SUBTITLE.value) { bundle: Bundle? ->
                    trigger(
                        Event.DID_SELECT_SUBTITLE.value,
                        bundle
                    )
                }

                if (it.activeContainer != null) {
                    bindContainerEvents()
                }

                if (it.activePlayback != null) {
                    bindPlaybackEvents()
                }

                playerViewGroup?.addView(it.render().view)
            }
        }

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
                Playback.State.STALLING -> State.STALLING
                Playback.State.ERROR -> State.ERROR
            }

    private val playbackEventsIds = mutableSetOf<String>()

    private val containerEventsIds = mutableSetOf<String>()

    private val coreEventsIds = mutableSetOf<String>()

    private var playerViewGroup: ViewGroup? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        playerViewGroup = inflater.inflate(R.layout.player_fragment, container, false) as ViewGroup
        core?.let { playerViewGroup?.addView(it.render().view) }
        return (playerViewGroup as View)
    }

    override fun onPause() {
        super.onPause()
        activity?.takeUnless { it.isRunningInAndroidTvDevice() }?.let {
            val playerIsNotPaused = core?.activePlayback?.state != Playback.State.PAUSED
            if (playerIsNotPaused && !pause())
                stop()
        }
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
    open fun configure(options: Options) {
        core?.let {
            it.options = options
        } ?: createCore(options)

        core?.load()
    }

    private fun createCore(options: Options) {
        core = Core(options)
    }

    protected fun destroyCore() {
        core = null
    }

    /**
     * Load a new media. Always make sure that the stop() method was called before invoking this
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
            playbackEventsToListen.mapTo(playbackEventsIds) { event ->
                listenTo(it, event) { bundle: Bundle? ->
                    trigger(
                        event,
                        bundle
                    )
                }
            }
        }
    }

    private fun unbindPlaybackEvents() {
        playbackEventsIds.forEach(::stopListening)
        playbackEventsIds.clear()
    }

    private fun bindContainerEvents() {
        core?.activeContainer?.let {
            containerEventsToListen.mapTo(containerEventsIds) { event ->
                listenTo(
                    it,
                    event
                ) { bundle: Bundle? -> trigger(event, bundle) }
            }
        }
    }

    private fun unbindContainerEvents() {
        containerEventsIds.forEach(::stopListening)
        containerEventsIds.clear()
    }

    private fun bindCoreEvents() {
        core?.let {
            coreEventsToListen.mapTo(coreEventsIds) { event ->
                listenTo(it, event) { bundle: Bundle? ->
                    trigger(
                        event,
                        bundle
                    )
                }
            }
        }
    }

    private fun unbindCoreEvents() {
        coreEventsIds.forEach(::stopListening)
        coreEventsIds.clear()
    }

    private fun updateCoreFullScreenStatus() {
        core?.fullscreenState = if (this.fullscreen) Core.FullscreenState.FULLSCREEN else Core.FullscreenState.EMBEDDED
    }

    fun holdKeyEvent(event: KeyEvent) {
        externalInputDevice?.holdKeyEvent(event)
    }
    
    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        Log.d("@@@", "Is in PiP mode: $isInPictureInPictureMode")

        if (isInPictureInPictureMode) {
            core?.trigger(Event.DID_ENTER_PIP.value)
            play()

            receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent?) {
                    if (intent == null || "media_control" != intent.action) return

                    when (intent.getIntExtra("control_type", 0)) {
                        1 -> {
                            when(state) {
                                State.PLAYING -> pause()
                                State.PAUSED, State.IDLE -> play()
                                else -> {}
                            }
                        }
                        2 -> {
                            seek(Math.min(duration, position + 10).toInt())
                        }
                    }
                }
            }

            requireActivity().registerReceiver(receiver, IntentFilter("media_control"))
        } else {
            core?.trigger(Event.DID_EXIT_PIP.value)
            receiver?.let { requireActivity().unregisterReceiver(it) }
        }

        super.onPictureInPictureModeChanged(isInPictureInPictureMode)
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun enterPictureInPictureMode() {
        requireActivity().enterPictureInPictureMode(createPipParameters())
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createPipParameters() : PictureInPictureParams {
        val playIntent = Intent("media_control").putExtra("control_type", 1)
        val playPendingIntent = PendingIntent.getBroadcast(activity, 1, playIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val playAction = RemoteAction(Icon.createWithResource(context, R.drawable.exo_controls_play), "Play", "Play", playPendingIntent)

        val fastForwardIntent = Intent("media_control").putExtra("control_type", 2)
        val fastForwardPendingIntent = PendingIntent.getBroadcast(activity, 2, fastForwardIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val fastForwardAction = RemoteAction(Icon.createWithResource(context, R.drawable.exo_icon_fastforward), "Fast Forward", "Fast Forward", fastForwardPendingIntent)

        return PictureInPictureParams.Builder().setActions(mutableListOf(playAction, fastForwardAction)).build()
    }
}
