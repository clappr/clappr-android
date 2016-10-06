package com.globo.clappr.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager

open class BaseObject() : EventInterface {
    companion object {
        const val CONTEXT_KEY  = "clappr:baseobject:context"
        const val USERDATA_KEY = "clappr:baseobject:userdata"

        var context: Context? = null
    }

    val context: Context
    init {
        context = Companion.context!!
    }
    override val id =  Utils.uniqueId("o")

    private var receivers: MutableMap<String, BroadcastReceiver> = hashMapOf()

    override fun on(eventName: String, handler: Callback?) : String {
        return on(eventName, handler, this)
    }

    override fun on(eventName: String, handler: Callback?, obj: BaseObject) : String{
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

    override fun once(eventName: String, handler: Callback?) : String {
        return once(eventName, handler, this)
    }

    override fun once(eventName: String, handler: Callback?, obj: BaseObject) : String {
        var listenId: String? = null
        var onceCallback = Callback ({ bundle : Bundle? ->
                off(listenId!!)
                handler?.invoke(bundle)
            } )
        listenId = on(eventName, onceCallback, obj)
        return listenId
    }

    override fun off(listenId: String) {
        val receiver = receivers[listenId] as? BroadcastReceiver
        if (receiver != null) {
            val bm = LocalBroadcastManager.getInstance(context?.applicationContext)
            bm.unregisterReceiver(receiver)
            receivers.remove(listenId)
        }
    }

    override fun listenTo(obj: BaseObject, eventName: String, handler: Callback?) : String {
        return on(eventName, handler, obj)
    }

    override fun stopListening(listenId: String) {
        off(listenId)
    }

    override fun stopListening() {
        val bm = LocalBroadcastManager.getInstance(context?.applicationContext)
        receivers.forEach { it -> bm.unregisterReceiver(it.value) }
        receivers.clear()
    }

    override fun trigger(eventName: String, userData: Bundle?) {
        val bm = LocalBroadcastManager.getInstance(context?.applicationContext)
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