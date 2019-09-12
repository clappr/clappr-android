package io.clappr.player.playback

import com.google.android.exoplayer2.C.TRACK_TYPE_AUDIO
import com.google.android.exoplayer2.C.TRACK_TYPE_TEXT
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.TrackGroup
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.FixedTrackSelection
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import kotlin.test.assertEquals


class ExoPlayerExtensionsTest {

    @Test
    fun `should load audio tracks`() {
        val trackSelector = buildTrackSelector(
            mapOf(
                TRACK_TYPE_AUDIO to listOf("por"),
                TRACK_TYPE_TEXT to listOf("eng")
            )
        )

        val expectedTracks = listOf(
            TrackInfo(TRACK_TYPE_AUDIO, 0, 0, 0, "por")
        )

        assertEquals(expectedTracks, trackSelector.audioTracks())
    }

    @Test
    fun `should load subtitle tracks`() {
        val trackSelector = buildTrackSelector(
            mapOf(
                TRACK_TYPE_AUDIO to listOf("por"),
                TRACK_TYPE_TEXT to listOf("por", "eng")
            )
        )

        val expectedTracks = listOf(
            TrackInfo(TRACK_TYPE_TEXT, 1, 0, 0, "por"),
            TrackInfo(TRACK_TYPE_TEXT, 1, 1, 0, "eng")
        )

        assertEquals(expectedTracks, trackSelector.subtitleTracks())
    }

    @Test
    fun `should load empty audio tracks when audio renderer is not setup`() {
        val trackSelector = buildTrackSelector(emptyMap())
        assertEquals(emptyList(), trackSelector.audioTracks())
    }

    @Test
    fun `should load empty subtitle tracks when subtitle renderer is not setup`() {
        val trackSelector = buildTrackSelector(emptyMap())
        assertEquals(emptyList(), trackSelector.subtitleTracks())
    }

    @Test
    fun `should load audio tracks with lowercase language`() {
        val trackSelector = buildTrackSelector(
            mapOf(
                TRACK_TYPE_AUDIO to listOf("Por", "eNG")
            )
        )

        val expectedTracks = listOf(
            TrackInfo(TRACK_TYPE_AUDIO, 0, 0, 0, "por"),
            TrackInfo(TRACK_TYPE_AUDIO, 0, 1, 0, "eng")
        )

        assertEquals(expectedTracks, trackSelector.audioTracks())
    }

    @Test
    fun `should load subtitle tracks with lowercase language`() {
        val trackSelector = buildTrackSelector(
            mapOf(
                TRACK_TYPE_TEXT to listOf("Por")
            )
        )

        val expectedTracks = listOf(
            TrackInfo(TRACK_TYPE_TEXT, 0, 0, 0, "por")
        )

        assertEquals(expectedTracks, trackSelector.subtitleTracks())
    }

    @Test
    fun `should load audio tracks with und language when language is null`() {
        val trackSelector = buildTrackSelector(
            mapOf(
                TRACK_TYPE_AUDIO to listOf(null)
            )
        )

        val expectedTracks = listOf(
            TrackInfo(TRACK_TYPE_AUDIO, 0, 0, 0, "und")
        )

        assertEquals(expectedTracks, trackSelector.audioTracks())
    }

    @Test
    fun `should load subtitle tracks with und language when language is null`() {
        val trackSelector = buildTrackSelector(
            mapOf(
                TRACK_TYPE_TEXT to listOf(null)
            )
        )

        val expectedTracks = listOf(
            TrackInfo(TRACK_TYPE_TEXT, 0, 0, 0, "off")
        )

        assertEquals(expectedTracks, trackSelector.subtitleTracks())
    }

    @Test
    fun `should load audio tracks with ISO 639-3 standard language for specific cases`() {
        val trackSelector = buildTrackSelector(
            mapOf(
                TRACK_TYPE_AUDIO to listOf("original", "pt", "en")
            )
        )

        val expectedTracks = listOf(
            TrackInfo(TRACK_TYPE_AUDIO, 0, 0, 0, "und"),
            TrackInfo(TRACK_TYPE_AUDIO, 0, 1, 0, "por"),
            TrackInfo(TRACK_TYPE_AUDIO, 0, 2, 0, "eng")
        )

        assertEquals(expectedTracks, trackSelector.audioTracks())
    }

    private fun buildTrackSelector(rendererMap: Map<Int, List<String?>>): MappingTrackSelector {
        val trackSelector = mock<DefaultTrackSelector>()
        val mappedTrackInfo = mock<MappingTrackSelector.MappedTrackInfo>()

        whenever(trackSelector.currentMappedTrackInfo).thenReturn(mappedTrackInfo)
        whenever(mappedTrackInfo.rendererCount).thenReturn(rendererMap.size)

        rendererMap.keys.forEachIndexed { index, rendererType ->
            whenever(mappedTrackInfo.getRendererType(index)).thenReturn(rendererType)
        }

        rendererMap.keys.forEachIndexed { index, rendererType ->

            val languages = rendererMap[rendererType].orEmpty()

            val formats = languages.map { createFormat(rendererType, it) }

            val trackGroups = formats.map { TrackGroup(it) }.toTypedArray()

            val trackGroupArray = TrackGroupArray(*trackGroups)

            whenever(mappedTrackInfo.getTrackGroups(index)).thenReturn(trackGroupArray)
        }

        return trackSelector
    }

    private fun createFormat(renderType: Int, language: String?) = when (renderType) {
        TRACK_TYPE_AUDIO -> createAudioFormat(language)
        TRACK_TYPE_TEXT -> createTextFormat(language)
        else -> createSampleFormat()
    }

    private fun createAudioFormat(language: String?) = Format.createAudioSampleFormat(
        null,
        null,
        null,
        0,
        0,
        0,
        0,
        null,
        null,
        0,
        language
    )

    private fun createTextFormat(language: String?) = Format.createTextSampleFormat(
        null,
        null,
        0,
        language
    )

    private fun createSampleFormat() = Format.createSampleFormat(
        null,
        null,
        0
    )
}
