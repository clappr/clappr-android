package io.clappr.player.utils

import org.junit.Test
import java.util.*
import kotlin.test.fail

class IdGeneratorTest {

    @Test
    fun `should generate UUID as id`() {

        val uniqueId = IdGenerator.uniqueId()

        try {
            UUID.fromString(uniqueId)
        } catch (e: Exception) {
            fail("invalid UUID String")
        }
    }
}