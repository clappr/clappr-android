package com.globo.clappr.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import com.globo.clappr.components.PlayerInfo

public open class BaseObject(options: Map<String, Object>?) {
    val context: Context
    init {
        context = options?.get("context") as Context
    }
    val id =  Utils.uniqueId("o")

    private var receivers: MutableMap<Any, BroadcastReceiver> = hashMapOf()

    fun on(eventName: String, handler: ((Intent?) -> Unit)?, obj: BaseObject = this) {
        val bm = LocalBroadcastManager.getInstance(context.applicationContext)
        val receiver = Utils.broadcastReceiver { context: Context?, intent: Intent? ->
            val objContext = intent?.getStringExtra("clappr:baseobject:context")
            if (objContext == obj.id) {
                handler?.invoke(intent)
            }
        }
        bm.registerReceiver(receiver, IntentFilter("clappr:" + eventName))
        val key = hashMapOf(
                "name" to eventName,
                "handler" to handler,
                "obj" to obj)
        receivers.put(key, receiver)
    }

    fun once(eventName: String, handler: ((Intent?) -> Unit)?, obj: BaseObject = this) {
        var onceCallback : ((Intent?) -> Unit)? = null
        onceCallback = { intent: Intent? ->
            off(eventName, onceCallback, obj)
            handler?.invoke(intent)
        }
        on(eventName, onceCallback, obj)
    }

    fun off(eventName: String, handler: ((Intent) -> Unit)?, obj: BaseObject = this) {
        val key = hashMapOf(
                "name" to eventName,
                "handler" to handler,
                "obj" to obj)
        val receiver = receivers.get(key) as? BroadcastReceiver
        if (receiver != null) {
            val bm = LocalBroadcastManager.getInstance(PlayerInfo.context?.applicationContext)
            bm.unregisterReceiver(receiver)
            receivers.remove(key)
        }
    }

    fun listenTo(obj: BaseObject, eventName: String, handler:  ((Intent?) -> Unit)?) {
        on(eventName, handler, obj)
    }

    fun stopListening(obj: BaseObject, eventName: String, handler: ((Intent?) -> Unit)?) {
        off(eventName, handler, obj)
    }

    fun stopListening() {
        val bm = LocalBroadcastManager.getInstance(PlayerInfo.context?.applicationContext)
        receivers.forEach { it -> bm.unregisterReceiver(it.value) }
        receivers.clear()
    }

    fun trigger(eventName: String) {
        val bm = LocalBroadcastManager.getInstance(PlayerInfo.context?.applicationContext)
        val intent = Intent()
        intent.setAction("clappr:" + eventName)
        intent.putExtra("clappr:baseobject:context", this.id)
        bm.sendBroadcastSync(intent)
    }
}