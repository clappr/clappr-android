package com.globo.clappr.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import com.globo.clappr.components.PlayerInfo
import groovy.transform.CompileStatic

@CompileStatic
class BaseObject {

    final String id = uniqueId("o")

    private final Map receivers = [:]

    private Map options

    BaseObject(Map options = [:]) {
        this.options = options
    }

    void on(String eventName, EventHandler handler, BaseObject obj = this) {
        def bm = LocalBroadcastManager.getInstance(PlayerInfo.context?.getApplicationContext())
        BroadcastReceiver receiver = { Context context, Intent intent ->
            def objContext = intent.getStringExtra("clappr:baseobject:context")
            if (objContext == obj.id) {
                handler.handleEvent(intent)
            }
        }
        bm.registerReceiver(receiver, new IntentFilter("clappr:" + eventName))
        def key = [name: eventName, handler: handler, obj: obj]
        receivers[key] = receiver
    }

    void once(String eventName, EventHandler handler, BaseObject obj = this) {
        EventHandler onceCallback = null
        onceCallback = { Intent intent ->
            off(eventName, onceCallback, obj)
            handler.handleEvent(intent)
        }
        on(eventName, onceCallback, obj)
    }

    void off(String eventName, EventHandler handler, BaseObject obj = this) {
        def key = [name: eventName, handler: handler, obj: obj]
        def receiver = receivers[key] as BroadcastReceiver
        if (receiver != null) {
            def bm = LocalBroadcastManager.getInstance(PlayerInfo.context?.getApplicationContext())
            bm.unregisterReceiver(receiver)
            receivers.remove(key)
        }
    }

    void listenTo(BaseObject obj, String eventName, EventHandler handler) {
        on(eventName, handler, obj)
    }

    void stopListening(BaseObject obj, String eventName, EventHandler handler) {
        off(eventName, handler, obj)
    }

    void stopListening() {
        def bm = LocalBroadcastManager.getInstance(PlayerInfo.context?.getApplicationContext())
        receivers.each { bm.unregisterReceiver(it.getValue() as BroadcastReceiver) }
        receivers.clear()
    }

    void trigger(String eventName, boolean sync = false) {
        def bm = LocalBroadcastManager.getInstance(PlayerInfo.context?.getApplicationContext())
        def intent = new Intent()
        intent.setAction("clappr:" + eventName)
        intent.putExtra("clappr:baseobject:context", this.id)
        bm.sendBroadcastSync(intent)
    }

    @CompileStatic
    static abstract class EventHandler {
        abstract void handleEvent(Intent intent)
    }

    private static long count = 0

    static final String uniqueId(String prefix = null) {
        return (prefix?:"") + ++count;
    }
}
