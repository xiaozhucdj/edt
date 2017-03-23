package com.yougy.view.showView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by jiangliang on 2016/7/6.
 */
public class MyFrameLayout extends FrameLayout {
    public MyFrameLayout(Context context) {
        super(context);
    }

    public MyFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isInterceptable;
    }

    private boolean isInterceptable;

    public void setInterceptable(boolean isInterceptable) {
        this.isInterceptable = isInterceptable;
    }

    private float x;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getRawX();
                break;
            case MotionEvent.ACTION_UP:
                if (onFlipOverListener != null) {
                    if (x <= 100) {
                        onFlipOverListener.previousPage();
                    }
                    if (x >= 650) {
                        onFlipOverListener.nextPage();
                    }
                }
                break;
        }
        return true;
    }

    private OnFlipOverListener onFlipOverListener;

    public void setOnFlipOverListener(OnFlipOverListener onFlipOverListener) {
        this.onFlipOverListener = onFlipOverListener;
    }

    public interface OnFlipOverListener {
        void nextPage();

        void previousPage();
    }

}
