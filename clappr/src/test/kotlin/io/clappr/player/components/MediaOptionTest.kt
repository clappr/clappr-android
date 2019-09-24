package io.clappr.player.components

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MediaOptionTest {

    private lateinit var playback: Playback

    @Before
    fun setUp() {
        playback = mock()
    }

    @Test
    fun `should create media options json with audio and subtitle`() {

        whenever(playback.selectedAudio) doReturn "eng"
        whenever(playback.selectedSubtitle) doReturn "por"

        val mediaOptionsJson = playback.buildMediaOptionsJson()
        val expectedJson = """{"media_option":[{"name":"por","type":"SUBTITLE"},{"name":"eng","type":"AUDIO"}]}}"""

        assertEquals(expectedJson, mediaOptionsJson)
    }

    @Test
    fun `should create media options json only with subtitle`() {
        whenever(playback.selectedSubtitle) doReturn "por"

        val mediaOptionsJson = playback.buildMediaOptionsJson()
        val expectedJson = """{"media_option":[{"name":"por","type":"SUBTITLE"}]}}"""

        assertEquals(expectedJson, mediaOptionsJson)
    }
}