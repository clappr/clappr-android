package io.clappr.player.utils

import java.util.*

object IdGenerator {
    fun uniqueId() = "${UUID.randomUUID()}"
}