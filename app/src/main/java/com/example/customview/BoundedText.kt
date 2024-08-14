package com.example.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.appcompat.widget.AppCompatTextView
import android.os.Build
import android.text.Layout
import android.util.AttributeSet
import android.util.Log

class BoundedText : AppCompatTextView {
    //public class BoundedText extends androidx.appcompat.widget.AppCompatTextView {
    private val mLineBounds = Rect()
    private val mTextBounds = Rect()
    private val mRectPaint = Paint()

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    ) {
        init()
    }

    private fun init() {
        mRectPaint.style = Paint.Style.STROKE
        mRectPaint.strokeWidth = 1f
        mRectPaint.color = Color.RED
    }

    private val boundVertical = Rect()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //Log.d("Test1", "onMeasure( widthMeasureSpec: $widthMeasureSpec, heightMeasureSpec: $heightMeasureSpec")
        getLineBounds(0, boundVertical)

        Log.d("Test1", "onMeasure( boundVertical: $boundVertical, boundVertical.top: ${boundVertical.top}, text: $text")
        //onMeasure( boundVertical: Rect(0, 179 - 523, 255), text: TextView1TextView2TextView3
        //Rect(int left, int top, int right, int bottom)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Log.d("Test1", "onLayout( changed: $changed, left: $left, top: $top, right: $right, bottom: $bottom")

        val layout = layout
        getTightBounds(layout, 0, mTextBounds)
        if (layout.lineCount > 1) { // Multi-line
            // We already have bounds of first line. Check bounds of last line since it will
            // be the bottom of the bounding rectangle for all the text.
            getTightBounds(layout, layout.lineCount - 1, mLineBounds)
            mTextBounds.bottom = mLineBounds.bottom

            // Now check remaining lines for min left bound and max right bound.
            for (line in 1 until layout.lineCount) {
                Log.d("Test11", "line: $line")
                getTightBounds(layout, line, mLineBounds)
                if (mLineBounds.left < mTextBounds.left) {
                    mTextBounds.left = mLineBounds.left
                }
                if (mLineBounds.right > mTextBounds.right) {
                    mTextBounds.right = mLineBounds.right
                }
            }
            Log.d("Test1", "mTextBounds.top: " + mTextBounds.top)
            Log.d("Test1", "mTextBounds.bottom: " + mTextBounds.bottom)

            // Convert pixel values to dp
            val density = resources.displayMetrics.density
            val textBoundsTopDp = mTextBounds.top / density
            val textBoundsBottomDp = mTextBounds.bottom / density
            val textBoundsLeftDp = mTextBounds.left / density
            val textBoundsRightDp = mTextBounds.right / density
            Log.d("Test1", "mTextBounds.top (dp): $textBoundsTopDp")
            Log.d("Test1", "mTextBounds.bottom (dp): $textBoundsBottomDp")
            Log.d("Test1", "mTextBounds.left (dp): $textBoundsLeftDp")
            Log.d("Test1", "mTextBounds.right (dp): $textBoundsRightDp")
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        Log.d("Test1", "onDraw(")

        canvas.save()
        canvas.translate(paddingLeft.toFloat(), paddingTop.toFloat())

        //gravityのレイアウト座標を反映する
        mTextBounds.offset(0, boundVertical.top)
        //mTextBounds.top = vertical
        //mTextBounds.bottom = boundVertical.bottom
        Log.d("Test1", "onDraw( mTextBounds: $mTextBounds, mTextBounds.top: ${mTextBounds.top}")
        //onDraw( mTextBounds: Rect(1, 16 - 518, 123), mTextBounds.top: 16
        //Rect(int left, int top, int right, int bottom)

        canvas.drawRect(mTextBounds, mRectPaint)
        canvas.restore()

        /*
        final String s = "Hello. I'm some text!";

        Paint p = new Paint();
        Rect bounds = new Rect();
        p.setTextSize(60);

        p.getTextBounds(s, 0, s.length(), bounds);
        float mt = p.measureText(s);
        int bw = bounds.width();

        Log.i("LCG", String.format(
                "measureText %f, getTextBounds %d (%s)",
                mt,
                bw, bounds.toShortString())
        );
        bounds.offset(0, -bounds.top);
        p.setStyle(Paint.Style.STROKE);
        canvas.drawColor(0xff000080);
        p.setColor(0xffff0000);
        canvas.drawRect(bounds, p);
        p.setColor(0xff00ff00);
        canvas.drawText(s, 0, bounds.bottom, p);
         */
    }

    private fun getTightBounds(layout: Layout, line: Int, bounds: Rect) {
        val firstCharOnLine = layout.getLineStart(line)
        val lastCharOnLine = layout.getLineVisibleEnd(line)
        val s = text.subSequence(firstCharOnLine, lastCharOnLine)

        //Log.d("Test1", "getTightBounds( getText(): " + getText());
        Log.d(
            "Test1",
            "getTightBounds( firstCharOnLine: $firstCharOnLine lastCharOnLine: $lastCharOnLine"
        )
        Log.d("Test1", "getTightBounds( s: " + s + ", s.length() " + s.length)

        //getPaint().setTextAlign(Paint.Align.CENTER);

        // Get the bounds for the text. Top and bottom are measured from the baseline. Left
        // and right are measured from 0.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            paint.getTextBounds(s, 0, s.length, bounds)
        } else {
            //getPaint().measureText(s.toString());
            paint.getTextBounds(s.toString(), 0, s.length, bounds)
        }
        val baseline = layout.getLineBaseline(line)
        Log.d(
            "Test1",
            "getTightBounds( bounds.top: " + bounds.top + ", bounds.bottom: " + bounds.bottom
        )
        Log.d("Test1", "getTightBounds( baseline: $baseline")

        //bounds.top = baseline;
        //bounds.bottom = baseline;
        bounds.top = baseline + bounds.top
        bounds.bottom = baseline + bounds.bottom
        //Log.d("Test1", "getTightBounds( bounds.top: " + bounds.top + ", bounds.bottom: " + bounds.bottom);
    }
}