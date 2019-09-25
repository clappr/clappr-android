package io.clappr.player.base.keys

enum class Key(val value: String) {
    UNDEFINED("undefined"),
    PLAY("play"),
    PAUSE("pause"),
    STOP("stop"),
    PLAY_PAUSE("playPause"),
    UP("up"),
    DOWN("down"),
    RIGHT("right"),
    LEFT("left"),
    BACK("back");

    companion object {
        fun getByValue(value: String) = values().firstOrNull { it.value == value }
    }
}