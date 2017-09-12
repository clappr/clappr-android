package io.clappr.player.log

import android.util.Log
import io.clappr.player.BuildConfig

object Logger {
    var tag = "Clappr"

    fun log(level: LogLevel, scope: String? = null, message: String) {
        val formatted = formattedMessage(scope, message)

        when (level) {
            LogLevel.DEBUG -> Log.d(tag, formatted)
            LogLevel.INFO -> Log.i(tag, formatted)
            LogLevel.WARNING -> Log.w(tag, formatted)
            LogLevel.ERROR -> Log.e(tag, formatted)
            LogLevel.OFF -> {}
        }
    }

    fun formattedMessage(scope: String? = null, message: String): String {
        return if (scope != null) String.format("[%s] %s", scope, message) else message
    }

    fun error(scope: String? = null, message: String, exception: Exception? = null) {
        log(LogLevel.ERROR, scope, message)
        exception?.printStackTrace()
    }

    fun warning(scope: String? = null, message: String) {
        log(LogLevel.WARNING, scope, message)
    }

    fun info(scope: String? = null, message: String) {
        log(LogLevel.INFO, scope, message)
    }

    fun debug(scope: String? = null, message: String) {
        log(LogLevel.DEBUG, scope, message)
    }
}
