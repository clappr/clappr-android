package io.clappr.player.utils

import org.junit.Assert.*
import org.junit.Test

class MetricsSessionTest {

    private val metricsSession: MetricsSession by lazy { MetricsSession() }

    @Test
    fun `should have a unique ID when created`() {
        assertNotNull(metricsSession.videoSessionId)
    }

    @Test
    fun `should have a constant unique ID for all calls`() {
        val firstId = metricsSession.videoSessionId
        val secondId = metricsSession.videoSessionId

        assertEquals(firstId, secondId)
    }

    @Test
    fun `should change unique ID when rewind is called`() {
        val firstId = metricsSession.videoSessionId

        metricsSession.renewSessionId()

        val secondId = metricsSession.videoSessionId

        assertNotEquals(firstId, secondId)
    }
}