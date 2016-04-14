package com.globo.clappr.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import com.globo.clappr.components.PlayerInfo

public open class BaseObject() {
    companion object {
        const val CONTEXT_KEY  = "clappr:baseobject:context"
        const val USERDATA_KEY = "clappr:baseobject:userdata"

        var context: Context? = null
    }

    val context: Context
    init {
        context = Companion.context!!
    }
    val id =  Utils.uniqueId("o")

    private var receivers: MutableMap<String, BroadcastReceiver> = hashMapOf()

    fun on(eventName: String, handler: ((Bundle?) -> Unit)?, obj: BaseObject = this) : String{
        val listenId = createListenId(eventName, obj)

        val bm = LocalBroadcastManager.getInstance(context.applicationContext)
        val receiver = Utils.broadcastReceiver { context: Context?, intent: Intent? ->
            val objContext = intent?.getStringExtra(CONTEXT_KEY)
            if (objContext == obj.id) {
                handler?.invoke(intent?.getBundleExtra(USERDATA_KEY))
            }
        }
        bm.registerReceiver(receiver, IntentFilter("clappr:" + eventName))

        receivers.put(listenId, receiver)
        return listenId
    }

    fun once(eventName: String, handler: ((Bundle?) -> Unit)?, obj: BaseObject = this) : String {
        var listenId: String? = null
        var onceCallback : ((Bundle?) -> Unit)? = { bundle ->
                off(listenId!!)
                handler?.invoke(bundle)
            }
        listenId = on(eventName, onceCallback, obj)
        return listenId
    }

    fun off(listenId: String) {
        val receiver = receivers[listenId] as? BroadcastReceiver
        if (receiver != null) {
            val bm = LocalBroadcastManager.getInstance(PlayerInfo.context?.applicationContext)
            bm.unregisterReceiver(receiver)
            receivers.remove(listenId)
        }
    }

    fun listenTo(obj: BaseObject, eventName: String, handler:  ((Bundle?) -> Unit)?) : String {
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

    fun trigger(eventName: String, userData: Bundle? = null) {
        val bm = LocalBroadcastManager.getInstance(PlayerInfo.context?.applicationContext)
        val intent = Intent()
        intent.action = "clappr:" + eventName
        intent.putExtra(CONTEXT_KEY, this.id)
        if (userData != null) {
            intent.putExtra(USERDATA_KEY, userData)
        }
        bm.sendBroadcastSync(intent)
    }

    private fun createListenId(eventName: String, baseObject: BaseObject) : String {
        return eventName + baseObject.hashCode() + System.currentTimeMillis()
    }
}