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
import com.yougy.home.bean.Photograph;
import com.yougy.home.bean.Position;
import com.yougy.ui.activity.R;


public class MoveImageView extends ImageView implements View.OnTouchListener {


    private float mStartX;
    private float mStartY;
    private static final String TAG = "MoveImageView";
    private int screenHeight;
    private int screenWidth;


    private Position mOldPosition;

    public void setOldPosition(Position oldPosition) {
        mOldPosition = oldPosition;
    }

    public Position getOldPosition() {
        return mOldPosition;
    }

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

    private void init() {
        mGestureDetector = new GestureDetector(getContext(), new MyOnGestureListener());
        screenHeight = UIUtils.getScreenHeight();
        screenWidth = UIUtils.getScreenWidth();
        setOnTouchListener(this);
    }

    private boolean flag;

    public void setFlag() {
        flag = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            mStartX = event.getRawX();
            mStartY = event.getRawY();
        }
        if (event.getAction() == MotionEvent.ACTION_UP && null != updateImageViewMapListener) {
            updateWindowPosition();
            updateImageViewMapListener.updateImageViewMap(new Position(params.leftMargin, params.topMargin), type, MoveImageView.this);
        }
        return true;
    }

    private Label label;

    public void setLabel(Label label) {
        type = TYPE_LABEL;
        this.label = label;
    }

    private Photograph photo;

    public void setPhotograph(Photograph photo) {
        type = TYPE_PHOTO;
        this.photo = photo;
    }

    private int type;

    public static final int TYPE_LABEL = 1;
    public static final int TYPE_PHOTO = 2;

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
        if (null != label) {
            label.setLeftMargin(params.leftMargin);
            label.setTopMargin(params.topMargin);
        }

        if (null != photo) {
            photo.setLeftMargin(params.leftMargin);
            photo.setTopMargin(params.topMargin);
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
        void updateImageViewMap(Position position, int type, MoveImageView imageView);
    }

    private class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            if (null != label) {
                setImageResource(R.drawable.icon_label_pressed);
            } else {
                setImageResource(R.drawable.img_pic_press);
            }
            mStartX = e.getRawX();
            mStartY = e.getRawY();
            return true;
        }


        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            performClick();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mStartX = e2.getRawX();
            mStartY = e2.getRawY();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            updateWindowPosition();
            if (null != updateImageViewMapListener) {
                updateImageViewMapListener.updateImageViewMap(new Position(params.leftMargin, params.topMargin), type, MoveImageView.this);
            }
            return true;
        }
    }
}
