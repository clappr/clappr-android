package io.clappr.player.base

import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance

interface NamedType {
    companion object: NamedType {
        internal fun getName(namedType: KClass<out NamedType>): String? {
            val companion = namedType.companionObjectInstance as? NamedType
            return companion?.name
        }

        override val name = ""
    }

    val name: String?
        get() = getName(javaClass.kotlin)
}