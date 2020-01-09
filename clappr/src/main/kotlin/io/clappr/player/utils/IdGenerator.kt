package io.clappr.player.utils

import java.util.concurrent.atomic.AtomicInteger

object IdGenerator {
    private var count = AtomicInteger()
    fun uniqueId(prefix: String? = null) = "${prefix.orEmpty()}${count.incrementAndGet()}"
}