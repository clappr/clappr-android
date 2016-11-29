package io.clappr.player.periodicTimer

import android.os.Handler

class PeriodicTimeElapsedHandler(val interval: Long, val function: () -> Unit) : Handler() {

    private var timeElapsedRunnable: TimeElapsedRunnable? = null

    fun start() {
        timeElapsedRunnable?.cancel()
        timeElapsedRunnable = TimeElapsedRunnable(this)
        postDelayed(timeElapsedRunnable, interval)
    }

    fun cancel() {
        timeElapsedRunnable?.cancel()
        removeCallbacks(timeElapsedRunnable)
        timeElapsedRunnable = null
    }

    inner class TimeElapsedRunnable(val handler: Handler) : Runnable {

        private var canceled: Boolean = false

        override fun run() {

            if (!canceled) {
                function.invoke()
                handler.postDelayed(this, interval)
            }
        }

        fun cancel() {
            canceled = true
        }
    }
}