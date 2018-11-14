package io.clappr.player.base

import org.junit.Test
import kotlin.test.assertEquals

class EventTest {

    @Test
    fun shouldHaveUniqueValue() {
        Event.values().forEach {
            assertEquals(1, Event.values().filter { event -> event.value == it.value }.size, "More than 1 event with \"${it.value}\" value")
        }
    }
}