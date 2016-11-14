package com.globo.clappr.base

import android.os.Bundle

interface Callback {
    companion object {
        inline fun wrap(crossinline callback: (Bundle?) -> Unit) = object: Callback {
            override fun invoke(bundle: Bundle?) = callback(bundle)
        }
    }
    fun invoke(bundle: Bundle?)
}

interface EventInterface {
    val id : String

    fun on(eventName: String, handler: Callback, obj: EventInterface) : String
    fun once(eventName: String, handler: Callback, obj: EventInterface) : String
    fun off(listenId: String)

    fun trigger(eventName: String, userData: Bundle?)

    fun listenTo(obj: EventInterface, eventName: String, handler: Callback) : String
    fun stopListening(listenId: String?)

    // Manual overloads for Java Interoperability (Kotlin interfaces don't generate Java overloads
    // for default parameter values)
    fun on(eventName: String, handler: Callback) : String {
        return on(eventName, handler, this)
    }

    fun once(eventName: String, handler: Callback) : String {
        return once(eventName, handler, this)
    }

    fun trigger(eventName: String) {
        trigger(eventName, null)
    }

    fun stopListening() {
        stopListening(null)
    }
}