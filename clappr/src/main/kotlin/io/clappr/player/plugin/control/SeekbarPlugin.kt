package io.clappr.player.plugin.control

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.*
import io.clappr.player.R
import io.clappr.player.base.Event
import io.clappr.player.base.InternalEvent
import io.clappr.player.base.NamedType
import io.clappr.player.components.Core
import io.clappr.player.components.Playback
import io.clappr.player.extensions.unlessChromeless
import io.clappr.player.plugin.PluginEntry

open class SeekbarPlugin(core: Core) : MediaControl.Plugin(core, name) {

    companion object : NamedType {
        override val name = "seekbar"

        val entry = PluginEntry.Core(name = name, factory = ::SeekbarPlugin.unlessChromeless())
    }

    override var panel = Panel.BOTTOM

    override val view by lazy { LayoutInflater.from(applicationContext).inflate(R.layout.seekbar_plugin, null) as ViewGroup }

    open val backgroundView by lazy { view.findViewById(R.id.background_view) as View }

    open val bufferedBar by lazy { view.findViewById(R.id.buffered_bar) as View }

    open val positionBar by lazy { view.findViewById(R.id.position_bar) as View }

    open val scrubberView by lazy { view.findViewById(R.id.scrubber) as View }

    protected var dragging = false

    private val playbackListenerIds = mutableListOf<String>()
    private val handler = Handler()
    private var isInteracting = false
    private val timeBetweenInteractionsEvents = 3500L


    init {
        listenTo(core, InternalEvent.DID_CHANGE_ACTIVE_PLAYBACK.value, { bindEventListeners() })
        listenTo(core, InternalEvent.DID_ENTER_FULLSCREEN.value, { updatePositionOnResize() })
        listenTo(core, InternalEvent.DID_EXIT_FULLSCREEN.value, { updatePositionOnResize() })
    }

    override fun render() {
        super.render()
        view.setOnTouchListener { view, motionEvent -> handleTouch(view, motionEvent) }
        updateLiveStatus()
    }

    open fun bindEventListeners() {
        updateLiveStatus()
        stopPlaybackListeners()
        core.activePlayback?.let {
            playbackListenerIds.add(listenTo(it, Event.DID_LOAD_SOURCE.value, { _ -> bindEventListeners() }))
            playbackListenerIds.add(listenTo(it, Event.DID_UPDATE_BUFFER.value, { bundle -> updateBuffered(bundle) }))
            playbackListenerIds.add(listenTo(it, Event.DID_UPDATE_POSITION.value, { bundle -> updatePosition(bundle) }))
            playbackListenerIds.add(listenTo(it, Event.DID_COMPLETE.value, { _ -> hide() }))
        }
    }

    private fun stopPlaybackListeners(){
        playbackListenerIds.forEach(::stopListening)
        playbackListenerIds.clear()
    }

    open fun handleTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> handleActionDown(motionEvent)
            MotionEvent.ACTION_MOVE -> updateDrag(motionEvent.x)
            MotionEvent.ACTION_UP -> handleActionUp(motionEvent)
            MotionEvent.ACTION_CANCEL -> {
                isInteracting = false
                stopDrag()
            }
        }

        return true
    }

    private fun handleActionDown(motionEvent: MotionEvent) {
        isInteracting = true
        core.trigger(InternalEvent.DID_UPDATE_INTERACTING.value)

        notifyInteraction()
        updateDrag(motionEvent.x)
    }

    private fun handleActionUp(motionEvent: MotionEvent) {
        isInteracting = false
        handleStopDrag(motionEvent.x)
    }

    private fun notifyInteraction() {
        handler.postDelayed({
            if (isInteracting) {
                core.trigger(InternalEvent.DID_UPDATE_INTERACTING.value)
                notifyInteraction()
            }
        }, timeBetweenInteractionsEvents)
    }

    open fun updateDrag(position: Float) {
        view.parent?.requestDisallowInterceptTouchEvent(true)

        dragging = true

        val adjustedPosition = Math.min(Math.max(position, 0.0f), backgroundView.width.toFloat())
        val percentage = (adjustedPosition / backgroundView.width) * 100.0
        updatePosition(percentage, dragging)
    }

    private fun handleStopDrag(position: Float) {
        stopDrag()

        core.activePlayback?.let {
            val adjustedPosition = Math.min(Math.max(position, 0.0f), backgroundView.width.toFloat())
            val time = (adjustedPosition / backgroundView.width) * it.duration
            if (time != it.position) {
                it.seek(time.toInt())
            }
        }
    }

    open fun stopDrag() {
        dragging = false
    }

    private fun updateBuffered(bundle: Bundle?) {
        val buffered = bundle?.getDouble("percentage") ?: 0.0
        core.activePlayback?.let {
            val layoutParams = bufferedBar.layoutParams
            layoutParams.width = ((buffered / 100.0) * backgroundView.width).toInt()
            bufferedBar.layoutParams = layoutParams
        }
    }

    private fun updatePositionOnResize() {
        core.activePlayback?.let {
            view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    removeGlobalLayoutListener(this)
                    val percentage = if (it.duration != 0.0) (it.position / it.duration) * 100 else 0.0
                    updatePosition(percentage)
                }
            })
        }
    }

    protected fun removeGlobalLayoutListener(listener: ViewTreeObserver.OnGlobalLayoutListener?) {
        listener?.let {
            if (Build.VERSION.SDK_INT < 16) view.viewTreeObserver.removeGlobalOnLayoutListener(listener)
            else view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }

    private fun updatePosition(bundle: Bundle?) {
        val percentage = bundle?.getDouble("percentage") ?: 0.0
        updatePosition(percentage)
    }

    open fun updatePosition(percentage: Double, dragEvent: Boolean = false) {
        if (dragEvent == dragging) {
            core.activePlayback?.let {
                val layoutParams = positionBar.layoutParams
                layoutParams.width = recalculatePositionBarWidth(percentage)
                positionBar.layoutParams = layoutParams

                var scrubberPosition = (layoutParams.width - scrubberView.width / 2).toFloat()
                scrubberPosition = Math.min(scrubberPosition, (backgroundView.width - scrubberView.width).toFloat())
                scrubberPosition = Math.max(scrubberPosition, 0.0f)
                scrubberView.x = scrubberPosition
            }
            updateLiveStatus()
        }
    }

    protected fun recalculatePositionBarWidth(percentage: Double) = ((percentage / 100.0) * backgroundView.width).toInt()

    private fun updateLiveStatus() {
        view.visibility = if (isPlaybackIdle) View.GONE else if (shouldPresentSeekbar()) View.VISIBLE else View.GONE
    }

    open fun shouldPresentSeekbar() = core.activePlayback?.mediaType == Playback.MediaType.VOD

    override fun destroy() {
        view.setOnTouchListener(null)
        handler.removeCallbacksAndMessages(null)
        stopPlaybackListeners()
        super.destroy()
    }
}
