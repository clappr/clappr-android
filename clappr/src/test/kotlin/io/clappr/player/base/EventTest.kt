package io.clappr.player.base

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class EventTest {

    @Test
    fun shouldHaveUniqueValue() {
        Event.values().forEach {
            assertEquals(1, Event.values().filter { event -> event.value == it.value  }.size, "More than 1 event with \"${it.value}\" value")
        }
    }
}