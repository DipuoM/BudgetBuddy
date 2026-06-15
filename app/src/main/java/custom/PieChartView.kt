package com.budgetbuddy.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.budgetbuddy.model.PieSlice

class PieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var slices: List<PieSlice> = emptyList()

    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val colors = listOf(
        Color.parseColor("#2196F3"), // Blue
        Color.parseColor("#F44336"), // Red
        Color.parseColor("#4CAF50"), // Green
        Color.parseColor("#FF9800"), // Orange
        Color.parseColor("#9C27B0"), // Purple
        Color.parseColor("#009688"), // Teal
        Color.parseColor("#795548"), // Brown
        Color.parseColor("#607D8B")  // Blue Grey
    )

    fun setData(data: List<PieSlice>) {
        slices = data
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (slices.isEmpty()) return

        val total =
            slices.sumOf { it.amount.toDouble() }.toFloat()

        val rect = RectF(
            50f,
            50f,
            width - 50f,
            height - 50f
        )

        var startAngle = 0f

        slices.forEachIndexed { index, slice ->

            val sweepAngle =
                (slice.amount / total) * 360f

            paint.color =
                colors[index % colors.size]

            canvas.drawArc(
                rect,
                startAngle,
                sweepAngle,
                true,
                paint
            )

            startAngle += sweepAngle
        }
    }
}