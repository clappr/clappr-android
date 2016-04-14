package com.globo.clappr.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import com.globo.clappr.components.PlayerInfo

public open class BaseObject() {
    companion object {
        var context: Context? = null
    }

    val context: Context
    init {
        context = Companion.context!!
    }
    val id =  Utils.uniqueId("o")

    private var receivers: MutableMap<String, BroadcastReceiver> = hashMapOf()

    fun on(eventName: String, handler: ((Intent?) -> Unit)?, obj: BaseObject = this) : String{
        val listenId = createListenId(eventName, obj)

        val bm = LocalBroadcastManager.getInstance(context.applicationContext)
        val receiver = Utils.broadcastReceiver { context: Context?, intent: Intent? ->
            val objContext = intent?.getStringExtra("clappr:baseobject:context")
            if (objContext == obj.id) {
                handler?.invoke(intent)
            }
        }
        bm.registerReceiver(receiver, IntentFilter("clappr:" + eventName))

        receivers.put(listenId, receiver)
        return listenId
    }

    fun once(eventName: String, handler: ((Intent?) -> Unit)?, obj: BaseObject = this) : String {
        var listenId: String? = null
        var onceCallback : ((Intent?) -> Unit)? = null
            onceCallback = { intent: Intent? ->
                off(listenId!!)
                handler?.invoke(intent)
            }
        listenId = on(eventName, onceCallback, obj)
        return listenId
    }

    fun off(listenId: String) {
        val receiver = receivers.get(listenId) as? BroadcastReceiver
        if (receiver != null) {
            val bm = LocalBroadcastManager.getInstance(PlayerInfo.context?.applicationContext)
            bm.unregisterReceiver(receiver)
            receivers.remove(listenId)
        }
    }

    fun listenTo(obj: BaseObject, eventName: String, handler:  ((Intent?) -> Unit)?) : String {
        return on(eventName, handler, obj)
    }

    fun stopListening(listenId: String) {
        off(listenId)
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

    private fun createListenId(eventName: String, baseObject: BaseObject) : String {
        return eventName + baseObject.hashCode() + System.currentTimeMillis()
    }
}