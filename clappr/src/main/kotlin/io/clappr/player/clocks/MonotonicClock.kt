package io.clappr.player.clocks

import android.os.SystemClock

typealias MonotonicClock = () -> Long

val ClapprSystemClock: MonotonicClock = { SystemClock.uptimeMillis() }