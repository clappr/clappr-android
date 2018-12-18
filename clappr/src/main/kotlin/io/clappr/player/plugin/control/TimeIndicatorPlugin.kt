package io.clappr.player.plugin.control

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import io.clappr.player.R
import io.clappr.player.base.*
import io.clappr.player.components.Core
import io.clappr.player.components.Playback
import io.clappr.player.extensions.asTimeInterval

open class TimeIndicatorPlugin(core: Core) : MediaControl.Plugin(core) {

    companion object : NamedType {
        override val name: String
            get() = "timeIndicator"
    }

    override var panel: Panel = Panel.BOTTOM
    override var position: Position = Position.LEFT

    protected val textView by lazy { LayoutInflater.from(applicationContext).inflate(R.layout.time_indicator, null) as TextView }

    override val view: View?
        get() = textView

    private val playbackListenerIds = mutableListOf<String>()

    init {
        listenTo(core, InternalEvent.DID_CHANGE_ACTIVE_PLAYBACK.value, Callback.wrap { setupPlaybackListeners() })
        updateLiveStatus()
    }

    private fun setupPlaybackListeners() {
        updateLiveStatus()
        stopPlaybackListeners()
        core.activePlayback?.let {
            updateValue(null)
            playbackListenerIds.add(listenTo(it, Event.DID_LOAD_SOURCE.value, Callback.wrap { _ -> setupPlaybackListeners() }))
            playbackListenerIds.add(listenTo(it, Event.DID_UPDATE_POSITION.value, Callback.wrap { bundle -> updateValue(bundle) }))
                    playbackListenerIds.add(listenTo(it, Event.DID_COMPLETE.value, Callback.wrap { _ -> hide() }))
        }
    }

    private fun stopPlaybackListeners() {
        playbackListenerIds.forEach(::stopListening)
        playbackListenerIds.clear()
    }

    private fun updateLiveStatus() {
        val isVOD = core.activePlayback?.mediaType == Playback.MediaType.VOD
        view?.visibility = if (!isVOD || isPlaybackIdle) View.GONE else View.VISIBLE
    }

    private fun updateValue(bundle: Bundle?) {
        (bundle ?: Bundle()).let {
            val position = it.getDouble("time", 0.0)
            val duration = core.activePlayback?.duration ?: 0.0
            textView.text = "%s / %s".format(position.asTimeInterval(), duration.asTimeInterval())
        }
        updateLiveStatus()
    }

    override fun render() {
        val height = applicationContext.resources?.getDimensionPixelSize(R.dimen.time_indicator_height) ?: 0
        val layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, height)
        layoutParams.gravity = Gravity.CENTER_VERTICAL
        textView.layoutParams = layoutParams
        textView.text = "00:00 / 00:00"
    }

    override fun destroy() {
        stopPlaybackListeners()
        super.destroy()
    }
}