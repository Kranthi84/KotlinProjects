package com.billscan.application.utils

import android.graphics.*

class TextGraphic internal constructor(
    overlay: GraphicOverlay,
    private val boundingBox: Rect?,
    private val color: Int = Color.BLUE
) : GraphicOverlay.Graphic(overlay) {

    private val rectPaint: Paint = Paint()

    init {
        rectPaint.color = Color.WHITE
        rectPaint.style = Paint.Style.STROKE
        rectPaint.strokeWidth = STROKE_WIDTH + 2

        // Redraw the overlay, as this graphic has been added.
        postInvalidate()
    }


    companion object {

        private const val STROKE_WIDTH = 4.0f
    }


    override fun draw(canvas: Canvas) {
        val rect = RectF(boundingBox)
        canvas.drawRect(rect, rectPaint)
        rectPaint.color = color
        rectPaint.style = Paint.Style.STROKE
        rectPaint.strokeWidth = STROKE_WIDTH
        canvas.drawRect(rect, rectPaint)
    }
}