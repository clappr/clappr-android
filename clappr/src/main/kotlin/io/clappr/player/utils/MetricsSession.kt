package io.clappr.player.utils

import java.util.*

class MetricsSession {

    var videoSessionId = UUID.randomUUID().toString()
        private set

    fun renewSessionId() {
        videoSessionId = UUID.randomUUID().toString()
    }
}