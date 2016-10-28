package com.globo.clappr.log

import android.util.Log

class Logger {
    private var logLevel = LogLevel.INFO

    fun setLevel(level: LogLevel) {
        logLevel = level
    }

    fun log(level: LogLevel, scope: String? = null, message: String) {
        if (level <= logLevel) {
            val tag = "clappr"
            val formatted = formattedMessage(message, scope)

            when (level) {
                LogLevel.DEBUG -> Log.d(tag, formatted)
                LogLevel.INFO -> Log.i(tag, formatted)
                LogLevel.WARNING -> Log.w(tag, formatted)
                LogLevel.ERROR -> Log.e(tag, formatted)
            }
        }
    }

    fun formattedMessage(scope: String? = null, message: String): String {
        return if (scope != null) String.format("[%s] %s", scope, message) else message
    }

    fun error(message: String, scope: String? = null) {
        log(LogLevel.ERROR, scope, message)
    }

    fun warning(message: String, scope: String? = null) {
        log(LogLevel.WARNING, scope, message)
    }

    fun info(message: String, scope: String? = null) {
        log(LogLevel.INFO, scope, message)
    }

    fun debug(message: String, scope: String? = null) {
        log(LogLevel.DEBUG, scope, message)
    }
}