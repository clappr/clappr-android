package io.clappr.player.utils

import org.junit.Assert.*
import org.junit.Test

class EnvironmentTest {

    @Test
    fun `should have a not empty sessionId`() {
        val environment = Environment()

        assertFalse(environment.playerId.isEmpty())
    }
}