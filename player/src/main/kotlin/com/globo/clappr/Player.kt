package com.globo.clappr

import com.globo.clappr.base.BaseObject
import com.globo.clappr.base.Options
import com.globo.clappr.components.Core

/**
 *  Main Clappr class.
 *
 * Once instantiated it should be attached to ViewGroup before playback can begin.
 */
class Player : BaseObject {
    val core : Core

    constructor(options: Options?) : super() {
        core = Core(options)
    }
}