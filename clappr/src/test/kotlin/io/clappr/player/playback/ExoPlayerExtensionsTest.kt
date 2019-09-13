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
        val trackSelector = buildAvailableTracks(
            TRACK_TYPE_AUDIO to listOf("por"),
            TRACK_TYPE_TEXT to listOf("eng")
        )

        val expectedTracks = listOf(
            TrackInfo(TRACK_TYPE_AUDIO, 0, 0, 0, "por")
        )

        assertEquals(expectedTracks, trackSelector.audioTracks())
    }

    @Test
    fun `should load subtitle tracks`() {
        val trackSelector = buildAvailableTracks(
            TRACK_TYPE_AUDIO to listOf("por"),
            TRACK_TYPE_TEXT to listOf("por", "eng")
        )

        val expectedTracks = listOf(
            TrackInfo(TRACK_TYPE_TEXT, 1, 0, 0, "por"),
            TrackInfo(TRACK_TYPE_TEXT, 1, 1, 0, "eng")
        )

        assertEquals(expectedTracks, trackSelector.subtitleTracks())
    }

    @Test
    fun `should load empty audio tracks when audio renderer is not setup`() {
        val trackSelector = buildAvailableTracks()
        assertEquals(emptyList(), trackSelector.audioTracks())
    }

    @Test
    fun `should load empty subtitle tracks when subtitle renderer is not setup`() {
        val trackSelector = buildAvailableTracks()
        assertEquals(emptyList(), trackSelector.subtitleTracks())
    }

    @Test
    fun `should load audio tracks with lowercase language`() {
        val trackSelector = buildAvailableTracks(
            TRACK_TYPE_AUDIO to listOf("Por", "eNG")
        )

        val expectedTracks = listOf(
            TrackInfo(TRACK_TYPE_AUDIO, 0, 0, 0, "por"),
            TrackInfo(TRACK_TYPE_AUDIO, 0, 1, 0, "eng")
        )

        assertEquals(expectedTracks, trackSelector.audioTracks())
    }

    @Test
    fun `should load subtitle tracks with lowercase language`() {
        val trackSelector = buildAvailableTracks(
            TRACK_TYPE_TEXT to listOf("Por")
        )

        val expectedTracks = listOf(
            TrackInfo(TRACK_TYPE_TEXT, 0, 0, 0, "por")
        )

        assertEquals(expectedTracks, trackSelector.subtitleTracks())
    }

    @Test
    fun `should load audio tracks with und language when language is null`() {
        val trackSelector = buildAvailableTracks(
            TRACK_TYPE_AUDIO to listOf(null)
        )

        val expectedTracks = listOf(
            TrackInfo(TRACK_TYPE_AUDIO, 0, 0, 0, "und")
        )

        assertEquals(expectedTracks, trackSelector.audioTracks())
    }

    @Test
    fun `should load subtitle tracks with und language when language is null`() {
        val trackSelector = buildAvailableTracks(
            TRACK_TYPE_TEXT to listOf(null)
        )

        val expectedTracks = listOf(
            TrackInfo(TRACK_TYPE_TEXT, 0, 0, 0, "off")
        )

        assertEquals(expectedTracks, trackSelector.subtitleTracks())
    }

    @Test
    fun `should load audio tracks with ISO 639-3 standard language for specific cases`() {
        val trackSelector = buildAvailableTracks(
            TRACK_TYPE_AUDIO to listOf("original", "pt", "en")
        )

        val expectedTracks = listOf(
            TrackInfo(TRACK_TYPE_AUDIO, 0, 0, 0, "und"),
            TrackInfo(TRACK_TYPE_AUDIO, 0, 1, 0, "por"),
            TrackInfo(TRACK_TYPE_AUDIO, 0, 2, 0, "eng")
        )

        assertEquals(expectedTracks, trackSelector.audioTracks())
    }

    @Test
    fun `should return the default audio language when there is no audios available`() {
        val trackSelector = buildAvailableTracks()

        val player = mock<Player>()

        assertEquals(null, player.getSelectedAudio(trackSelector))
    }

    @Test
    fun `should return the default subtitle language when there is no subtitles available`() {
        val trackSelector = buildAvailableTracks()

        val player = mock<Player>()

        assertEquals("off", player.getSelectedSubtitle(trackSelector))
    }


    @Test
    fun `should return the selected audio language based on selected audio track`() {
        val trackSelector = buildAvailableTracks(
            TRACK_TYPE_AUDIO to listOf("por", "eng", "und")
        )

        val player = mock<Player>()

        val trackSelections = buildSelectedTracks(
            trackSelector,
            TRACK_TYPE_AUDIO to "eng"
        )

        whenever(player.currentTrackSelections).doReturn(trackSelections)

        assertEquals("eng", player.getSelectedAudio(trackSelector))
    }

    @Test
    fun `should return the selected subtitle language based on selected subtitle track`() {
        val trackSelector = buildAvailableTracks(
            TRACK_TYPE_TEXT to listOf("por", "eng")
        )

        val player = mock<Player>()

        val trackSelections = buildSelectedTracks(
            trackSelector,
            TRACK_TYPE_TEXT to "por"
        )

        whenever(player.currentTrackSelections).doReturn(trackSelections)

        assertEquals("por", player.getSelectedSubtitle(trackSelector))
    }

    private fun buildAvailableTracks(vararg renderers: Pair<Int, List<String?>>): MappingTrackSelector {
        val rendererMap = renderers.toMap()
        val trackSelector = mock<DefaultTrackSelector>()
        val mappedTrackInfo = mock<MappingTrackSelector.MappedTrackInfo>()

        whenever(trackSelector.currentMappedTrackInfo).thenReturn(mappedTrackInfo)
        whenever(mappedTrackInfo.rendererCount).thenReturn(rendererMap.size)

        rendererMap.keys.forEachIndexed { index, rendererType ->
            val languages = rendererMap[rendererType].orEmpty()
            val formats = languages.map { createFormat(rendererType, it) }
            val trackGroups = formats.map { TrackGroup(it) }.toTypedArray()
            val trackGroupArray = TrackGroupArray(*trackGroups)

            whenever(mappedTrackInfo.getRendererType(index)).thenReturn(rendererType)
            whenever(mappedTrackInfo.getTrackGroups(index)).thenReturn(trackGroupArray)
        }

        return trackSelector
    }

    private fun buildSelectedTracks(
        trackSelector: MappingTrackSelector,
        vararg selections: Pair<Int, String>
    ): TrackSelectionArray {
        val selectionMap = selections.toMap()
        val mappedTrackInfo = trackSelector.currentMappedTrackInfo!!

        val trackSelections = selectionMap.map { (rendererType, selectedLanguage) ->
            val rendererIndex = (0 until mappedTrackInfo.rendererCount).first {
                mappedTrackInfo.getRendererType(it) == rendererType
            }
            val trackGroupArray = mappedTrackInfo.getTrackGroups(rendererIndex)
            val trackGroup = (0 until trackGroupArray.length)
                .map { trackGroupArray.get(it) }
                .first { it.getFormat(0).language == selectedLanguage }

            FixedTrackSelection(trackGroup, 0)
        }

        return TrackSelectionArray(*trackSelections.toTypedArray())
    }

    private fun createFormat(renderType: Int, language: String?) = when (renderType) {
        TRACK_TYPE_AUDIO -> createAudioFormat(language)
        TRACK_TYPE_TEXT -> createTextFormat(language)
        else -> createSampleFormat()
    }

    private fun createAudioFormat(language: String?) =
        Format.createAudioSampleFormat(null, null, null, 0, 0, 0, 0, null, null, 0, language)

    private fun createTextFormat(language: String?) =
        Format.createTextSampleFormat(null, null, 0, language)

    private fun createSampleFormat() =
        Format.createSampleFormat(null, null, 0)
}
