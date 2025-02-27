package com.example.customview

import android.annotation.SuppressLint
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
import android.widget.TextView

@SuppressLint("AppCompatCustomView")
class BoundedText : TextView {
//class BoundedText : AppCompatTextView {
    //public class BoundedText extends androidx.appcompat.widget.AppCompatTextView {
    private val mLineBounds = Rect()
    private val mTextBounds = Rect()
    private val mRectPaint = Paint()

    private val mPaddingBounds = Rect()
    private val mPaddingPaint = Paint()

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

        mPaddingPaint.style = Paint.Style.STROKE
        mPaddingPaint.strokeWidth = 1f
        mPaddingPaint.color = Color.GREEN
    }

    private val boundVertical = Rect()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //Log.d("Test1", "onMeasure( widthMeasureSpec: $widthMeasureSpec, heightMeasureSpec: $heightMeasureSpec")
        getLineBounds(0, boundVertical)

        //Log.d("Test1", "onMeasure( boundVertical.top: ${boundVertical.top}, boundVertical.left: ${boundVertical.left}, boundVertical.right: ${boundVertical.right}, text: $text, boundVertical: $boundVertical")
        //onMeasure( boundVertical: Rect(0, 179 - 523, 255), text: TextView1TextView2TextView3
        //Rect(int left, int top, int right, int bottom)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        //Log.d("Test1", "onLayout( changed: $changed, left: $left, top: $top, right: $right, bottom: $bottom")

        // X方向のずれを修正したい： leftは0なので取得できない
        val rect = Rect()
        getLineBounds(0, rect)
        Log.d("Test1", "onLayout( rect.top: ${rect.top}, rect.left: ${rect.left}, rect.right: ${rect.right}, text: $text, rect: $rect")
        //onLayout( rect.top: 0, rect.left: 0, rect.right: 688, text: TextView1TextView2TextView3あああ, rect: Rect(0, 0 - 688, 68)

        val layout = layout
        getTightBounds(layout, 0, mTextBounds)
        if (layout.lineCount > 1) { // Multi-line
            // We already have bounds of first line. Check bounds of last line since it will
            // be the bottom of the bounding rectangle for all the text.
            getTightBounds(layout, layout.lineCount - 1, mLineBounds)
            mTextBounds.bottom = mLineBounds.bottom

            // Now check remaining lines for min left bound and max right bound.
            for (line in 1 until layout.lineCount) {
                Log.d("Test1", "onLayout( line: $line")
                getTightBounds(layout, line, mLineBounds)
                if (mLineBounds.left < mTextBounds.left) {
                    mTextBounds.left = mLineBounds.left
                }
                if (mLineBounds.right > mTextBounds.right) {
                    mTextBounds.right = mLineBounds.right
                }
            }
            Log.d("Test1", "onLayout( mTextBounds.top: " + mTextBounds.top)
            Log.d("Test1", "onLayout( mTextBounds.bottom: " + mTextBounds.bottom)

            // Convert pixel values to dp
            val density = resources.displayMetrics.density
            val textBoundsTopDp = mTextBounds.top / density
            val textBoundsBottomDp = mTextBounds.bottom / density
            val textBoundsLeftDp = mTextBounds.left / density
            val textBoundsRightDp = mTextBounds.right / density
            Log.d("Test1", "onLayout( mTextBounds.top (dp): $textBoundsTopDp")
            Log.d("Test1", "onLayout( mTextBounds.bottom (dp): $textBoundsBottomDp")
            Log.d("Test1", "onLayout( mTextBounds.left (dp): $textBoundsLeftDp")
            Log.d("Test1", "onLayout( mTextBounds.right (dp): $textBoundsRightDp")
        }
    }

    private fun calculatePadding() {
        Log.d("Test1", "calculatePadding() paddingTop: $paddingTop, paddingBottom: $paddingBottom, paddingLeft: $paddingLeft, paddingRight: $paddingRight")

        val top = 0 + paddingTop
        val bottom = this.height - paddingBottom
        val left = this.left - paddingLeft
        val right = this.right - paddingRight

        mPaddingBounds.top = top
        mPaddingBounds.bottom =  bottom
        mPaddingBounds.left = left
        mPaddingBounds.right = right

        //mTextBounds.offset(0, boundVertical.top)

        //paddingTopを指定するとgravityが設定、未設定にかかわらず文字の描画領域が正しく取得できない
        // paddingBottomを指定するとgravityにbottomが設定されている場合、文字の描画領域が正しく取得できない
        // paddingが未設定の場合、 文字の描画領域を正しく取得できる

        // memo
        // padding
        // gravityが設定されている状態でpaddingを設定すると正しい値が反映されない
        // android:paddingTop="1dp"を設定した場合、見た目1dp以上が設定されている
        // style & gravity
        // xmlレイアウトファイルにstyleを適応した場合、styleに設定しているgravityは正しく反映される
        // プログラムで指定した場合はstyleに設定しているgravityは正しく反映されない？
        // fontWeight
        Log.d("Test1", "calculatePadding() mPaddingBounds: $mPaddingBounds, mPaddingBounds.top: ${mPaddingBounds.top}, mPaddingBounds.bottom: ${mPaddingBounds.bottom}, mPaddingBounds.right: ${mPaddingBounds.right}, mPaddingBounds.left: ${mPaddingBounds.left}")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        Log.d("Test1", "onDraw( -------------------------- start -------------------")

        canvas.save()
        canvas.translate(paddingLeft.toFloat(), paddingTop.toFloat())

        //gravityのレイアウト座標を反映する
        mTextBounds.offset(0, boundVertical.top)
        //mTextBounds.top = vertical
        //mTextBounds.bottom = boundVertical.bottom
        Log.d("Test1", "onDraw( mTextBounds: $mTextBounds, mTextBounds.top: ${mTextBounds.top}")
        //onDraw( mTextBounds: Rect(1, 16 - 518, 123), mTextBounds.top: 16
        //Rect(int left, int top, int right, int bottom)

        calculatePadding()

        canvas.drawRect(mTextBounds, mRectPaint)
        // padding Rect
        canvas.drawRect(mPaddingBounds, mPaddingPaint)

        canvas.restore()
    }

    private fun getTightBounds(layout: Layout, line: Int, bounds: Rect) {
        val firstCharOnLine = layout.getLineStart(line)
        val lastCharOnLine = layout.getLineVisibleEnd(line)
        val s = text.subSequence(firstCharOnLine, lastCharOnLine)

        //Log.d("Test1", "getTightBounds( getText(): " + getText());
        Log.d("Test1", "getTightBounds( firstCharOnLine: $firstCharOnLine lastCharOnLine: $lastCharOnLine")
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

        // X方向のずれを修正したい： leftは0なので取得できない
        val rect = Rect()
        getLineBounds(0, rect)
        Log.d("Test1", "getTightBounds( rect.top: ${rect.top}, rect.left: ${rect.left}, rect.right: ${rect.right}, text: $text, rect: $rect")
        //getTightBounds( rect.top: 0, rect.left: 0, rect.right: 688, text: TextView1TextView2TextView3あああ, rect: Rect(0, 0 - 688, 68)

        val baseline = layout.getLineBaseline(line)
        Log.d("Test1", "getTightBounds( bounds.top: " + bounds.top + ", bounds.bottom: " + bounds.bottom)
        Log.d("Test1", "getTightBounds( baseline: $baseline")

        bounds.top = baseline + bounds.top
        bounds.bottom = baseline + bounds.bottom
        //Log.d("Test1", "getTightBounds( bounds.top: " + bounds.top + ", bounds.bottom: " + bounds.bottom);
    }
}