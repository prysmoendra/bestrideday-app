package edu.dakode.bestbikeday

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class CirclePercentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    private var percent: Int = 0
    private val paintBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 10f
        color = Color.parseColor("#E0E0E0") // very light gray
    }
    private val paintFg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 10f
        color = Color.parseColor("#AEE9D1") // pastel green
    }
    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#444444") // soft dark gray
        textSize = 36f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }
    private val paintDesc = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#888888")
        textSize = 18f
        textAlign = Paint.Align.CENTER
    }
    fun setPercent(p: Int) {
        percent = p
        invalidate()
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val size = Math.min(width, height) - 10
        val rect = RectF(5f, 5f, size.toFloat() + 5, size.toFloat() + 5)
        canvas.drawArc(rect, 0f, 360f, false, paintBg)
        val sweep = percent * 3.6f
        paintFg.color = Color.parseColor("#AEE9D1") // keep arc color soft
        canvas.drawArc(rect, -90f, sweep, false, paintFg)
        // Draw percentage
        val centerY = height / 2f
        canvas.drawText("$percent%", width / 2f, centerY - 6f, paintText)
        // Draw description below
        canvas.drawText("for biking", width / 2f, centerY + 22f, paintDesc)
    }
    private fun scoreToColor(score: Int): Int {
        return when {
            score <= 50 -> interpolateColor(Color.RED, Color.YELLOW, score / 50f)
            else -> interpolateColor(Color.YELLOW, Color.GREEN, (score - 50) / 50f)
        }
    }
    private fun interpolateColor(colorStart: Int, colorEnd: Int, fraction: Float): Int {
        val r = Color.red(colorStart) + ((Color.red(colorEnd) - Color.red(colorStart)) * fraction).toInt()
        val g = Color.green(colorStart) + ((Color.green(colorEnd) - Color.green(colorStart)) * fraction).toInt()
        val b = Color.blue(colorStart) + ((Color.blue(colorEnd) - Color.blue(colorStart)) * fraction).toInt()
        return Color.rgb(r, g, b)
    }
} 