package com.yougy.view.showView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.bean.Label;
import com.yougy.home.bean.Position;
import com.yougy.ui.activity.R;


public class MoveImageView extends ImageView implements View.OnTouchListener {


    private float mRawX;
    private float mRawY;
    private float mStartX;
    private float mStartY;
    private static final String TAG = "MoveImageView";

    public MoveImageView(Context context) {
        super(context);
        init();
    }

    public MoveImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MoveImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (flag) {
            setMeasuredDimension(200, 200);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
    private GestureDetector mGestureDetector;

    private void init(){
        mGestureDetector = new GestureDetector(getContext(),new MyOnGestureListener());
        setOnTouchListener(this);
    }
    private boolean flag;

    public void setFlag() {
        flag = true;
    }

    //用于判断是执行点击事件，还是更新View的位置。因为点击时也会出发几次move事件
    private int count = 0;
    private static final int MAX_COUNT = 40;

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        final int action = event.getAction();
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                setImageResource(R.drawable.icon_label_pressed);
//                mStartX = event.getX();
//                mStartY = event.getY();
//                LogUtils.e(TAG,"onTouchEvent x : " + mStartX);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                mRawX = event.getRawX();
//                mRawY = event.getRawY();
//                LogUtils.e(TAG,"onTouchEvent rawX : " + mRawX);
//                count++;
//                break;
//
//            case MotionEvent.ACTION_UP:
//                if (count < MAX_COUNT) {
//                    performClick();
//                } else {
//                    updateWindowPosition();
//                    if (null != updateImageViewMapListener) {
//                        updateImageViewMapListener.updateImageViewMap(new Position(params.leftMargin, params.topMargin), this);
//                    }
//                }
//                count = 0;
//                break;
//        }
//
//        return true;
//    }

    private Label label;

    public void setLabel(Label label) {
        this.label = label;
    }

    private FrameLayout.LayoutParams params;
    private FrameLayout viewGroup;

    private void updateWindowPosition() {
        viewGroup = (FrameLayout) getParent();
        params = (FrameLayout.LayoutParams) getLayoutParams();

        //设置边距 W
        if (mRawX - mStartX < 0) {
            params.leftMargin = 0;
        } else if (mRawX - mStartX > getMeasuredWidth()) {
            params.leftMargin = (int) (mRawX - getMeasuredWidth());
        } else {
            params.leftMargin = (int) (mRawX - mStartX);
        }

        LogUtils.i(mRawY +":"+mStartY);
        LogUtils.i(mRawY - mStartY+"rrr");
        LogUtils.i(UIUtils.getScreenHeight()-111+"111");

        //设置边距 H
        if (mRawY - mStartY <UIUtils.getStatusBarHeight() +60) {
            LogUtils.i("H1");
            params.topMargin = 60 ;
        } else if (mRawY > UIUtils.getScreenHeight()-111) {
            LogUtils.i("H2");
            params.topMargin = UIUtils.getScreenHeight() - UIUtils.getStatusBarHeight()-getMeasuredHeight()-60;
        } else {
            LogUtils.i("H3");
           params.topMargin = (int) (mRawY - mStartY - UIUtils.getStatusBarHeight());
        }

        setLayoutParams(params);
        if (null != label) {
            label.setLeftMargin(params.leftMargin);
            label.setTopMargin(params.topMargin);
            LogUtils.e(TAG, "viewgroup's width is : " + viewGroup.getWidth() + ",height is : " + viewGroup.getHeight());
        }
    }


    private UpdateImageViewMapListener updateImageViewMapListener;

    public void setUpdateImageViewMapListener(UpdateImageViewMapListener updateImageViewMapListener) {
        this.updateImageViewMapListener = updateImageViewMapListener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    public interface UpdateImageViewMapListener {
        void updateImageViewMap(Position position, MoveImageView imageView);
    }

    private class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent e) {
            LogUtils.e(TAG,"onDown......"+e.getX());
            setImageResource(R.drawable.icon_label_pressed);
            mStartX = e.getX();
            mStartY = e.getY();
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            performClick();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            LogUtils.e(TAG,"onScroll .... rawX1 : " + e1.getRawX()+",rawX2 : " + e2.getRawX());
            mRawX = e2.getRawX();
            mRawY = e2.getRawY();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            LogUtils.e(TAG,"onFling .... rawX1 : " + e1.getRawX()+",rawX2 : " + e2.getRawX());
            updateWindowPosition();
            if (null != updateImageViewMapListener) {
                updateImageViewMapListener.updateImageViewMap(new Position(params.leftMargin, params.topMargin), MoveImageView.this);
            }
            return true;
        }
    }
}
