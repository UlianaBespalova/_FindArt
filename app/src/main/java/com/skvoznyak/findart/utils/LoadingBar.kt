package com.skvoznyak.findart.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation

class LoadingBar : View {

    private val dotRadius = 9
    private val bounceDotRadius = 15
    private val dotAmount = 6
    private var dotPosition = 0
    private var duration = 700L
    private val dotsDistributionX: Float = 90F

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val paint = Paint()
        createDot(canvas, paint)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
    }

    private fun createDot(canvas: Canvas, paint: Paint) {
        for (i in 0 until dotAmount) {
            if (i == dotPosition) {
                paint.color = Color.parseColor("#11FFECB8")
                canvas.drawCircle(
                    (2 * dotRadius + i * dotsDistributionX),
                    bounceDotRadius.toFloat(),
                    bounceDotRadius.toFloat(),
                    paint
                )

                paint.color = Color.parseColor("#55FFECB8")
                canvas.drawCircle(
                    (2 * dotRadius + i * dotsDistributionX),
                    bounceDotRadius.toFloat(),
                    dotRadius.toFloat() + 3,
                    paint
                )

                paint.color = Color.parseColor("#FFFFECB8")
                canvas.drawCircle(
                    (2 * dotRadius + i * dotsDistributionX),
                    bounceDotRadius.toFloat(),
                    dotRadius.toFloat(),
                    paint
                )
            } else {
                paint.color = Color.parseColor("#FF707070")
                canvas.drawCircle(
                    (2 * dotRadius + i * dotsDistributionX),
                    bounceDotRadius.toFloat(),
                    dotRadius.toFloat(),
                    paint
                )
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width: Int
        val calculatedWidth = dotsDistributionX * (dotAmount - 1) + bounceDotRadius * 3
        width = calculatedWidth.toInt()
        val height: Int = bounceDotRadius * 2

        setMeasuredDimension(width, height)
    }

    private fun startAnimation() {
        val bounceAnimation = BounceAnimation()
        bounceAnimation.duration = duration
        bounceAnimation.repeatCount = Animation.INFINITE
        bounceAnimation.interpolator = LinearInterpolator()
        bounceAnimation.setAnimationListener(
            object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {}
                override fun onAnimationRepeat(animation: Animation) {
                    dotPosition++
                    if (dotPosition == dotAmount) {
                        dotPosition = 0
                    }
                }
            }
        )
        startAnimation(bounceAnimation)
    }

    private inner class BounceAnimation : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            super.applyTransformation(interpolatedTime, t)
            invalidate()
        }
    }
}
