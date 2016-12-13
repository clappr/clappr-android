package io.clappr.player.log

import android.util.Log

object Logger {
    var logLevel = LogLevel.INFO
    var tag = "clappr"

    fun setLevel(level: LogLevel) {
        logLevel = level
    }

    fun log(level: LogLevel, message: String, scope: String? = null) {
        if (level <= logLevel) {
            val formatted = formattedMessage(message, scope)

            when (level) {
                LogLevel.DEBUG -> Log.d(tag, formatted)
                LogLevel.INFO -> Log.i(tag, formatted)
                LogLevel.WARNING -> Log.w(tag, formatted)
                LogLevel.ERROR -> Log.e(tag, formatted)
            }
        }
    }

    fun formattedMessage(message: String, scope: String? = null): String {
        return if (scope != null) String.format("[%s] %s", scope, message) else message
    }

    fun error(message: String, scope: String? = null) {
        log(LogLevel.ERROR, message, scope)
    }

    fun warning(message: String, scope: String? = null) {
        log(LogLevel.WARNING, message, scope)
    }

    fun info(message: String, scope: String? = null) {
        log(LogLevel.INFO, message, scope)
    }

    fun debug(message: String, scope: String? = null) {
        log(LogLevel.DEBUG, message, scope)
    }
}