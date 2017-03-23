package com.yougy.view.showView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jiangliang on 2016/7/28.
 */
public class ColorView extends View {
    public ColorView(Context context) {
        super(context);
        init();
    }

    public ColorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private Paint fillPaint;
    private Paint strokePaint;
    private RectF fillRectF;
    private RectF strokeRectF;

    private void init() {
        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        strokePaint = new Paint();
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(2);
        strokePaint.setColor(0xffefefef);
        fillRectF = new RectF(1, 1, 49, 29);
        strokeRectF = new RectF(0, 0, 50, 30);
    }

    public void setFillColor(int color){
        fillPaint.setColor(color);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(50,30);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(fillRectF, fillPaint);
        canvas.drawRect(strokeRectF, strokePaint);
    }
}
