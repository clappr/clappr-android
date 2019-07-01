package io.clappr.player.utils

import org.junit.Assert.*
import org.junit.Test

class EnvironmentTest {

    @Test
    fun `should have a not empty playerId`() {
        val environment = Environment()

        assertFalse(environment.playerId.isEmpty())
    }
}