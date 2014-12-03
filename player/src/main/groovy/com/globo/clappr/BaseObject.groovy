package com.globo.clappr

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import groovy.transform.CompileStatic

@CompileStatic
public class BaseObject {

    final String id = uniqueId("o")

    public void on(String eventName, Closure<Intent> callback, BaseObject obj = this) {
        def bm = LocalBroadcastManager.getInstance(Player.getContext()?.getApplicationContext())
        def receiver = { broadcastContext, Intent intent ->
            def objContext = intent.getStringExtra("clappr:baseobject:context")
            if (objContext == obj.id) {
                callback(intent)
            }
        }
        bm.registerReceiver(receiver, new IntentFilter("clappr:" + eventName))
        def key = [name: eventName, callback: callback, obj: obj]
        receivers[key] = receiver
    }

    public void once(String eventName, Closure<Intent> callback, BaseObject obj = this) {
        def onceCallback = null
        onceCallback = { Intent intent ->
            off(eventName, onceCallback, obj)
            callback(intent)
        }
        on(eventName, onceCallback, obj)
    }

    public void off(String eventName, Closure<Intent> callback, BaseObject obj = this) {
        def key = [name: eventName, callback: callback, obj: obj]
        if (receivers[key] != null) {
            def bm = LocalBroadcastManager.getInstance(Player.getContext()?.getApplicationContext())
            bm.unregisterReceiver(receivers[key] as BroadcastReceiver)
            receivers.remove(key)
        }
    }

    public void listenTo(String eventName, Closure<Intent> callback) {
        on(eventName, callback)
    }

    public void stopListening(String eventName, Closure<Intent> callback) {
        off(eventName, callback)
    }

    public void stopListening() {
        def bm = LocalBroadcastManager.getInstance(Player.getContext()?.getApplicationContext())
        receivers.each {
            it.getValue().each { BroadcastReceiver receiver -> bm.unregisterReceiver(receiver) }
        }
        receivers.clear()
    }

    public void trigger(String eventName, boolean sync = false) {
        def bm = LocalBroadcastManager.getInstance(Player.getContext()?.getApplicationContext())
        def intent = new Intent()
        intent.setAction("clappr:" + eventName)
        intent.putExtra("clappr:baseobject:context", this.id)
        if (sync) {
            bm.sendBroadcastSync(intent)
        } else {
            bm.sendBroadcast(intent)
        }
    }

    private static final receivers = [:]

    private static final long count = 0

    public static final String uniqueId(String prefix) {
        return (prefix?:"") + ++count;
    }
}
