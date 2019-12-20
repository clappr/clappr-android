package io.clappr.player.base

import android.content.Context
import android.os.Bundle
import io.clappr.player.log.Logger
import io.clappr.player.utils.IdGenerator
import java.util.*

open class BaseObject(private val logger: Logger = Logger) : EventInterface {

    override val id = IdGenerator.uniqueId("o")

    override fun on(eventName: String, handler: EventHandler, obj: EventInterface): String {
        val subscription = Subscription(obj, this, eventName, handler)
        subscriptions += subscription
        return subscription.id
    }

    override fun once(eventName: String, handler: EventHandler, obj: EventInterface): String {
        var listenId: String? = null

        val onceCallback: EventHandler = {
            off(listenId!!)
            handler(it)
        }

        listenId = on(eventName, onceCallback, obj)

        return listenId
    }

    override fun off(listenId: String) {
        subscriptions.removeAll { it.id == listenId }
    }

    override fun listenTo(obj: EventInterface, eventName: String, handler: EventHandler) =
        on(eventName, handler, obj)

    override fun stopListening(listenId: String?) = if (listenId != null)
        off(listenId)
    else
        subscriptions.filter { it.target.id == id }.forEach { off(it.id) }

    override fun trigger(eventName: String, userData: Bundle?) {
        subscriptions
            .filter { it.source.id == id && it.eventName == eventName }
            .forEach {
                try {
                    it.handler(userData)
                } catch (exception: Exception) {
                    logException(it, exception)
                }
            }
    }

    private fun logException(subscription: Subscription, exception: Exception) = logger.error(
        BaseObject::class.java.simpleName,
        "Plugin ${subscription.handler.javaClass.name} crashed during invocation of event ${subscription.eventName}",
        exception
    )

    private data class Subscription(
        val source: EventInterface,
        val target: EventInterface,
        val eventName: String,
        val handler: EventHandler
    ) {
        val id = hashCode().toString()
    }

    companion object {
        @JvmStatic
        lateinit var applicationContext: Context

        private val subscriptions = Collections.synchronizedSet(mutableSetOf<Subscription>())
    }
}