package com.yougy.view.showView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.bean.Diagram;
import com.yougy.home.bean.Photograph;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by jiangliang on 2017/6/21.
 */

public class MoveRelativeLayout1 extends RelativeLayout implements View.OnTouchListener {
    private Photograph photo;
    private GestureDetector mGestureDetector;
    private float mStartX;
    private float mStartY;
    private int screenHeight;
    private int screenWidth;

    private Diagram diagram;

    public void setDiagram(Diagram diagram) {
        this.diagram = diagram;
    }
    public void setPhotoGraph(Photograph photo) {
        this.photo = photo;
    }

    public Photograph getPhotoGraph() {
        return photo;
    }

    public MoveRelativeLayout1(Context context) {
        super(context);
        init();
    }

    public MoveRelativeLayout1(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    private void init(){
        mGestureDetector = new GestureDetector(getContext(), new MyOnGestureListener());
        screenHeight = UIUtils.getScreenHeight();
        screenWidth = UIUtils.getScreenWidth();
        setOnTouchListener(this);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            mStartX = event.getRawX();
            mStartY = event.getRawY();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            updateWindowPosition();
        }
        return true;
    }

    public interface OnHideDeleteBtnListener {
        void hideDeleteBtn();
    }

    private OnHideDeleteBtnListener onHideDeleteBtnListener;

    public void setOnHideDeleteBtnListener(OnHideDeleteBtnListener onHideDeleteBtnListener) {
        this.onHideDeleteBtnListener = onHideDeleteBtnListener;
    }

    private FrameLayout.LayoutParams params;
    private void updateWindowPosition() {
        params = (FrameLayout.LayoutParams) getLayoutParams();
        int y = screenHeight - 61;
        LogUtils.e(getClass().getName(), "leftmargin : " + mStartX + ",topmargin : " + mStartY + ",Y:" + y);
        int x = screenWidth - 50;
        if (mStartX < x) {
            params.leftMargin = (int) mStartX;
        } else {
            params.leftMargin = x;
        }
        if (mStartY < 60) {
            params.topMargin = 60;
        } else if (mStartY > y) {
            LogUtils.e(getClass().getName(), "startY..................." + mStartY);
            params.topMargin = y - 50;
        } else {
            params.topMargin = (int) mStartY;
        }
        setLayoutParams(params);

        if (null != photo) {
            photo.setLeftMargin(params.leftMargin);
            photo.setTopMargin(params.topMargin);
        }
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                if (photo != null) {
                    photo.setLeftMargin(params.leftMargin);
                    photo.setTopMargin(params.topMargin);
                    int updatecount = photo.update(photo.getId());
                    LogUtils.e("MoveRelativeLayout", "photo updatecount is : " + updatecount);
                }

                if (diagram != null) {
                    diagram.setLeftMargin(params.leftMargin);
                    diagram.setTopMargin(params.topMargin);
                    int updatecount = diagram.update(diagram.getId());
                    LogUtils.e("MoveRelativeLayout", "diagram update count is : " + updatecount);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
        if (null != onHideDeleteBtnListener) {
            onHideDeleteBtnListener.hideDeleteBtn();
        }
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }
    private class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            mStartX = e.getRawX();
            mStartY = e.getRawY();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mStartX = e2.getRawX();
            mStartY = e2.getRawY();
            return true;
        }
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            performClick();
            return true;
        }
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            updateWindowPosition();
            return true;
        }
    }
}
