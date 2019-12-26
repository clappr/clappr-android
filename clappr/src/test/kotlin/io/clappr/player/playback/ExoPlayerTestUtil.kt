package io.clappr.player.playback

import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.source.TrackGroup
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import io.mockk.every
import io.mockk.mockk

fun buildAvailableTracks(vararg renderers: Pair<Int, List<String?>>): MappingTrackSelector {
    val rendererMap = renderers.toMap()
    val trackSelector = mockk<DefaultTrackSelector>(relaxUnitFun = true)
    val mappedTrackInfo = mockk<MappingTrackSelector.MappedTrackInfo>()

    every { trackSelector.currentMappedTrackInfo } returns mappedTrackInfo
    every { mappedTrackInfo.rendererCount } returns rendererMap.size

    rendererMap.keys.forEachIndexed { index, rendererType ->
        val languages = rendererMap[rendererType].orEmpty()
        val formats = languages.map { createFormat(rendererType, it) }
        val trackGroups = formats.map { TrackGroup(it) }.toTypedArray()
        val trackGroupArray = TrackGroupArray(*trackGroups)

        every { mappedTrackInfo.getRendererType(index) } returns rendererType
        every { mappedTrackInfo.getTrackGroups(index) } returns trackGroupArray
    }

    return trackSelector
}

private fun createFormat(renderType: Int, language: String?) = when (renderType) {
    C.TRACK_TYPE_AUDIO -> createAudioFormat(language)
    C.TRACK_TYPE_TEXT -> createTextFormat(language)
    else -> createSampleFormat()
}

private fun createAudioFormat(language: String?) =
    Format.createAudioSampleFormat(null, null, null, 0, 0, 0, 0, null, null, 0, language)

private fun createTextFormat(language: String?) =
    Format.createTextSampleFormat(null, null, 0, language)

private fun createSampleFormat() =
    Format.createSampleFormat(null, null, 0)