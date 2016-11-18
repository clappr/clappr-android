package com.globo.clappr.timer
import java.util.*

class TimerManager(val period: Long = 200, val callBack: () -> Unit) {
    var timer: Timer? = null

    fun start() {
        stop()
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                callBack()
            }
        }, 0, period)
    }

    fun stop() {
        timer?.cancel()
    }
}