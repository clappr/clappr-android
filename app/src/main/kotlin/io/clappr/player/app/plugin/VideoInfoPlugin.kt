package io.clappr.player.app.plugin

import android.support.annotation.Keep
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import io.clappr.player.app.R
import io.clappr.player.base.Callback
import io.clappr.player.base.Event
import io.clappr.player.base.InternalEvent
import io.clappr.player.base.NamedType
import io.clappr.player.components.Core
import io.clappr.player.plugin.control.MediaControl

open class VideoInfoPlugin(core: Core) : MediaControl.Plugin(core) {

    enum class Option(val value: String) {
        TITLE("$name:title"),

        SUBTITLE("$name:subtitle")
    }

    @Keep
    companion object : NamedType {
        override val name: String?
            get() = "videoInfo"
    }

    override var panel: Panel = Panel.TOP

    override var position: Position = Position.LEFT

    override val view by lazy {
        LayoutInflater.from(context).inflate(R.layout.video_info_plugin, null) as LinearLayout
    }

    open val titleLabel by lazy { view.findViewById(R.id.title_label) as TextView }

    open val subtitleLabel by lazy { view.findViewById(R.id.subtitle_label) as TextView }

    private val playbackListenerIds = mutableListOf<String>()

    private val title
            get() = core.activeContainer?.options?.get(Option.TITLE.value) as? String

    private val subtitle
            get() = core.activeContainer?.options?.get(Option.SUBTITLE.value) as? String

    init {
        bindCoreEvents()
    }

    override fun render() {
        super.render()
        show()
    }

    private fun bindCoreEvents() {
        listenTo(core, InternalEvent.DID_CHANGE_ACTIVE_PLAYBACK.value, Callback.wrap { bindPlaybackEvents() })
        listenTo(core, InternalEvent.DID_ENTER_FULLSCREEN.value, Callback.wrap { updateVideoInfo() })
        listenTo(core, InternalEvent.DID_EXIT_FULLSCREEN.value, Callback.wrap { updateVideoInfo() })
    }

    private fun bindPlaybackEvents() {
        stopPlaybackListeners()
        updateVideoInfo()
        core.activePlayback?.let {
            playbackListenerIds.add(listenTo(it, Event.DID_COMPLETE.value, Callback.wrap { hide() }))
        }
    }

    private fun updateVideoInfo() {
        titleLabel.text = title
        subtitleLabel.text = subtitle

        if (core.fullscreenState == Core.FullscreenState.FULLSCREEN) {
            subtitleLabel.visibility = View.VISIBLE
        } else {
            subtitleLabel.visibility = View.GONE
        }
    }

    private fun stopPlaybackListeners() {
        playbackListenerIds.forEach(::stopListening)
        playbackListenerIds.clear()
    }
}