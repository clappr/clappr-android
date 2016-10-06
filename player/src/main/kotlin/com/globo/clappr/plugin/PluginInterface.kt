package com.globo.clappr.plugin

enum class PluginState { ENABLED, DISABLED }
enum class PluginVisibility { VISIBLE, HIDDEN }

interface PluginInterface {
    val name : String
}