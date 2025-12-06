package com.notificationthriller.app.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import com.notificationthriller.app.R

/**
 * Custom view showing animated typing indicator (three dots)
 * Provides AAA-quality visual feedback when someone is typing
 */
class TypingIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.typing_indicator_color)
        style = Paint.Style.FILL
    }

    private val dotRadius = 8f
    private val dotSpacing = 24f
    private val animationDuration = 600L

    private val animators = mutableListOf<ValueAnimator>()
    private val dotScales = FloatArray(3) { 1f }

    init {
        setupAnimations()
    }

    private fun setupAnimations() {
        // Create staggered animation for each dot
        for (i in 0..2) {
            val animator = ValueAnimator.ofFloat(0.4f, 1f).apply {
                duration = animationDuration
                startDelay = i * 150L
                repeatMode = ValueAnimator.REVERSE
                repeatCount = ValueAnimator.INFINITE
                interpolator = AccelerateDecelerateInterpolator()
                
                addUpdateListener { animation ->
                    dotScales[i] = animation.animatedValue as Float
                    invalidate()
                }
            }
            animators.add(animator)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimations()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimations()
    }

    private fun startAnimations() {
        animators.forEach { it.start() }
    }

    private fun stopAnimations() {
        animators.forEach { it.cancel() }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerY = height / 2f
        val totalWidth = (dotRadius * 2 * 3) + (dotSpacing * 2)
        val startX = (width - totalWidth) / 2 + dotRadius

        for (i in 0..2) {
            val x = startX + (i * (dotRadius * 2 + dotSpacing))
            val scaledRadius = dotRadius * dotScales[i]
            
            canvas.drawCircle(x, centerY, scaledRadius, paint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = ((dotRadius * 2 * 3) + (dotSpacing * 2) + paddingLeft + paddingRight).toInt()
        val desiredHeight = ((dotRadius * 2) + paddingTop + paddingBottom).toInt()

        setMeasuredDimension(
            resolveSize(desiredWidth, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }
}
