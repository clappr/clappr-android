package io.clappr.player.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

object Utils {
    private var count = 0
    fun uniqueId(prefix: String? = null) : String {
        return (prefix?:"") + ++count;
    }


    fun broadcastReceiver(handlerFn: (Context?, Intent?) -> Unit?) : BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                handlerFn(context, intent)
            }
        }
    }
}