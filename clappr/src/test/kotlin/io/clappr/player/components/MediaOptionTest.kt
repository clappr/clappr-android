package io.clappr.player.components

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MediaOptionTest {

    private lateinit var playback: Playback

    @Before
    fun setUp() {
        playback = mockk()
    }

    @Test
    fun `should create media options json with audio and subtitle`() {
        every { playback.selectedAudio } returns "eng"
        every { playback.selectedSubtitle } returns "por"

        val mediaOptionsJson = playback.buildMediaOptionsJson()
        val expectedJson = """{"media_option":[{"name":"por","type":"SUBTITLE"},{"name":"eng","type":"AUDIO"}]}}"""

        assertEquals(expectedJson, mediaOptionsJson)
    }

    @Test
    fun `should create media options json only with subtitle`() {
        every { playback.selectedAudio } returns null
        every { playback.selectedSubtitle } returns "por"

        val mediaOptionsJson = playback.buildMediaOptionsJson()
        val expectedJson = """{"media_option":[{"name":"por","type":"SUBTITLE"}]}}"""

        assertEquals(expectedJson, mediaOptionsJson)
    }
}