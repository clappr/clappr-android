package com.globo.clappr.app

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import com.globo.clappr.Player
import com.globo.clappr.base.BaseObject
import com.globo.clappr.base.Options
import com.globo.clappr.playback.ExoPlayerPlayback
import com.globo.clappr.plugin.Loader

class PlayerActivity : Activity() {

    private var player: Player? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        if (savedInstanceState == null) {
            BaseObject.context = applicationContext
            Loader.registerPlayback(ExoPlayerPlayback::class)
            val urlString = "http://www.html5videoplayer.net/videos/toystory.mp4"
            val options = Options(urlString, "", true)
            player = Player(options)
        }
    }

    override fun onResume() {
        super.onResume()
        val viewGroup = findViewById(R.id.player) as FrameLayout
        ExoPlayerPlayback.containerView = viewGroup

        player?.attachTo(viewGroup)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.player, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_settings) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun play(view: View) {
        player?.core?.activeContainer?.playback?.play()
    }

    fun stop(view: View) {
        player?.core?.activeContainer?.playback?.stop()
    }

    fun seek(view: View) {
        player?.core?.activeContainer?.playback?.seek(30)
    }

    fun pause(view: View) {
        player?.core?.activeContainer?.playback?.pause()
    }

}
