package io.clappr.player.log

enum class LogLevel {
    OFF,
    ERROR,
    WARNING,
    INFO,
    DEBUG;

    fun description(): String {
        return when (this) {
            DEBUG -> "DEBUG"
            INFO -> "INFO"
            WARNING -> "WARNING"
            ERROR -> "ERROR"
            else -> ""
        }
    }
}