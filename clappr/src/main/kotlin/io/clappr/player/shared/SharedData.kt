package io.clappr.player.shared

import io.clappr.player.utils.MetricsSession

class SharedData {
    var metricsSession: MetricsSession

    private val metricsSessionKey = "metricsSession"
    private val storeMap  = mutableMapOf<String, Any>()

    init {
        metricsSession = createMetricsSessionIfNeeded()
    }

    private fun createMetricsSessionIfNeeded() =
            storeMap[metricsSessionKey]?.let { it as MetricsSession } ?: setupMetrics()

    private fun setupMetrics() : MetricsSession {
        val metricsSession = MetricsSession()
        storeMap[metricsSessionKey] = metricsSession
        return metricsSession
    }
}