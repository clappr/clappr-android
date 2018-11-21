package io.clappr.player.app

import android.app.Application
import io.clappr.player.Player

class PlayerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Player.initialize(this)
    }
}