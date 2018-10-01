package io.clappr.player.app

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import io.clappr.player.Player
import io.clappr.player.base.*
import io.clappr.player.log.Logger

class  PlayerActivity : Activity() {

    private lateinit var player: Player
    private val playerContainer by lazy { findViewById<ViewGroup>(R.id.player_container) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        player = Player()
        player.on(Event.WILL_PLAY.value, Callback.wrap { Logger.info("App", "Will Play") })
        player.on(Event.PLAYING.value, Callback.wrap { Logger.info("App","Playing") })
        player.on(Event.DID_COMPLETE.value, Callback.wrap { Logger.info("App", "Completed") })
        player.on(Event.BUFFER_UPDATE.value, Callback.wrap { bundle: Bundle? -> Logger.info("App","Buffer update: ${bundle?.getDouble("percentage")}") })
        player.on(Event.REQUEST_FULLSCREEN.value, Callback.wrap { Logger.info("App","Enter full screen"); enterFullscreen() })
        player.on(Event.EXIT_FULLSCREEN.value, Callback.wrap { Logger.info("App","Exit full screen"); exitFullscreen() })

        player.on(Event.ERROR.value, Callback.wrap { bundle: Bundle? ->
            bundle?.getParcelable<ErrorInfo>(Event.ERROR.value)?.let {
                Logger.error("App","Error: ${it.code} ${it.message}", (it.extras?.getSerializable(ErrorInfoData.EXCEPTION.value) as? Exception))
            }

        })

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.player_container, player)
        fragmentTransaction.commit()

        player.configure(Options(source = "http://clappr.io/highline.mp4"))
        player.play()
    }

    fun enterFullscreen() {
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
    }

    fun exitFullscreen() {
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        checkScreenOrientation(newConfig.orientation)
    }

    fun checkScreenOrientation(orientation: Int) {
        when (orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                hideSystemUi()
                player.fullscreen = true
            }
            Configuration.ORIENTATION_PORTRAIT -> {
                showSystemUi()
                player.fullscreen = false
                this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
    }

    private fun showSystemUi() {
        this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        playerContainer.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        playerContainer.layoutParams.height = this.resources.getDimensionPixelSize(R.dimen.player_portrait_height)
    }

    private fun hideSystemUi() {
        this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        playerContainer.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        playerContainer.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
    }
}
