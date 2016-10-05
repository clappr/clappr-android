package com.globo.clappr.log

enum class LogLevel(level: Int) {
    OFF(0),
    ERROR(1),
    WARNING(2),
    INFO(3),
    DEBUG(4);

    fun description(): String {
        when (this) {
            DEBUG -> return "DEBUG"
            INFO -> return "INFO"
            WARNING -> return "WARNING"
            ERROR -> return "ERROR"
            else -> return ""
        }
    }
}