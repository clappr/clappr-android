package com.globo.clappr.base

import android.os.Bundle

class Callback(val callback: (Bundle?) -> Unit) { fun invoke(bundle: Bundle?) = callback(bundle)}

interface EventInterface {
    val id : String

    fun on(eventName: String, handler: Callback?) : String
    fun on(eventName: String, handler: Callback?, obj: BaseObject) : String
    fun once(eventName: String, handler: Callback?) : String
    fun once(eventName: String, handler: Callback?, obj: BaseObject) : String
    fun off(listenId: String)

    fun trigger(eventName: String, userData: Bundle? = null)

    fun listenTo(obj: BaseObject, eventName: String, handler: Callback?) : String
    fun stopListening(listenId: String)
    fun stopListening()
}