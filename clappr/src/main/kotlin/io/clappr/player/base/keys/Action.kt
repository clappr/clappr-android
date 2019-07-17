package io.clappr.player.base.keys

enum class Action(val value: String) {
    UNDEFINED("undefined"),
    UP("up"),
    DOWN("down");

    companion object {
        fun getByValue(value: String) = values().firstOrNull { it.value == value }?.let { it }
    }
}