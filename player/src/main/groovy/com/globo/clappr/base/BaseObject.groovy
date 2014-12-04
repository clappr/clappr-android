package com.globo.clappr.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import com.globo.clappr.Player
import com.globo.clappr.components.PlayerInfo
import groovy.transform.CompileStatic

@CompileStatic
public class BaseObject {

    final String id = uniqueId("o")

    private final Map<Object, Object> receivers = [:]

    public void on(String eventName, EventHandler handler, BaseObject obj = this) {
        def bm = LocalBroadcastManager.getInstance(PlayerInfo.context?.getApplicationContext())
        def receiver = new BroadcastReceiver () {
            public void onReceive (Context context, Intent intent) {
                def objContext = intent.getStringExtra("clappr:baseobject:context")
                if (objContext == obj.id) {
                    handler.handleEvent(intent)
                }
            }
        }
        bm.registerReceiver(receiver, new IntentFilter("clappr:" + eventName))
        def key = [name: eventName, handler: handler, obj: obj]
        receivers[key] = receiver
    }

    public void once(String eventName, EventHandler handler, BaseObject obj = this) {
        EventHandler onceCallback = null
        onceCallback = new EventHandler() {
            public void handleEvent(Intent intent) {
                off(eventName, onceCallback, obj)
                handler.handleEvent(intent)
            }
        }
        on(eventName, onceCallback, obj)
    }

    public void off(String eventName, EventHandler handler, BaseObject obj = this) {
        def key = [name: eventName, handler: handler, obj: obj]
        if (receivers[key] != null) {
            def bm = LocalBroadcastManager.getInstance(PlayerInfo.context?.getApplicationContext())
            bm.unregisterReceiver(receivers[key] as BroadcastReceiver)
            receivers.remove(key)
        }
    }

    public void listenTo(BaseObject obj, String eventName, EventHandler handler) {
        on(eventName, handler, obj)
    }

    public void stopListening(BaseObject obj, String eventName, EventHandler handler) {
        off(eventName, handler, obj)
    }

    public void stopListening() {
        def bm = LocalBroadcastManager.getInstance(PlayerInfo.context?.getApplicationContext())
        receivers.each { bm.unregisterReceiver(it.getValue() as BroadcastReceiver) }
        receivers.clear()
    }

    public void trigger(String eventName, boolean sync = false) {
        def bm = LocalBroadcastManager.getInstance(PlayerInfo.context?.getApplicationContext())
        def intent = new Intent()
        intent.setAction("clappr:" + eventName)
        intent.putExtra("clappr:baseobject:context", this.id)
        if (sync) {
            bm.sendBroadcastSync(intent)
        } else {
            bm.sendBroadcast(intent)
        }
    }

    @CompileStatic
    public static abstract class EventHandler {
        public abstract void handleEvent(Intent intent)
    }

    private static final long count = 0

    public static final String uniqueId(String prefix) {
        return (prefix?:"") + ++count;
    }
}
