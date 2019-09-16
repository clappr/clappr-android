package io.clappr.player.components

import org.junit.Assert.assertEquals
import org.junit.Test

class MediaOptionTest {

    @Test
    fun `should create media options json with audio and subtitle`() {
        val mediaOptionsJson = buildMediaOptionsJson("por", "eng")
        val expectedJson = """{"media_option":[{"name":"por","type":"SUBTITLE"},{"name":"eng","type":"AUDIO"}]}}"""

        assertEquals(expectedJson, mediaOptionsJson)
    }

    @Test
    fun `should create media options json only with subtitle`() {
        val mediaOptionsJson = buildMediaOptionsJson("por", null)
        val expectedJson = """{"media_option":[{"name":"por","type":"SUBTITLE"}]}}"""

        assertEquals(expectedJson, mediaOptionsJson)
    }
}