package io.clappr.player.extensions

fun Double.asTimeInterval(): String {
    val hours = this.toInt() / 3600
    val hoursStr = "%02d".format(hours)
    val minutesStr = "%02d".format((this.toInt() / 60) % 60)
    val secondsStr = "%02d".format(this.toInt() % 60)

    return if (hours > 0) "$hoursStr:$minutesStr:$secondsStr" else "$minutesStr:$secondsStr"
}