package com.onyx.android.sdk.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.onyx.android.sdk.R;

/**
 * Created by solskjaer49 on 15/8/21 18:28.
 */

public class DottedProgressBar extends View {
    static final String TAG = DottedProgressBar.class.getSimpleName();
    private final float mDotSize;
    private final float mSpacing;
    private int mEmptyDotsColor = Color.WHITE;
    private int mActiveDotColor = Color.BLACK;

    private int mNumberOfDots;
    private float mMaxProgress = 100;
    private float mCurrentProgress = 0;
    private Paint mPaint;
    private int mPaddingLeft;


    public DottedProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DottedProgressBar,
                0, 0);

        try {
            TypedValue value = new TypedValue();

            a.getValue(R.styleable.DottedProgressBar_activeDotColor, value);
            if (value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                mActiveDotColor = getResources().getColor(value.resourceId);
            }

            a.getValue(R.styleable.DottedProgressBar_inactiveDotColor, value);
            if (value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                mEmptyDotsColor = getResources().getColor(value.resourceId);
            }

            mDotSize = a.getDimensionPixelSize(R.styleable.DottedProgressBar_dotSize, 5);
            mSpacing = a.getDimensionPixelSize(R.styleable.DottedProgressBar_spacing, 10);

            mCurrentProgress = a.getFloat(R.styleable.DottedProgressBar_currentProgress, 0);
            mMaxProgress = a.getFloat(R.styleable.DottedProgressBar_maxProgress, 100);


            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setStyle(Paint.Style.FILL);

        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < mNumberOfDots; i++) {
            int x = (int) (getPaddingLeft() + mPaddingLeft + mSpacing / 2 + i * (mSpacing + mDotSize));
            if (isActiveDot(i)) {
                mPaint.setColor(mActiveDotColor);
                canvas.drawCircle(x + mDotSize / 2,
                        getPaddingTop() + mDotSize / 2, mDotSize / 2, mPaint);
            } else {
                mPaint.setColor(mEmptyDotsColor);
                canvas.drawCircle(x + mDotSize / 2,
                        getPaddingTop() + mDotSize / 2, mDotSize / 2, mPaint);

            }
        }
    }

    private boolean isActiveDot(int dotIndex) {
        return (dotIndex < ((mCurrentProgress / mMaxProgress) * mNumberOfDots));
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);

        int widthWithoutPadding = parentWidth - getPaddingLeft() - getPaddingRight();
        int heigthWithoutPadding = parentHeight - getPaddingTop() - getPaddingBottom();

        int calculatedHeight = getPaddingTop() + getPaddingBottom() + (int) mDotSize;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, calculatedHeight);
        mNumberOfDots = calculateDotsNumber(widthWithoutPadding);
    }

    private int calculateDotsNumber(int width) {
        int number = (int) (width / (mDotSize + mSpacing));
        mPaddingLeft = (int) ((width % (mDotSize + mSpacing)) / 2);
        return number;
    }

    public void setMax(float maxProgress) {
        if (maxProgress > 0) {
            mMaxProgress = maxProgress;
        }
    }

    public void setProgress(float currentProgress) {
        if (currentProgress >= 0) {
            mCurrentProgress = currentProgress;
        }
        invalidate();
    }

    public float getMax() {
        return mMaxProgress;
    }

    public float getProgress() {
        return mCurrentProgress;
    }
}