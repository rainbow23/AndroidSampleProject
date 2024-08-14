package com.example.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import java.util.*

class CustomTextView : AppCompatTextView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val textPaint: Paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    val bounds = ArrayList<Rect>()
    val fullBounds = Rect()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        getBound()
    }

    private fun getBound() {

        text.toString().let { string ->

            val lines = string.split("\n")
            var offset = 0

            lines.forEachIndexed { i, str ->

                // replace all tabs with _ char for measuring
                val s = str.replace('\t', '_')

                // get horizontal bound for each line
                val boundHorizontal = Rect()
                paint.getTextBounds(s, 0, s.length, boundHorizontal)
                boundHorizontal.offset(
                    paddingStart + (layout?.getPrimaryHorizontal(offset)?.toInt() ?: 0),
                    0
                )

                // get vertical bound for each line
                val boundVertical = Rect()
                getLineBounds(i, boundVertical)
                boundVertical.apply {
                    left = boundHorizontal.left
                    right = boundHorizontal.right
                }
                bounds.add(boundVertical)

                offset += (s.length + 1)
            }

            bounds.forEachIndexed { i, rect ->
                if (i == 0) {
                    fullBounds.set(rect)
                }
                fullBounds.intersectUnchecked(rect)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {

        canvas.drawRect(fullBounds, textPaint.apply { color = Color.YELLOW })

        bounds.forEach {
            canvas.drawRect(it, textPaint.apply { color = Color.MAGENTA })
        }

        super.onDraw(canvas)
    }

    companion object {

        fun Rect.intersectUnchecked(other: Rect) {
            if (other.left < left) left = other.left
            if (other.right > right) right = other.right
            if (other.top < top) top = other.top
            if (other.bottom > bottom) bottom = other.bottom
        }
    }
}