package io.clappr.player.utils

import android.os.Bundle
import java.io.Serializable

fun Bundle.withPayload(vararg pairs: Pair<String, Serializable>): Bundle? = apply {
    pairs.forEach { (key, value) -> putSerializable(key, value) }
}