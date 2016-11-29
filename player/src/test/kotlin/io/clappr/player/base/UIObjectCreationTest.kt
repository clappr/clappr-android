package io.clappr.player.base

import android.content.Context
import io.clappr.player.BuildConfig
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication
import kotlin.test.assertNotNull

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23))
open class UIObjectCreationTest {
    var context: Context? = null

    @Before
    fun setup() {
        BaseObject.context = null
    }

    @Test(expected = IllegalStateException::class)
    fun uiObjectWithoutContext() {
        var uo = UIObject()
    }

    @Test
    fun uiObjectCreation() {
        BaseObject.context = ShadowApplication.getInstance().applicationContext
        val uo = UIObject()
        assertNotNull(uo.view, "invalid view")
    }
}