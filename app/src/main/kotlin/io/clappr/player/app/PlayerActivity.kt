package io.clappr.player.app

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import io.clappr.player.Player
import io.clappr.player.base.*
import io.clappr.player.log.Logger

class  PlayerActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val player = Player()
        player.configure(Options(source = "http://clappr.io/highline.mp4"))
        player.on(Event.WILL_PLAY.value, Callback.wrap { Logger.info("App", "Will Play") })
        player.on(Event.PLAYING.value, Callback.wrap { Logger.info("App","Playing") })
        player.on(Event.DID_COMPLETE.value, Callback.wrap { Logger.info("App", "Completed") })
        player.on(Event.BUFFER_UPDATE.value, Callback.wrap { bundle: Bundle? -> Logger.info("App","Buffer update: ${bundle?.getDouble("percentage")}") })
        player.on(Event.ERROR.value, Callback.wrap { bundle: Bundle? ->
            bundle?.getParcelable<ErrorInfo>(Event.ERROR.value)?.let {
                Logger.error("App","Error: ${it.code} ${it.message}", (it.extras?.getSerializable(ErrorInfoData.EXCEPTION.value) as? Exception))
            }

        })

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.container, player)
        fragmentTransaction.commit()

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
