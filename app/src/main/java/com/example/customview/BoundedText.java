package com.example.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;


public class BoundedText extends AppCompatTextView {
    //public class BoundedText extends androidx.appcompat.widget.AppCompatTextView {
    private final Rect mLineBounds = new Rect();
    private final Rect mTextBounds = new Rect();
    private final Paint mRectPaint = new Paint();

    public BoundedText(Context context) {
        super(context);
        init();
    }

    public BoundedText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BoundedText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setStrokeWidth(1f);
        mRectPaint.setColor(Color.RED);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        Layout layout = getLayout();
        getTightBounds(layout, 0, mTextBounds);

        if (layout.getLineCount() > 1) { // Multi-line
            // We already have bounds of first line. Check bounds of last line since it will
            // be the bottom of the bounding rectangle for all the text.
            getTightBounds(layout, layout.getLineCount() - 1, mLineBounds);
            mTextBounds.bottom = mLineBounds.bottom;

            // Now check remaining lines for min left bound and max right bound.
            for (int line = 1; line < layout.getLineCount(); line++) {
                Log.d("Test", "line: " + line);
                getTightBounds(layout, line, mLineBounds);
                if (mLineBounds.left < mTextBounds.left) {
                    mTextBounds.left = mLineBounds.left;
                }
                if (mLineBounds.right > mTextBounds.right) {
                    mTextBounds.right = mLineBounds.right;
                }
            }
            Log.d("Test", "mTextBounds.top: " + mTextBounds.top);
            Log.d("Test", "mTextBounds.bottom: " + mTextBounds.bottom);

            // Convert pixel values to dp
            float density = getResources().getDisplayMetrics().density;
            float textBoundsTopDp = mTextBounds.top / density;
            float textBoundsBottomDp = mTextBounds.bottom / density;
            float textBoundsLeftDp = mTextBounds.left / density;
            float textBoundsRightDp = mTextBounds.right / density;
            Log.d("Test", "mTextBounds.top (dp): " + textBoundsTopDp);
            Log.d("Test", "mTextBounds.bottom (dp): " + textBoundsBottomDp);
            Log.d("Test", "mTextBounds.left (dp): " + textBoundsLeftDp);
            Log.d("Test", "mTextBounds.right (dp): " + textBoundsRightDp);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());
        canvas.drawRect(mTextBounds, mRectPaint);
        canvas.restore();

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

    private void getTightBounds(Layout layout, int line, Rect bounds) {
        int firstCharOnLine = layout.getLineStart(line);
        int lastCharOnLine = layout.getLineVisibleEnd(line);
        CharSequence s = getText().subSequence(firstCharOnLine, lastCharOnLine);

        //Log.d("Test", "getTightBounds( getText(): " + getText());
        Log.d("Test", "getTightBounds( firstCharOnLine: " + firstCharOnLine + " lastCharOnLine: " + lastCharOnLine);
        Log.d("Test", "getTightBounds( s: " + s + ", s.length() " + s.length());

        //getPaint().setTextAlign(Paint.Align.CENTER);

        // Get the bounds for the text. Top and bottom are measured from the baseline. Left
        // and right are measured from 0.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getPaint().getTextBounds(s, 0, s.length(), bounds);
        } else {
            //getPaint().measureText(s.toString());
            getPaint().getTextBounds(s.toString(), 0, s.length(), bounds);
        }
        int baseline = layout.getLineBaseline(line);
        Log.d("Test", "getTightBounds( bounds.top: " + bounds.top + ", bounds.bottom: " + bounds.bottom);
        Log.d("Test", "getTightBounds( baseline: " + baseline);

        //bounds.top = baseline;
        //bounds.bottom = baseline;

        bounds.top = baseline + bounds.top;
        bounds.bottom = baseline + bounds.bottom;
        //Log.d("Test", "getTightBounds( bounds.top: " + bounds.top + ", bounds.bottom: " + bounds.bottom);
    }
}