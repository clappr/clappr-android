package com.globo.clappr.base

import kotlin.reflect.KClass
import kotlin.reflect.companionObjectInstance

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