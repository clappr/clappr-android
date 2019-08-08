package io.clappr.player.extensions

import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils

fun View.removeFromParent() {
    (this.parent as? ViewGroup)?.removeView(this)
}

fun View.animate(res: Int, onAnimationEnd: () -> Unit = {}): Animation {
    val animator = AnimationUtils.loadAnimation(context, res)

    animator.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {
        }

        override fun onAnimationEnd(animation: Animation?) {
            if (this@animate is ViewGroup)
                this@animate.enableAllViews()
            else {
                isEnabled = true
            }
            onAnimationEnd()

        }

        override fun onAnimationStart(animation: Animation?) {
            if (this@animate is ViewGroup)
                this@animate.disableAllViews()
            else {
                isEnabled = false
            }
        }
    })

    this.startAnimation(animator)
    return animation
}
