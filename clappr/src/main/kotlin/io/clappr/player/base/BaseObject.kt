package io.clappr.player.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import io.clappr.player.log.Logger

open class BaseObject : EventInterface {
    companion object {
        const val CONTEXT_KEY = "clappr:baseobject:applicationContext"
        const val USERDATA_KEY = "clappr:baseobject:userdata"

        @JvmStatic
        lateinit var applicationContext: Context
    }

    override val id = Utils.uniqueId("o")

    private var receivers: MutableMap<String, BroadcastReceiver> = hashMapOf()

    override fun on(eventName: String, handler: EventHandler, obj: EventInterface): String {
        val listenId = createListenId(eventName, obj, handler)

        val broadcastManager = LocalBroadcastManager.getInstance(applicationContext)

        if (receivers[listenId] == null) {
            val receiver = Utils.broadcastReceiver { _, intent: Intent? ->
                val objContext = intent?.getStringExtra(CONTEXT_KEY)
                if (objContext == obj.id) {
                    try {
                        handler.invoke(intent.getBundleExtra(USERDATA_KEY))
                    } catch (error: Exception) {
                        Logger.error(
                                BaseObject::class.java.simpleName,
                                "Plugin ${handler.javaClass.name} crashed during invocation of event $eventName",
                                error)
                    }
                }
            }

            broadcastManager.registerReceiver(receiver, IntentFilter("clappr:$eventName"))
            receivers[listenId] = receiver
        }
        return listenId
    }

    override fun once(eventName: String, handler: EventHandler, obj: EventInterface): String {
        var listenId: String? = null
        val onceCallback: EventHandler = { bundle ->
            off(listenId!!)
            handler.invoke(bundle)
        }
        listenId = on(eventName, onceCallback, obj)
        return listenId
    }

    override fun off(listenId: String) {
        val receiver = receivers[listenId]
        if (receiver != null) {
            LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(receiver)
            receivers.remove(listenId)
        }
    }

    override fun listenTo(obj: EventInterface, eventName: String, handler: EventHandler): String {
        return on(eventName, handler, obj)
    }

    override fun stopListening(listenId: String?) {
        if (listenId != null) {
            off(listenId)
        } else {
            val bm = LocalBroadcastManager.getInstance(applicationContext)
            receivers.forEach { it -> bm.unregisterReceiver(it.value) }
            receivers.clear()
        }
    }

    override fun trigger(eventName: String, userData: Bundle?) {
        val broadcastManager = LocalBroadcastManager.getInstance(applicationContext)
        val intent = Intent()
        intent.action = "clappr:$eventName"
        intent.putExtra(CONTEXT_KEY, this.id)
        if (userData != null) {
            intent.putExtra(USERDATA_KEY, userData)
        }
        broadcastManager.sendBroadcastSync(intent)
    }

    private fun createListenId(eventName: String, baseObject: EventInterface, handler: EventHandler): String {
        return eventName + baseObject.hashCode() + handler.hashCode()
    }
}