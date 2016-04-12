package com.globo.clappr.base

import com.globo.clappr.BuildConfig
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricGradleTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication

@RunWith(RobolectricGradleTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(21))
public open class BaseObjectTest {
    @Test(expected=kotlin.TypeCastException::class)
    fun baseObjectWithoutNullOptions() {
        var bo = BaseObject(null)
    }

    @Test(expected=kotlin.TypeCastException::class)
    fun baseObjectWithoutContext() {
        var bo = BaseObject(mapOf("opt" to (1 as Object)))
    }

    @Test
    fun baseObjectCreation() {
        val bo = BaseObject(mapOf("context" to (ShadowApplication.getInstance().applicationContext as Object)))
    }
}