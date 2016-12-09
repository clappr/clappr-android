package io.clappr.player.base

import android.os.Bundle
import android.os.Parcel
import io.clappr.player.BuildConfig
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23))
class ErrorInfoTest {

    @Test
    fun shouldBeParcelableWhenBundleIsNull() {
        val message = "some message"
        val code = 7
        val errorInfo = ErrorInfo(message, code)

        val parcel = Parcel.obtain()
        errorInfo.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)

        val errorFromParcel = ErrorInfo.CREATOR.createFromParcel(parcel)
        assertEquals(errorInfo, errorFromParcel)
    }

    @Test
    fun shouldBeParcelable() {
        val message = "some message"
        val code = 7
        val bundle = Bundle()
        bundle.putString("key", "value")
        val errorInfo = ErrorInfo(message, code, bundle)

        val parcel = Parcel.obtain()
        errorInfo.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)

        val errorFromParcel = ErrorInfo.CREATOR.createFromParcel(parcel)

        assertEquals(errorInfo.code, errorFromParcel.code)
        assertEquals(errorInfo.message, errorFromParcel.message)
        assertEquals(errorInfo.extras?.get("key"), errorFromParcel.extras?.get("key"))
    }

}