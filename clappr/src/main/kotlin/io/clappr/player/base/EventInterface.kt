package io.clappr.player.base

import android.os.Bundle

typealias EventHandler = (Bundle?) -> Unit

interface EventInterface {
    val id : String

    fun on(eventName: String, handler: EventHandler, obj: EventInterface) : String
    fun once(eventName: String, handler: EventHandler, obj: EventInterface) : String
    fun off(listenId: String)

    fun trigger(eventName: String, userData: Bundle?)

    fun listenTo(obj: EventInterface, eventName: String, handler: EventHandler) : String
    fun stopListening(listenId: String?)

    // Manual overloads for Java Interoperability (Kotlin interfaces don't generate Java overloads
    // for default parameter values)
    fun on(eventName: String, handler: EventHandler) : String {
        return on(eventName, handler, this)
    }

    fun once(eventName: String, handler: EventHandler) : String {
        return once(eventName, handler, this)
    }

    fun trigger(eventName: String) {
        trigger(eventName, null)
    }

    fun stopListening() {
        stopListening(null)
    }
}