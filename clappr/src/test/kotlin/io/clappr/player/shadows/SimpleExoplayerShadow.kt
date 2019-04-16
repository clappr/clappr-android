package io.clappr.player.shadows

import com.google.android.exoplayer2.SimpleExoPlayer
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

@Implements(SimpleExoPlayer::class)
class SimpleExoplayerShadow {

    companion object {
        var staticRepeatMode: Int? = null
    }

    @Implementation
    fun setRepeatMode(repeatMode: Int) {
        staticRepeatMode = repeatMode
    }
}