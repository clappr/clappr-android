package com.globo.clappr.base

import android.content.Context
import com.globo.clappr.BuildConfig
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricGradleTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication

@RunWith(RobolectricGradleTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(21))
public open class BaseObjectCreationTest {
    var context: Context? = null

    @Before
    fun setup() {
        BaseObject.context = null
    }

    @Test(expected = kotlin.KotlinNullPointerException::class)
    fun baseObjectWithoutContext() {
        var bo = BaseObject()
    }

    @Test
    fun baseObjectCreation() {
        BaseObject.context = ShadowApplication.getInstance().applicationContext
        val bo = BaseObject()
    }
}