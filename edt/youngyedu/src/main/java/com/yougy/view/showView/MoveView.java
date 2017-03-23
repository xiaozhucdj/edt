package com.yougy.view.showView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.yougy.common.utils.LogUtils;

/**
 * Created by jiangliang on 2017/1/12.
 */

public class MoveView extends ImageView implements View.OnTouchListener {

    private GestureDetector mGestureDetector;

    public MoveView(Context context) {
        super(context);
        init(context);
    }

    public MoveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        mGestureDetector = new GestureDetector(context,new MySimpleOnGestureListener());
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    private class MySimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            LogUtils.e("MoveView","onSingleTapUp............"+e.getX());
            performClick();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            LogUtils.e("MoveView","onFling............");
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            LogUtils.e("MoveView","onScroll...........");
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            LogUtils.e("MoveView","onDown...........");
            return true;
        }
    }



}
