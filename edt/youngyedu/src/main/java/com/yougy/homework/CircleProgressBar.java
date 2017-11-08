package com.yougy.homework;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by FH on 2017/9/3.
 */

public class CircleProgressBar extends View{
    Paint mPaint;
    private int mHeight;
    private int mWidth;
    private int progress = 70;
    private String text = "";


    public CircleProgressBar(Context context) {
        super(context);
        init();
    }

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CircleProgressBar setProgress(int progress) {
        if (progress >= 0){
            this.progress = progress;
        }
        postInvalidate();
        return this;
    }

    public String getText() {
        return text;
    }

    public CircleProgressBar setText(String text) {
        this.text = text;
        postInvalidate();
        return this;
    }

    private Paint getPaint (int color , Paint.Style style , float strokeWidth , float textSize){
        if (mPaint == null){
            mPaint = new Paint();
        }
        mPaint.setColor(color);
        mPaint.setStyle(style);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(textSize);
        return mPaint;
    }
    private void init(){
        //测试进度条走动
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimation();
            }
        });
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int measuredHeight, measuredWidth;

        if (widthMode == MeasureSpec.EXACTLY) {
            measuredWidth = widthSize;
        } else {
            measuredWidth = 15;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            measuredHeight = heightSize;
        } else {
            measuredHeight = 15;
        }
        Log.v("FH" , "onMeasure measuredWidth = " + measuredWidth + "  measuredHeight " + measuredHeight);
        setMeasuredDimension(measuredWidth , measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth();
        mHeight = getHeight();
        Log.v("FH" , "onLayout mWidth " + mWidth + " mHeight " + mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.v("FH" , "onDraw");
        //进度条半径
        float radius = (mWidth > mHeight ? mHeight : mWidth) / 2;
        //这个缩放值是调试调出来的
        float scale = radius / 540;
        if (radius <= 0){
            //进度条半径为0的时候啥都不画
            return;
        }
        //进度条粗细
        float lineWidth = 40*scale;
        //底层圆粗细
        float bgLineWidth = 10*scale;
        //由于圆线条有粗细,所以需要留够一定的padding值,否则圆线条会超出控件
        float padding = 30*scale;
        RectF oval = new RectF(mWidth/2-radius+padding , mHeight/2-radius+padding , mWidth/2+radius-padding, mHeight/2+radius-padding);
        //先画底层圆
        canvas.drawArc(oval , 0 , 360 , false , getPaint(0xff2bc5c5 , Paint.Style.STROKE, bgLineWidth , 0));
        //再画实际进度圆弧
        canvas.drawArc(oval , 360-(progress*360/100) , progress*360/100 , false , getPaint(0xff2bc5c5 , Paint.Style.STROKE , lineWidth , 0));

        //写字
        StringBuilder sb = new StringBuilder(text);
        int maxTextLength = 10;
        //如果字符数多于10个,则截断并添加省略号
        if (sb.length() > maxTextLength){
            sb.delete(maxTextLength , sb.length());
            sb.append("...");
        }
        //
        if (sb.length() < maxTextLength){
            int tempInt = (maxTextLength-sb.length());
            for (int i = 0 ; i < tempInt ; i++){
                sb.insert(0 , " ");
                sb.append(" ");
            }
        }
        //写上方文字
        float textSizeAbove = 120*scale;
        canvas.drawText(sb.toString() , mWidth/2 - 320*scale , mHeight/2 - 80*scale ,
                getPaint(Color.BLACK , Paint.Style.FILL, 0 , textSizeAbove));
        //写下方文字
        float textSizeBelow = 195*scale;
        canvas.drawText("正确率" , mWidth/2 - 290*scale , mHeight/2 + 200*scale ,
                getPaint(Color.BLACK , Paint.Style.FILL, 0 , textSizeBelow));
    }

    private ValueAnimator mAnimator;

    public void startAnimation(){
        mAnimator = ValueAnimator.ofInt(0 , 100);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                Log.v("FH" , "animate : " + value);
                setProgress(value);
            }
        });
        mAnimator.setRepeatCount(-1);
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setDuration(5000);
        mAnimator.start();
    }
}
