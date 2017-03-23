package com.yougy.view.showView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.yougy.common.utils.LogUtils;


/**
 * Created by Administrator on 2017/1/19.
 */

public class TextThumbSeekBar extends SeekBar {

    private int mThumbSize;//绘制滑块宽度
    private TextPaint mTextPaint;//绘制文本的大小
    private int mSeekBarMin=0;//滑块开始值
    private int mCounts;
    private int mPageSliderRes;

    public TextThumbSeekBar(Context context) {
        this(context, null);
    }

    public TextThumbSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.seekBarStyle);
    }

    public TextThumbSeekBar(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        mThumbSize=94;
        mTextPaint = new TextPaint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(16);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }
    Rect bounds ;
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int unsignedMin = mSeekBarMin < 0 ? mSeekBarMin * -1 : mSeekBarMin;
        String progressText = String.valueOf(getProgress()+unsignedMin);
        if (bounds == null){
             bounds = new Rect();
        }
        String text = (String.format("%d / %d",( (getProgress() + mPageSliderRes / 2) / mPageSliderRes) + 1, mCounts));
        mTextPaint.getTextBounds(text, 0, text.length(), bounds);

        int leftPadding = getPaddingLeft() - getThumbOffset();
        int rightPadding = getPaddingRight() - getThumbOffset();
        int width = getWidth() - leftPadding - rightPadding;
        float progressRatio = (float) getProgress() / getMax();
        float thumbOffset = mThumbSize * (.5f - progressRatio);
        float thumbX = progressRatio * width + leftPadding + thumbOffset;
//        float thumbY = getHeight() / 2f + bounds.height() / 2f;
        float thumbY = getHeight() / 2f +5;
//        LogUtils.i("yuanye ...theum =="+progressText);
//        LogUtils.i("yuanye ...thumbY =="+thumbY);
        canvas.drawText(text, thumbX, thumbY, mTextPaint);
    }

    public void setMix(int min){
        mSeekBarMin=min;
    }

    public void setPdfCounts(int counts){
        mCounts = counts ;
    }

    public void setPageSliderRes(int pageSliderRes ){
        mPageSliderRes = pageSliderRes ;
    }


}
