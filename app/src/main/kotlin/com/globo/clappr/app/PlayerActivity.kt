package com.globo.clappr.app

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.globo.clappr.Player
import com.globo.clappr.base.Callback
import com.globo.clappr.base.Event
import com.globo.clappr.base.Options
import com.globo.clappr.playback.ExoPlayerPlayback
import com.globo.clappr.plugin.Loader

class PlayerActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)


        val player = Player()
        Loader.registerPlayback(ExoPlayerPlayback::class)
        player.configure(Options(source = "http://clappr.io/highline.mp4", autoPlay = false))
        player.on(Event.WILL_PLAY.value, Callback.wrap {Log.i("PLAYER", "Will Play")})
        player.on(Event.PLAYING.value, Callback.wrap {Log.i("PLAYER", "Playing")})
        player.on(Event.DID_COMPLETE.value, Callback.wrap {Log.i("PLAYER", "Completed")})

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.container, player);
        fragmentTransaction.commit();

        player.play()
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
}
