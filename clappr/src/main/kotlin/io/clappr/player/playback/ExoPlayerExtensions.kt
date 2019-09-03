package io.clappr.player.playback

import com.google.android.exoplayer2.C.TRACK_TYPE_AUDIO
import com.google.android.exoplayer2.C.TRACK_TYPE_TEXT
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import io.clappr.player.components.AudioLanguage.ORIGINAL
import io.clappr.player.components.SubtitleLanguage.OFF

val Player.selectedSubtitle
    get() = currentTrackSelections.get(TRACK_TYPE_TEXT)?.selectedFormat?.language ?: OFF.value

val Player.selectedAudio
    get() = currentTrackSelections.get(TRACK_TYPE_AUDIO)?.selectedFormat?.language ?: ORIGINAL.value

fun MappingTrackSelector.tracks(): List<TrackInfo> {

    val tracks = mutableListOf<TrackInfo>()

    val mappedTrackInfo = currentMappedTrackInfo ?: return tracks

    for (rendererIndex in 0 until mappedTrackInfo.rendererCount) {

        val rendererType = mappedTrackInfo.getRendererType(rendererIndex)
        val trackGroupArray = mappedTrackInfo.getTrackGroups(rendererIndex)

        for (trackGroupIndex in 0 until trackGroupArray.length) {

            val trackGroup = trackGroupArray.get(trackGroupIndex)

            for (formatIndex in 0 until trackGroup.length) {

                val format = trackGroup.getFormat(formatIndex)
                val language = format.language ?: when (rendererType) {
                    TRACK_TYPE_AUDIO -> ORIGINAL.value
                    else -> OFF.value
                }

                tracks += TrackInfo(
                    rendererType,
                    rendererIndex,
                    trackGroupIndex,
                    formatIndex,
                    language
                )
            }
        }
    }

    return tracks
}

fun MappingTrackSelector.audioTracks() = tracks().filter {
    currentMappedTrackInfo?.getRendererType(it.rendererIndex) == TRACK_TYPE_AUDIO
}

fun MappingTrackSelector.subtitleTracks() = tracks().filter {
    currentMappedTrackInfo?.getRendererType(it.rendererIndex) == TRACK_TYPE_TEXT
}

data class TrackInfo(
    val rendererType: Int,
    val rendererIndex: Int,
    val trackGroupIndex: Int,
    val formatIndex: Int,
    val language: String
)