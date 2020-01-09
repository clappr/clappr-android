package io.clappr.player.utils

import org.junit.Before
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals

class IdGeneratorTest {

    @Before
    fun setUp() {
        val countField = IdGenerator::class.java.getDeclaredField("count")
        countField.isAccessible = true
        countField.set(IdGenerator, AtomicInteger())
    }

    @Test
    fun `should generate a sequential unique id starting from 1`() {
        assertEquals("1", IdGenerator.uniqueId())
        assertEquals("2", IdGenerator.uniqueId())
        assertEquals("3", IdGenerator.uniqueId())
    }

    @Test
    fun `should generate a sequential unique with provided prefix`() {
        assertEquals("o1", IdGenerator.uniqueId("o"))
        assertEquals("prefix2", IdGenerator.uniqueId("prefix"))
    }
}