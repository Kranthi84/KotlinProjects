package com.billscan.application.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class GraphicOverlay(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private val lock = Any()
    private val graphics = HashSet<Graphic>()
    private val handles = mutableListOf<Handle>()


    abstract class Graphic(private val overlay: GraphicOverlay) {

        abstract fun draw(canvas: Canvas)

        fun postInvalidate() {
            overlay.postInvalidate()
        }
    }

    fun clear() {
        synchronized(lock) {
            graphics.clear()
        }

        postInvalidate()
    }

    private fun add(graphic: Graphic) {
        synchronized(lock) {
            graphics.add(graphic)
        }
        postInvalidate()
    }


    class Handle(val text: String, val boundingBox: Rect?)

    fun addText(text: String, boundingBox: Rect?) {
        add(TextGraphic(this, boundingBox))
        handles.add(Handle(text, boundingBox))
    }

    fun addBox(boundingBox: Rect?) {
        add(TextGraphic(this, boundingBox, Color.RED))
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        synchronized(lock) {
            for (graphic in graphics) {
                canvas?.let { graphic.draw(it) }
            }
        }
    }
}