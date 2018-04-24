package com.yougy.view.controlView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.yougy.common.utils.UIUtils;

/**
 * Created by Administrator on 2016/8/24.
 *  对PDF 手势操作的封装
 */
public class ControlView extends FrameLayout implements GestureDetector.OnGestureListener {
    //手势识别器
    private GestureDetector mGestureDetector;
    private int tapPageMargin;
    private PagerChangerListener mPagerListener ;

    private boolean  mIntercept = false ;


    public ControlView(Context context) {
        super(context);
        init();
//        setBackgroundColor(UIUtils.getColor(R.color.red));
    }

    public ControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
//        setBackgroundColor(UIUtils.getColor(R.color.red));
    }

    public ControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
//        setBackgroundColor(UIUtils.getColor(R.color.red));
    }
    /**
     * 初始化 手势识别器
     */
    private void init() {
        mGestureDetector = new GestureDetector(this);
        DisplayMetrics dm = UIUtils.getDisplayMetrics();
        tapPageMargin = (int) dm.xdpi;
        if (tapPageMargin < 100)
            tapPageMargin = 100;
        if (tapPageMargin > dm.widthPixels / 5)
            tapPageMargin = dm.widthPixels / 5;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        if (mPagerListener== null){
//            System.out.println("11----- mPagerListener == null");
            return  false;
        }

        if (e.getX() < tapPageMargin) {
            //切换 上一页
            mPagerListener.smartMoveBackwards();
        } else if (e.getX() > super.getWidth() - tapPageMargin) {
            //切换 下一页
            mPagerListener.smartMoveForwards();
        } else if (e.getY() < tapPageMargin) {
            //切换 上一页
            mPagerListener.smartMoveBackwards();
        }else{
            mPagerListener.onTapMainDocArea();
        }
        return false;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return mIntercept ;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true ;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }




    /**当前*/
    public interface PagerChangerListener {
        /**上一页*/
        void smartMoveBackwards();
        /**下一页*/
        void smartMoveForwards();
        /**其他区域 用来显示seekbar*/
        void onTapMainDocArea() ;

    }


    /**
     * 设置 页码切换 回调
     * @param listener 外部实现接口
     */
    public  void setPagerListener(PagerChangerListener listener){
        mPagerListener = listener ;
    }

    /***
     *  事件 拦截 Flag
     * @param intercept
     *          intercept == true  拦截事件 ，使用自身的onTouchEvent ，实现 左右点击 切换View
     *          intercept == false 不对事件拦截 由子View 处理
     */
    public void  setIntercept(boolean intercept){
        mIntercept = intercept ;
    }
}

