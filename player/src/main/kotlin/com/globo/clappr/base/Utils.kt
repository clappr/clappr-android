package com.globo.clappr.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

object Utils {
    var count = 0
    fun uniqueId(prefix: String? = null) : String {
        return (prefix?:"") + ++count;
    }


    fun broadcastReceiver(handlerFn: (Context?, Intent?) -> Unit?) : BroadcastReceiver {
        return object : BroadcastReceiver() {
            public override fun onReceive(context: Context?, intent: Intent?) {
                handlerFn(context, intent)
            }
        }
    }
}