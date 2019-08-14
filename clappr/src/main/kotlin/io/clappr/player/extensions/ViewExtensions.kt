package io.clappr.player.extensions

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils

fun View.animate(res: Int, onAnimationEnd: () -> Unit = {}): Animation {
    val animator = AnimationUtils.loadAnimation(context, res)

    animator.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {
        }

        override fun onAnimationEnd(animation: Animation?) {
            onAnimationEnd()
        }

        override fun onAnimationStart(animation: Animation?) {
        }
    })

    this.startAnimation(animator)
    return animation
}
