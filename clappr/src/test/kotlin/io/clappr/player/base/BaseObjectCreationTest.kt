package io.clappr.player.base

import android.content.Context
import io.clappr.player.BuildConfig
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23))
open class BaseObjectCreationTest {
    var context: Context? = null

    @Before
    fun setup() {
        BaseObject.applicationContext = null
    }

    @Test(expected = IllegalStateException::class)
    fun baseObjectWithoutContext() {
        var bo = BaseObject()
    }

    @Test
    fun baseObjectCreation() {
        BaseObject.applicationContext = ShadowApplication.getInstance().applicationContext
        val bo = BaseObject()
    }
}