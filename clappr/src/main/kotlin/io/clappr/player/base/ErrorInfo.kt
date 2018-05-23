package io.clappr.player.base

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable

fun writeBundle(dest: Parcel, value: Bundle?) {
    if (value != null) {
        dest.writeInt(1)
        dest.writeBundle(value)
    } else {
        dest.writeInt(0)
    }
}

fun readBundle(source: Parcel): Bundle? = if (source.readInt() == 1) source.readBundle() else null

data class ErrorInfo(val message: String, val code: Int, val extras: Bundle? = null) : Parcelable {
    private constructor(source: Parcel) : this(source.readString(), source.readInt(), readBundle(source) as? Bundle)

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<ErrorInfo> = object : Parcelable.Creator<ErrorInfo> {
            override fun createFromParcel(source: Parcel): ErrorInfo {
                return ErrorInfo(source)
            }

            override fun newArray(size: Int): Array<ErrorInfo?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(message)
        dest.writeInt(code)
        writeBundle(dest, extras)
    }

    override fun describeContents(): Int {
        return 0
    }
}

/**
 * Event bundle data keys for error info
 */
enum class ErrorInfoData(val value: String) {

    /**
     * [ErrorInfo] data
     *
     * Type: String
     *
     * Exception data.
     */
    EXCEPTION("exception")
}