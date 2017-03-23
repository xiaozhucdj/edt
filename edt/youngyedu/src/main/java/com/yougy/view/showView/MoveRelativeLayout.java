package com.yougy.view.showView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.yougy.common.utils.LogUtils;
import com.yougy.home.bean.Diagram;
import com.yougy.home.bean.Photograph;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;


/**
 * Created by jiangliang on 2016/7/13.
 */
public class MoveRelativeLayout extends RelativeLayout implements View.OnTouchListener {

    private float mRawX;
    private float mRawY;
    private float mStartX;
    private float mStartY;
    private float tempX;
    private float tempY;
    private Photograph photo;
    private GestureDetector mGestureDetector;

    private int count;
    private static final int MAX_COUNT = 6;

    public void setPhotoGraph(Photograph photo) {
        this.photo = photo;
    }

    private Diagram diagram;

    public void setDiagram(Diagram diagram) {
        this.diagram = diagram;
    }

    public Diagram getDiagram() {
        return diagram;
    }

    public Photograph getPhotoGraph() {
        return photo;
    }

    public MoveRelativeLayout(Context context) {
        super(context);
        init();
    }

    public MoveRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mGestureDetector = new GestureDetector(getContext(), new MyOnGestureListener());
        setOnTouchListener(this);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        final int action = event.getAction();
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                mStartX = event.getX();
//                mStartY = event.getY();
//                tempX = event.getRawX();
//                tempY = event.getRawY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                mRawX = event.getRawX();
//                mRawY = event.getRawY();
//                count++;
//                break;
//            case MotionEvent.ACTION_UP:
//                if ((int) tempX != (int) mRawX && (int) tempY != (int) mRawY) {
//                    updateWindowPosition();
//                }
//                if (count < MAX_COUNT) {
//                    performClick();
//                } else {
//                    Observable.create(new Observable.OnSubscribe<Object>() {
//                        @Override
//                        public void call(Subscriber<? super Object> subscriber) {
//                            if (photo != null) {
//                                photo.setLeftMargin(params.leftMargin);
//                                photo.setTopMargin(params.topMargin);
//                                int updatecount = photo.update(photo.getId());
//                                LogUtils.e("MoveRelativeLayout", "photo updatecount is : " + updatecount);
//                            }
//
//                            if (diagram != null) {
//                                diagram.setLeftMargin(params.leftMargin);
//                                diagram.setTopMargin(params.topMargin);
//                                int updatecount = diagram.update(diagram.getId());
//                                LogUtils.e("MoveRelativeLayout", "diagram update count is : " + updatecount);
//                            }
//                        }
//                    }).subscribeOn(Schedulers.io()).subscribe();
//                    count = 0;
//                    if (null != onHideDeleteBtnListener) {
//                        onHideDeleteBtnListener.hideDeleteBtn();
//                    }
//                }
//                break;
//        }
//        return true;
//    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    public interface OnHideDeleteBtnListener {
        void hideDeleteBtn();
    }

    private OnHideDeleteBtnListener onHideDeleteBtnListener;

    public void setOnHideDeleteBtnListener(OnHideDeleteBtnListener onHideDeleteBtnListener) {
        this.onHideDeleteBtnListener = onHideDeleteBtnListener;
    }


    private FrameLayout.LayoutParams params;
    private FrameLayout viewGroup;

    private void updateWindowPosition() {
        viewGroup = (FrameLayout) getParent();
        params = (FrameLayout.LayoutParams) getLayoutParams();
        params.leftMargin = (int) (mRawX - mStartX);
        params.topMargin = (int) (mRawY - mStartY - getStatusBarHeight() - 60);
        setLayoutParams(params);

    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            mStartX = e.getX();
            mStartY = e.getY();
            tempX = e.getRawX();
            tempY = e.getRawY();
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            performClick();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mRawX = e2.getRawX();
            mRawY = e2.getRawY();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if ((int) tempX != (int) mRawX && (int) tempY != (int) mRawY) {
                updateWindowPosition();
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
            return true;
        }
    }


}
