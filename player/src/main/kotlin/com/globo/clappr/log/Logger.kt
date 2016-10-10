package com.globo.clappr.log

import android.util.Log

class Logger {
    private var logLevel = LogLevel.INFO

    fun setLevel(level: LogLevel) {
        logLevel = level
    }

    fun log(level: LogLevel, scope: String?, message: String) {
        if (level <= logLevel) {
            val tag = scope ?: "clappr"
            Log.v(tag , String.format("\n%s %s", level.description(), message))
        }
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