package com.globo.clappr.log

import android.util.Log

class Logger {
    private var logLevel = LogLevel.INFO

    fun setLevel(level: LogLevel) {
        logLevel = level
    }

    fun log(level: LogLevel, message: String) {
        if (level <= logLevel) {
            Log.v("Clappr", String.format("\n%s %s", level.description(), message))
        }
    }

    fun log(level: LogLevel, scope: String?, message: String) {
        if (scope != null) {
            log(level, String.format("[%s] %s", scope, message))
        } else {
            log(level, message)
        }
    }

    fun error(message: String, scope: String?) {
        log(LogLevel.ERROR, scope, message)
    }

    fun warning(message: String, scope: String?) {
        log(LogLevel.WARNING, scope, message)
    }

    fun info(message: String, scope: String?) {
        log(LogLevel.INFO, scope, message)
    }

    fun debug(message: String, scope: String?) {
        log(LogLevel.DEBUG, scope, message)
    }
}