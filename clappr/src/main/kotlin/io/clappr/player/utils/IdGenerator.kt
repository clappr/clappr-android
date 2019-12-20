package io.clappr.player.utils

object IdGenerator {
    private var count = 0
    fun uniqueId(prefix: String? = null) = "${prefix.orEmpty()}${++count}"
}