package com.globo.clappr

import android.view.ViewGroup
import com.globo.clappr.base.BaseObject
import com.globo.clappr.base.Options
import com.globo.clappr.components.Core
import com.globo.clappr.plugin.Loader

/**
 *  Main Clappr class.
 *
 * Once instantiated it should be attached to ViewGroup before playback can begin.
 */
class Player(val options: Options = Options()) : BaseObject() {
    var core: Core? = null
    val loader = Loader()

    /**
     * Attaches the player to a ViewGroup.
     *
     * After the player has been created, it must be included within a ViewGroup using this method.
     * The player will resize itself and fit the video under the containing viewGroup without distorting.
     *
     * @param viewGroup
     *            a visible viewGroup created by the application using this player.
     */
    fun attachTo(viewGroup: ViewGroup) {
        core = Core(loader, options)

        core?.let {
            viewGroup.addView(it.view)
            it.render()
        }
    }
}