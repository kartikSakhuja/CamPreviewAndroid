package com.example.campreviewandroid.data

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.campreviewandroid.domain.GraphicOverlay
import kotlin.jvm.Volatile
import com.google.mlkit.vision.face.Face

class FaceGraphic(overlay: GraphicOverlay?) : GraphicOverlay.Graphic(overlay!!) {
    private val facePositionPaint: Paint
    private val idPaint: Paint
    private val boxPaint: Paint

    @Volatile
    private var face: Face? = null

    fun updateFace(face: Face?) {
        this.face = face
        postInvalidate()
    }

    override fun draw(canvas: Canvas?) {
        val face = face ?: return

        val x = translateX(face.boundingBox.centerX().toFloat())
        val y = translateY(face.boundingBox.centerY().toFloat())

        val xOffset = scale(face.boundingBox.width() / 2.0f)
        val yOffset = scale(face.boundingBox.height() / 2.0f)
        val left = x - xOffset
        val top = y - yOffset
        val right = x + xOffset
        val bottom = y + yOffset
        canvas!!.drawRect(left, top, right, bottom, boxPaint)
        val contour = face.allContours
        for (faceContour in contour) {
            for (point in faceContour.points) {
                val px = translateX(point.x)
                val py = translateY(point.y)
                canvas.drawCircle(px, py, FACE_POSITION_RADIUS, facePositionPaint)
            }
        }
    }

    companion object {
        private const val FACE_POSITION_RADIUS = 8.0f
        private const val BOX_STROKE_WIDTH = 5.0f
        private val COLOR_CHOICES = intArrayOf(
            Color.BLACK
        )
        private var currentColorIndex = 0
    }

    init {
        currentColorIndex = (currentColorIndex + 1) % COLOR_CHOICES.size
        val selectedColor = COLOR_CHOICES[currentColorIndex]
        facePositionPaint = Paint()
        facePositionPaint.color = selectedColor
        idPaint = Paint()
        idPaint.color = selectedColor
        boxPaint = Paint()
        boxPaint.color = selectedColor
        boxPaint.style = Paint.Style.STROKE
        boxPaint.strokeWidth = BOX_STROKE_WIDTH
    }
}