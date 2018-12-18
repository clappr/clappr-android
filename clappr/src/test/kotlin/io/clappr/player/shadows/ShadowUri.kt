package io.clappr.player.shadows

import android.net.Uri
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

@Implements(Uri::class)
class ShadowUri {
    companion object {
        lateinit var urlToParse: String

        @Implementation
        @JvmStatic
        fun parse(uriString: String): Uri {
            urlToParse = uriString
            return Uri.EMPTY
        }
    }
}