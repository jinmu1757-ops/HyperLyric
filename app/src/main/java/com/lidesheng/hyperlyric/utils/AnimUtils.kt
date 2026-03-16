package com.lidesheng.hyperlyric.utils

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.TextView

object AnimUtils {

    const val ANIM_SLIDE = 1
    const val ANIM_CROSS_FADE = 2
    const val ANIM_SCALE = 3
    const val ANIM_SLIDE_UP = 4

    private const val DURATION = 300L

    fun applyTextWithAnim(
        view: TextView,
        newText: String,
        mode: Int,
        manualStartX: Float? = null,
        onAnimEnd: () -> Unit
    ) {
        if (view.text == newText) {
            onAnimEnd()
            return
        }

        view.animate().cancel()
        resetViewProperties(view)

        when (mode) {
            ANIM_SLIDE -> playSlideAnim(view, newText, manualStartX, onAnimEnd)
            ANIM_CROSS_FADE -> playCrossFadeAnim(view, newText, onAnimEnd)
            ANIM_SCALE -> playScaleAnim(view, newText, onAnimEnd)
            ANIM_SLIDE_UP -> playSlideUpAnim(view, newText, onAnimEnd)
            else -> {
                view.text = newText
                onAnimEnd()
            }
        }
    }

    private fun playSlideAnim(view: TextView, newText: String, manualStartX: Float?, onEnd: () -> Unit) {
        val width = view.width.toFloat()
        val startX = manualStartX ?: view.translationX
 
        if (manualStartX != null) {
            view.translationX = manualStartX
        }

        view.animate()
            .translationX(startX - width / 2)
            .alpha(0f)
            .setDuration(DURATION)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                view.text = newText
                view.translationX = startX + width / 2
                view.animate()
                    .translationX(startX)
                    .alpha(1f)
                    .setDuration(DURATION)
                    .withEndAction(onEnd)
                    .start()
            }
            .start()
    }

    private fun playCrossFadeAnim(view: TextView, newText: String, onEnd: () -> Unit) {
        view.animate()
            .alpha(0f)
            .setDuration(DURATION)
            .withEndAction {
                view.text = newText
                view.animate()
                    .alpha(1f)
                    .setDuration(DURATION)
                    .withEndAction(onEnd)
                    .start()
            }
            .start()
    }

    private fun playScaleAnim(view: TextView, newText: String, onEnd: () -> Unit) {
        view.animate()
            .scaleX(0.5f).scaleY(0.5f).alpha(0f)
            .setDuration(DURATION)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                view.text = newText
                view.scaleX = 0.5f; view.scaleY = 0.5f

                view.animate()
                    .scaleX(1f).scaleY(1f).alpha(1f)
                    .setDuration(DURATION)
                    .setInterpolator(DecelerateInterpolator())
                    .withEndAction(onEnd)
                    .start()
            }
            .start()
    }

    private fun playSlideUpAnim(view: TextView, newText: String, onEnd: () -> Unit) {
        val height = view.height.takeIf { it > 0 }?.toFloat() ?: 50f

        view.animate()
            .translationY(-height / 2)
            .alpha(0f)
            .setDuration(DURATION)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                view.text = newText
                view.translationY = height / 2

                view.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(DURATION)
                    .withEndAction(onEnd)
                    .start()
            }
            .start()
    }

    private fun resetViewProperties(view: View) = view.apply {
        alpha = 1f
        translationY = 0f
        scaleX = 1f
        scaleY = 1f
    }
}
