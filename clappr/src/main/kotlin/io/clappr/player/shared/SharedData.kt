package io.clappr.player.shared

import java.util.HashMap

class SharedData (
    val sharedData: HashMap<String, Any> = hashMapOf()) : MutableMap<String, Any> by sharedData
