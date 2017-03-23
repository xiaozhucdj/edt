package com.yougy.view.showView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ScreenShotView extends View {
    private int left;
    private int top;
    private int right;
    private int bottom;
    private boolean sign;//绘画标记位
    private Paint paint;//画笔���
    private RectF rectF;

    public ScreenShotView(Context context) {
        super(context);
        init();
    }

    public ScreenShotView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        rectF = new RectF(0, 0, 0, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!sign) {
            paint.setColor(Color.TRANSPARENT);
        } else {
            //这两个条件判断是为了在left>right，top>bottom的情况下也能绘制出区域
            if (left > right) {
                int tmp = left;
                left = right;
                right = tmp;
            }
            if (top > bottom) {
                int tmp = top;
                top = bottom;
                bottom = tmp;
            }
            rectF.set(left, top, right, bottom);
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(3.0f);
//            paint.setAlpha(80);
            canvas.drawRect(rectF, paint);
        }
        super.onDraw(canvas);
    }

    public void setSeat(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public boolean isSign() {
        return sign;
    }

    public void setSign(boolean sign) {
        this.sign = sign;
    }

    public int getLeftValue(){
        return this.left;
    }

    public int getTopValue(){
        return this.top;
    }

    public int getCutWidth(){
        return this.right - this.left;
    }
}
