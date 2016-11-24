package com.globo.clappr.app

import android.app.Application
import com.globo.clappr.Player
import com.globo.clappr.base.BaseObject

class PlayerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Player.initialize(this)
    }
}