package com.artifex.mupdfdemo.pdf.View;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Scroller;

import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * 功能： 用来显示PDF ,并且执行一些手势的操作
 */
public class ReaderView extends AdapterView<Adapter> implements GestureDetector.OnGestureListener, ScaleGestureDetector.OnScaleGestureListener, Runnable {

    /**判断笔和手 是否需要拦截事件*/
    private boolean mInterceptTouch = true;

    private final String TAG = "ReaderView  for tag";
    private boolean mDeBug = true;
    ////////////////////////////////成员 常量////////////////////////////////////////

    private static final int MOVING_DIAGONALLY = 0;
    private static final int MOVING_LEFT = 1;
    private static final int MOVING_RIGHT = 2;
    private static final int MOVING_UP = 3;
    private static final int MOVING_DOWN = 4;
    private static final int FLING_MARGIN = 100;
    private static final int GAP = 20;
    private static final float MIN_SCALE = 1.0f;
    private static final float MAX_SCALE = 5.0f;
    private static final float REFLOW_SCALE_FACTOR = 0.5f;

    ////////////////////////////////成员 私有变量////////////////////////////////////////
    private Adapter mAdapter;

    //当前索引
    private int mCurrent;

    //是否从新设置 布局
    private boolean mResetLayout;

    // 显示布局的页数
    private final SparseArray<ViewGroup> mChildViews = new SparseArray<ViewGroup>(3);

    // 缓存的View --》是否可以理解为 阴影的显示页
    // Shadows the children of the adapter view
    // but with more sensible indexing
    //TODO:
    private final LinkedList<ViewGroup> mViewCache = new LinkedList<ViewGroup>();

    //TODO:
    // 用户的交互 这个字段 代表什么意思？
    private boolean mUserInteracting; // Whether the user is interacting

    // 是否在缩放
    private boolean mScaling; // Whether the user is currently pinch zooming

    // 缩放原始比例
    private float mScale = 1.0f;

    // 记录滚动事件的位置
    private int mXScroll; // Scroll amounts recorded from events.
    private int mYScroll; // and then accounted for in onLayout

    //是否 回滚
    //TODO:
    private boolean mReflow = false;

    //手势识别器
    private final GestureDetector mGestureDetector;

    //缩放识别器
    private final ScaleGestureDetector mScaleGestureDetector;

    // 滚动事件
    private final Scroller mScroller;

    //滚动的最后一个记录X
    private int mScrollerLastX;

    //滚动的最后一个记录Y
    private int mScrollerLastY;

    //是否可以滚动
    private boolean mScrollDisabled;

    @Override
    public void run() {
        if (!mScroller.isFinished()) { //滚动事件结束 会返回true 否则返回false
            mScroller.computeScrollOffset(); // 实时更新位置 ，
            int x = mScroller.getCurrX();  // 当前X
            int y = mScroller.getCurrY();   //当前Y
            mXScroll += x - mScrollerLastX; //X-上次的具体 就是偏移量
            mYScroll += y - mScrollerLastY;//Y-上次的具体 就是偏移量
            mScrollerLastX = x;
            mScrollerLastY = y;
            requestLayout();
            post(this);
        } else if (!mUserInteracting) {
            // End of an inertial scroll and the user is not interacting.
            // The layout is stable
            View v = mChildViews.get(mCurrent);
            if (v != null)
                postSettle(v);
        }
    }


    /////////////////////////////////静态的内部类///////////////////////////////////////
    //TODO:什么作用?
    static abstract class ViewMapper {
        abstract void applyToView(View view);
    }


    ////////////////////////////////构造 函数 ,初始化手势识别器////////////////////////////////////////
    public ReaderView(Context context) {
        super(context);
        mGestureDetector = new GestureDetector(this);
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        mScroller = new Scroller(context);
    }

    public ReaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(this);
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        mScroller = new Scroller(context);
    }


    public ReaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mGestureDetector = new GestureDetector(this);
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        mScroller = new Scroller(context);
    }


    ////////////////////////////////AdapterView 实现的 函数////////////////////////////////////////
    @Override
    public Adapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;
        mChildViews.clear();
        removeAllViewsInLayout();
        requestLayout();
    }

    @Override
    public View getSelectedView() {
        return null;
    }

    @Override
    public void setSelection(int i) {

    }


    ////////////////////////////////手势识别器////////////////////////////////////////

    //ACTION_DOWN时触发
    @Override
    public boolean onDown(MotionEvent motionEvent) {

//        System.out.println("onDown");
        // 设置 滚动事件 完成 ，并且消费了down事件
        mScroller.forceFinished(true);
        return true;
    }

    //ACTION_DOWN了过一会还没有滑动时触发，onDown->onShowPress->onLongPress
    @Override
    public void onShowPress(MotionEvent motionEvent) {
//        System.out.println("onShowPress");
    }

    //ACTION_DOWN后没有滑动（onScroll）且没有长按（onLongPress）接着ACTION_UP时触发
    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
//        System.out.println("onSingleTapUp");
        return false;
    }

    //滑动时实时触发
    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float distanceX, float distanceY) {
//        System.out.println("onScroll");
   /*     if (true)
        return  true;*/
       if (!mScrollDisabled) {
            mXScroll -= distanceX;
            mYScroll -= distanceY;
            requestLayout();
        }
        return true;
    }

    //ACTION_DOWN长按时触发
    @Override
    public void onLongPress(MotionEvent motionEvent) {
//        System.out.println("onLongPress");
    }

    //触摸滑动一定距离后松手ACTION_UP时触发，后参数为速率
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        //根据速率去判断 用户的滑动方向 ，并且会自动 切换item
//        System.out.println("onFling");
  /*    if (true)
        return  true;*/

        //如果是进行缩放  == mScrollDisabled = false
        if (mScrollDisabled)
            return true;

        View v = mChildViews.get(mCurrent);
        if (v != null) {
            Rect bounds = getScrollBounds(v);
            switch (directionOfTravel(velocityX, velocityY)) {
                case MOVING_LEFT:
                    if (bounds.left >= 0) {
                        // Fling off to the left bring next view onto screen
                        View vl = mChildViews.get(mCurrent + 1);
                        if (vl != null) {
                            slideViewOntoScreen(vl);
                            return true;
                        }
                    }
                    break;
                case MOVING_RIGHT:
                    if (bounds.right <= 0) {
                        // Fling off to the right bring previous view onto screen
                        View vr = mChildViews.get(mCurrent - 1);

                        if (vr != null) {
                            slideViewOntoScreen(vr);
                            return true;
                        }
                    }
                    break;
            }
            mScrollerLastX = mScrollerLastY = 0;
            // If the page has been dragged out of bounds then we want to spring
            // back
            // nicely. fling jumps back into bounds instantly, so we don't want
            // to use
            // fling in that case. On the other hand, we don't want to forgo a
            // fling
            // just because of a slightly off-angle drag taking us out of bounds
            // other
            // than in the direction of the drag, so we test for out of bounds
            // only
            // in the direction of travel.
            //
            // Also don't fling if out of bounds in any direction by more than
            // fling
            // margin
            Rect expandedBounds = new Rect(bounds);
            expandedBounds.inset(-FLING_MARGIN, -FLING_MARGIN);

            if (withinBoundsInDirectionOfTravel(bounds, velocityX, velocityY) && expandedBounds.contains(0, 0)) {
                mScroller.fling(0, 0, (int) velocityX, (int) velocityY, bounds.left, bounds.right, bounds.top, bounds.bottom);
                post(this);
            }
        }

        return true;
    }


    ////////////////////////////////ScaleGestureDetector///////////////////////////////////////
    //ScaleGestureDetector这个类是专门用来检测两个手指在屏幕上做缩放的手势用的，最简单的应用就是用来缩放图片或者缩放网页。

    //缩放比例
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
//        System.out.println(" detector.getFocusX() =="+ detector.getFocusX());
//        System.out.println("mScale =="+mScale);
        float previousScale = mScale;
        float scale_factor = mReflow ? REFLOW_SCALE_FACTOR : 1.0f;
        float min_scale = MIN_SCALE *scale_factor;
        float max_scale = MAX_SCALE * scale_factor;
        mScale = Math.min(Math.max(mScale * detector.getScaleFactor(), min_scale), max_scale);
        //还原View
        if (mReflow) {
            applyToChildren(new ViewMapper() {
                @Override
                void applyToView(View view) {
                    onScaleChild(view, mScale);
                }
            });
        } else {
            float factor = mScale / previousScale;
            View v = mChildViews.get(mCurrent);
            if (v != null) {
                // Work out the focus point relative to the view top left
                int viewFocusX = (int) detector.getFocusX() - (v.getLeft() + mXScroll);
                int viewFocusY = (int) detector.getFocusY() - (v.getTop() + mYScroll);
                // Scroll to maintain the focus point
                mXScroll += viewFocusX - viewFocusX * factor;
                mYScroll += viewFocusY - viewFocusY * factor;
                requestLayout();
            }
        }


        return true;
    }

    // return 才会调用onScale
    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
//        System.out.println("onScaleBegin");
        mScaling = true;
        // Ignore any scroll amounts yet to be accounted for: the
        // screen is not showing the effect of them, so they can
        // only confuse the user
        mXScroll = mYScroll = 0;
        // Avoid jump at end of scaling by disabling scrolling
        mScrollDisabled = true;
        return true;
    }

    //缩放结束
    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
//        System.out.println("onScaleEnd");
        mScaling = false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

       return   mInterceptTouch;
    }

    ////////////////////////////////触摸 ，测量，存放位置////////////////////////////////////////
    @Override
    public boolean onTouchEvent(MotionEvent event) {

      /*  if (mInterceptTouch) {
            return super.onTouchEvent(event);
        }*/
        mScaleGestureDetector.onTouchEvent(event);

        if (!mScaling) {
            mGestureDetector.onTouchEvent(event);
        }


        /***
         * 多点触控
         */
        if ((event.getAction() & event.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
          mUserInteracting = true;
        }

        if ((event.getAction() & event.ACTION_MASK) == MotionEvent.ACTION_UP) {
            mScrollDisabled = false;
            mUserInteracting = false;

            View v = mChildViews.get(mCurrent);
            if (v != null) {
                //缓慢的滑动
                if (mScroller.isFinished()) {


                    // If, at the end of user interaction, there is no
                    // current inertial scroll in operation then animate
                    // the view onto screen if necessary
                   slideViewOntoScreen(v);
                }

                if (mScroller.isFinished()) {
                    // If still there is no inertial scroll in operation
                    // then the layout is stable
                   postSettle(v);
                }
            }
        }
        requestLayout();
        return true;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int n = getChildCount();
        for (int i = 0; i < n; i++)
            measureView(getChildAt(i));
    }


    /**
     * 测量VIEW 大小 ,改变的是
     *
     * @param v
     */
    private void measureView(View v) {
        // See what size the view wants to be
        v.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);

        if (!mReflow) {
            // Work out a scale that will fit it to this view
            float scale = Math.min((float) getWidth() / (float) v.getMeasuredWidth(), (float) getHeight() / (float) v.getMeasuredHeight());
            // Use the fitting values scaled by our current scale factor
            v.measure(MeasureSpec.EXACTLY | (int) (v.getMeasuredWidth() * scale * mScale), MeasureSpec.EXACTLY
                    | (int) (v.getMeasuredHeight() * scale * mScale));
        } else {
            //VIEW的大小
         v.measure(MeasureSpec.EXACTLY | (v.getMeasuredWidth()), MeasureSpec.EXACTLY | (getMeasuredHeight()));
        }
    }

    /**
     * 设置View的位置
     *
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        View cv = mChildViews.get(mCurrent);
        Point cvOffset;

        if (!mResetLayout) {
            // Move to next or previous if current is sufficiently off center
            if (cv != null) {
                cvOffset = subScreenSizeOffset(cv);
                // cv.getRight() may be out of date with the current scale
                // so add left to the measured width for the correct position
                if (cv.getLeft() + cv.getMeasuredWidth() + cvOffset.x + GAP / 2 + mXScroll < getWidth() / 2 && mCurrent + 1 < mAdapter.getCount()) {
                    postUnsettle(cv);
                    // post to invoke test for end of animation
                    // where we must set hq area for the new current view
                    post(this);

                    onMoveOffChild(mCurrent);
                    mCurrent++;
                    onMoveToChild(mCurrent);
                }

                if (cv.getLeft() - cvOffset.x - GAP / 2 + mXScroll >= getWidth() / 2 && mCurrent > 0) {
                    postUnsettle(cv);
                    // post to invoke test for end of animation
                    // where we must set hq area for the new current view
                    post(this);

                    onMoveOffChild(mCurrent);
                    mCurrent--;
                    onMoveToChild(mCurrent);
                }
            }

            // Remove not needed children and hold them for reuse
            int numChildren = mChildViews.size();
            int childIndices[] = new int[numChildren];
            for (int i = 0; i < numChildren; i++)
                childIndices[i] = mChildViews.keyAt(i);

            for (int i = 0; i < numChildren; i++) {
                int ai = childIndices[i];
                if (ai < mCurrent - 1 || ai > mCurrent + 1) {
                    ViewGroup v = mChildViews.get(ai);
                    onNotInUse(v);
                    mViewCache.add(v);
                    removeViewInLayout(v);
                    mChildViews.remove(ai);
                }
            }
        } else {
            mResetLayout = false;
            mXScroll = mYScroll = 0;

            // Remove all children and hold them for reuse
            int numChildren = mChildViews.size();
            for (int i = 0; i < numChildren; i++) {
                View v = mChildViews.valueAt(i);
                onNotInUse(v);
                mViewCache.add((ViewGroup) v);
                removeViewInLayout(v);
            }
            mChildViews.clear();
            // post to ensure generation of hq area
            post(this);
        }

        // Ensure current view is present
        int cvLeft, cvRight, cvTop, cvBottom;
        boolean notPresent = (mChildViews.get(mCurrent) == null);
        cv = getOrCreateChild(mCurrent);
        // When the view is sub-screen-size in either dimension we
        // offset it to center within the screen area, and to keep
        // the views spaced out
        cvOffset = subScreenSizeOffset(cv);
        if (notPresent) {
            // Main item not already present. Just place it top left
            cvLeft = cvOffset.x;
            cvTop = cvOffset.y;
        } else {
            // Main item already present. Adjust by scroll offsets
            cvLeft = cv.getLeft() + mXScroll;
            cvTop = cv.getTop() + mYScroll;
        }
        // Scroll values have been accounted for
        mXScroll = mYScroll = 0;
        cvRight = cvLeft + cv.getMeasuredWidth();
        cvBottom = cvTop + cv.getMeasuredHeight();

        if (!mUserInteracting && mScroller.isFinished()) {
            Point corr = getCorrection(getScrollBounds(cvLeft, cvTop, cvRight, cvBottom));
            cvRight += corr.x;
            cvLeft += corr.x;
            cvTop += corr.y;
            cvBottom += corr.y;
        } else if (cv.getMeasuredHeight() <= getHeight()) {
            // When the current view is as small as the screen in height, clamp
            // it vertically
            Point corr = getCorrection(getScrollBounds(cvLeft, cvTop, cvRight, cvBottom));
            cvTop += corr.y;
            cvBottom += corr.y;
        }

        cv.layout(cvLeft, cvTop, cvRight, cvBottom);

        if (mCurrent > 0) {
            View lv = getOrCreateChild(mCurrent - 1);
            Point leftOffset = subScreenSizeOffset(lv);
            int gap = leftOffset.x + GAP + cvOffset.x;
            lv.layout(cvLeft - lv.getMeasuredWidth() - gap, (cvBottom + cvTop - lv.getMeasuredHeight()) / 2, cvLeft - gap,
                    (cvBottom + cvTop + lv.getMeasuredHeight()) / 2);
        }

        if (mCurrent + 1 < mAdapter.getCount()) {
            View rv = getOrCreateChild(mCurrent + 1);
            Point rightOffset = subScreenSizeOffset(rv);
            int gap = cvOffset.x + GAP + rightOffset.x;
            rv.layout(cvRight + gap, (cvBottom + cvTop - rv.getMeasuredHeight()) / 2, cvRight + rv.getMeasuredWidth() + gap,
                    (cvBottom + cvTop + rv.getMeasuredHeight()) / 2);
        }

        invalidate();
    }


    ////////////////////////////////puiblic  method////////////////////////////////////////


    /***
     * 返回当前View的索引
     *
     * @return 索引值
     */
    public int getDisplayedViewIndex() {
        String msg = "索引值 ==" + mCurrent;
        if (mDeBug) {
            Log.d(TAG, msg);
        }
        return mCurrent;
    }

    /**
     * 设置要显示View的角标
     *
     * @param i
     */
    public void setDisplayedViewIndex(int i) {
        if (0 <= i && i < mAdapter.getCount()) {
            // 离开当前的View
            onMoveOffChild(mCurrent);
            // 重新设置角标
            mCurrent = i;
            // 移动View到新的角标
            onMoveToChild(i);
            // 从新设置layput
            mResetLayout = true;
            //更新UI
            requestLayout();
        }
    }

    /***
     * 移动到下一个View ,翻页效果
     */
    public void moveToNext() {
        View v = mChildViews.get(mCurrent + 1);
        if (v != null)
            slideViewOntoScreen(v);
    }

    /**
     * 移动到上一个 View ，翻页效果
     */
    public void moveToPrevious() {
        View v = mChildViews.get(mCurrent - 1);
        if (v != null)
            slideViewOntoScreen(v);
    }

    /**
     * 应用到View ,不知道干嘛用的  外部实现  抽象函数
     *
     * @param mapper
     */
    public void applyToChildren(ViewMapper mapper) {
        for (int i = 0; i < mChildViews.size(); i++)
            mapper.applyToView(mChildViews.valueAt(i));
    }

    /**
     * 刷新 恢复数据初始值
     *
     * @param reflow
     */
    public void refresh(boolean reflow) {
        mReflow = reflow;
        mScale = 1.0f;
        mXScroll = mYScroll = 0;
        int numChildren = mChildViews.size();
        for (int i = 0; i < numChildren; i++) {
            View v = mChildViews.valueAt(i);
            onNotInUse(v);
            removeViewInLayout(v);
        }
        mChildViews.clear();
        mViewCache.clear();
        requestLayout();
    }


    /**
     * 重新设置View
     */
    public void resetupChildren() {
        if (mDeBug) {
            Log.d(TAG, "mChildViews.size() == +" + mChildViews.size());
        }
        for (int i = 0; i < mChildViews.size(); i++)
            onChildSetup(mChildViews.keyAt(i), mChildViews.valueAt(i));
    }

    /**
     * 合理的 跳转到一下个View ,这个主意是 PDF 页数大小不一样 我们需要设置一个合理的大小
     */
    public void smartMoveForwards() {
        View v = mChildViews.get(mCurrent);
        if (v == null)
            return;

        // The following code works in terms of where the screen is on the
        // views;
        // so for example, if the currentView is at (-100,-100), the visible
        // region would be at (100,100). If the previous page was (2000, 3000)
        // in
        // size, the visible region of the previous page might be (2100 + GAP,
        // 100)
        // (i.e. off the previous page). This is different to the way the rest
        // of
        // the code in this file is written, but it's easier for me to think
        // about.
        // At some point we may refactor this to fit better with the rest of the
        // code.

        // screenWidth/Height are the actual width/height of the screen. e.g.
        // 480/800
        int screenWidth = getWidth();
        int screenHeight = getHeight();
        // We might be mid scroll; we want to calculate where we scroll to based
        // on
        // where this scroll would end, not where we are now (to allow for
        // people
        // bashing 'forwards' very fast.
        int remainingX = mScroller.getFinalX() - mScroller.getCurrX();
        int remainingY = mScroller.getFinalY() - mScroller.getCurrY();
        // right/bottom is in terms of pixels within the scaled document; e.g.
        // 1000
        int top = -(v.getTop() + mYScroll + remainingY);
        int right = screenWidth - (v.getLeft() + mXScroll + remainingX);
        int bottom = screenHeight + top;
        // docWidth/Height are the width/height of the scaled document e.g.
        // 2000x3000
        int docWidth = v.getMeasuredWidth();
        int docHeight = v.getMeasuredHeight();

        int xOffset, yOffset;
        if (bottom >= docHeight) {
            // We are flush with the bottom. Advance to next column.
            if (right + screenWidth > docWidth) {
                // No room for another column - go to next page
                View nv = mChildViews.get(mCurrent + 1);
                if (nv == null) // No page to advance to
                    return;
                int nextTop = -(nv.getTop() + mYScroll + remainingY);
                int nextLeft = -(nv.getLeft() + mXScroll + remainingX);
                int nextDocWidth = nv.getMeasuredWidth();
                int nextDocHeight = nv.getMeasuredHeight();

                // Allow for the next page maybe being shorter than the screen
                // is high
                yOffset = (nextDocHeight < screenHeight ? ((nextDocHeight - screenHeight) >> 1) : 0);

                if (nextDocWidth < screenWidth) {
                    // Next page is too narrow to fill the screen. Scroll to the
                    // top, centred.
                    xOffset = (nextDocWidth - screenWidth) >> 1;
                } else {
                    // Reset X back to the left hand column
                    xOffset = right % screenWidth;
                    // Adjust in case the previous page is less wide
                    if (xOffset + screenWidth > nextDocWidth)
                        xOffset = nextDocWidth - screenWidth;
                }
                xOffset -= nextLeft;
                yOffset -= nextTop;
            } else {
                // Move to top of next column
                xOffset = screenWidth;
                yOffset = screenHeight - bottom;
            }
        } else {
            // Advance by 90% of the screen height downwards (in case lines are
            // partially cut off)
            xOffset = 0;
            yOffset = smartAdvanceAmount(screenHeight, docHeight - bottom);
        }
        mScrollerLastX = mScrollerLastY = 0;
        mScroller.startScroll(0, 0, remainingX - xOffset, remainingY - yOffset, 400);
        post(this);
    }

    /**
     * 返回上一个View
     */
    public void smartMoveBackwards() {
        View v = mChildViews.get(mCurrent);
        if (v == null)
            return;

        // The following code works in terms of where the screen is on the
        // views;
        // so for example, if the currentView is at (-100,-100), the visible
        // region would be at (100,100). If the previous page was (2000, 3000)
        // in
        // size, the visible region of the previous page might be (2100 + GAP,
        // 100)
        // (i.e. off the previous page). This is different to the way the rest
        // of
        // the code in this file is written, but it's easier for me to think
        // about.
        // At some point we may refactor this to fit better with the rest of the
        // code.

        // screenWidth/Height are the actual width/height of the screen. e.g.
        // 480/800
        int screenWidth = getWidth();
        int screenHeight = getHeight();
        // We might be mid scroll; we want to calculate where we scroll to based
        // on
        // where this scroll would end, not where we are now (to allow for
        // people
        // bashing 'forwards' very fast.
        int remainingX = mScroller.getFinalX() - mScroller.getCurrX();
        int remainingY = mScroller.getFinalY() - mScroller.getCurrY();
        // left/top is in terms of pixels within the scaled document; e.g. 1000
        int left = -(v.getLeft() + mXScroll + remainingX);
        int top = -(v.getTop() + mYScroll + remainingY);
        // docWidth/Height are the width/height of the scaled document e.g.
        // 2000x3000
        int docHeight = v.getMeasuredHeight();

        int xOffset, yOffset;
        if (top <= 0) {
            // We are flush with the top. Step back to previous column.
            if (left < screenWidth) {
                /* No room for previous column - go to previous page */
                View pv = mChildViews.get(mCurrent - 1);
                if (pv == null) /* No page to advance to */
                    return;
                int prevDocWidth = pv.getMeasuredWidth();
                int prevDocHeight = pv.getMeasuredHeight();

                // Allow for the next page maybe being shorter than the screen
                // is high
                yOffset = (prevDocHeight < screenHeight ? ((prevDocHeight - screenHeight) >> 1) : 0);

                int prevLeft = -(pv.getLeft() + mXScroll);
                int prevTop = -(pv.getTop() + mYScroll);
                if (prevDocWidth < screenWidth) {
                    // Previous page is too narrow to fill the screen. Scroll to
                    // the bottom, centred.
                    xOffset = (prevDocWidth - screenWidth) >> 1;
                } else {
                    // Reset X back to the right hand column
                    xOffset = (left > 0 ? left % screenWidth : 0);
                    if (xOffset + screenWidth > prevDocWidth)
                        xOffset = prevDocWidth - screenWidth;
                    while (xOffset + screenWidth * 2 < prevDocWidth)
                        xOffset += screenWidth;
                }
                xOffset -= prevLeft;
                yOffset -= prevTop - prevDocHeight + screenHeight;
            } else {
                // Move to bottom of previous column
                xOffset = -screenWidth;
                yOffset = docHeight - screenHeight + top;
            }
        } else {
            // Retreat by 90% of the screen height downwards (in case lines are
            // partially cut off)
            xOffset = 0;
            yOffset = -smartAdvanceAmount(screenHeight, top);
        }
        mScrollerLastX = mScrollerLastY = 0;
        mScroller.startScroll(0, 0, remainingX - xOffset, remainingY - yOffset, 400);
        post(this);
    }


    /**
     * 返回当前前后左右 中的VIEW ； ，目前大小 ==3
     *
     * @param i
     * @return VIEW
     */
    public View getView(int i) {
        return mChildViews.get(i);
    }


    /**
     * @return 返回当前显示的VIEW
     */
    public View getDisplayedView() {
        return mChildViews.get(mCurrent);
    }

    /**
     * 是否拦截事件
     * @param interceptTouch
     */
    public void setInterceptTouch(boolean interceptTouch) {
        this.mInterceptTouch = interceptTouch;
    }

    ////////////////////////////////子类根据自身 需要重写 父亲的 protected method/////////////////////

    /**
     * 移动View到新的角标
     */
    protected void onMoveToChild(int i) {
    }

    /**
     * 离开当前的View
     */
    protected void onMoveOffChild(int i) {
    }

    /**
     * 滑动View 会使用
     */
    protected void onSettle(View v) {
    }

    ;

    /**
     * 重新设置View
     */
    protected void onChildSetup(int i, View v) {
    }


    /**
     * 刷新数据 ， 不知道怎么使用 提View 方便自己操作
     */
    protected void onNotInUse(View v) {
    }


    /**
     * 缩放比例
     */
    protected void onScaleChild(View v, Float scale) {
    }

    /**
     * layout调用了
     *
     * @param v
     */
    protected void onUnsettle(View v) {
    }

    ////////////////////////////////私有 内部使用 method/////////////////////

    /**
     * 滑动 屏幕 view
     *
     * @param v
     */
    private void slideViewOntoScreen(View v) {
        Point corr = getCorrection(getScrollBounds(v));
        if (corr.x != 0 || corr.y != 0) {
            mScrollerLastX = mScrollerLastY = 0;
            mScroller.startScroll(0, 0, corr.x, corr.y, 400);
            post(this);
        }
    }


    /**
     * 获取一个矩形
     *
     * @param v
     * @return
     */
    private Rect getScrollBounds(View v) {
        // There can be scroll amounts not yet accounted for in
        // onLayout, so add mXScroll and mYScroll to the current
        // positions when calculating the bounds.
        return getScrollBounds(v.getLeft() + mXScroll,
                v.getTop() + mYScroll,
                v.getLeft() + v.getMeasuredWidth() + mXScroll,
                v.getTop() + v.getMeasuredHeight()
                        + mYScroll);
    }

    /**
     * 传入坐标获取一个 矩形
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @return
     */
    private Rect getScrollBounds(int left, int top, int right, int bottom) {
        int xmin = getWidth() - right;
        int xmax = -left;
        int ymin = getHeight() - bottom;
        int ymax = -top;

        // In either dimension, if view smaller than screen then
        // constrain it to be central
        if (xmin > xmax)
            xmin = xmax = (xmin + xmax) / 2;
        if (ymin > ymax)
            ymin = ymax = (ymin + ymax) / 2;

        return new Rect(xmin, ymin, xmax, ymax);
    }


    /**
     * 返回点
     *
     * @param bounds
     * @return
     */
    private Point getCorrection(Rect bounds) {
        return new Point(Math.min(Math.max(0, bounds.left), bounds.right), Math.min(Math.max(0, bounds.top), bounds.bottom));
    }


    private void postSettle(final View v) {
        // onSettle and onUnsettle are posted so that the calls
        // wont be executed until after the system has performed
        // layout.

        post(new Runnable() {
            public void run() {
                onSettle(v);
            }
        });
    }

    /**
     * '
     *
     * @param screenHeight
     * @param max
     * @return 预期展示的View
     */

    // When advancing down the page, we want to advance by about
    // 90% of a screenful. But we'd be happy to advance by between
    // 80% and 95% if it means we hit the bottom in a whole number
    // of steps.
    private int smartAdvanceAmount(int screenHeight, int max) {
        int advance = (int) (screenHeight * 0.9 + 0.5);
        int leftOver = max % advance;
        int steps = max / advance;
        if (leftOver == 0) {
            // We'll make it exactly. No adjustment
        } else if ((float) leftOver / steps <= screenHeight * 0.05) {
            // We can adjust up by less than 5% to make it exact.
            advance += (int) ((float) leftOver / steps + 0.5);
        } else {
            int overshoot = advance - leftOver;
            if ((float) overshoot / steps <= screenHeight * 0.1) {
                // We can adjust down by less than 10% to make it exact.
                advance -= (int) ((float) overshoot / steps + 0.5);
            }
        }
        if (advance > max)
            advance = max;
        return advance;
    }


    /***
     * 计算 方向
     *
     * @param vx
     * @param vy
     * @return
     */
    private static int directionOfTravel(float vx, float vy) {
        if (Math.abs(vx) > 2 * Math.abs(vy))
            return (vx > 0) ? MOVING_RIGHT : MOVING_LEFT;
        else if (Math.abs(vy) > 2 * Math.abs(vx))
            return (vy > 0) ? MOVING_DOWN : MOVING_UP;
        else
            return MOVING_DIAGONALLY;
    }

    private static boolean withinBoundsInDirectionOfTravel(Rect bounds, float vx, float vy) {
        switch (directionOfTravel(vx, vy)) {
            case MOVING_DIAGONALLY:
                return bounds.contains(0, 0);
            case MOVING_LEFT:
                return bounds.left <= 0;
            case MOVING_RIGHT:
                return bounds.right >= 0;
            case MOVING_UP:
                return bounds.top <= 0;
            case MOVING_DOWN:
                return bounds.bottom >= 0;
            default:
                throw new NoSuchElementException();
        }
    }

    /***
     * 设置view位置
     *
     * @param v
     * @return 返回一个点
     */
    private Point subScreenSizeOffset(View v) {
        return new Point(Math.max((getWidth() - v.getMeasuredWidth()) / 2, 0), Math.max((getHeight() - v.getMeasuredHeight()) / 2, 0));
    }


    private void postUnsettle(final View v) {
        post(new Runnable() {
            public void run() {
                onUnsettle(v);
            }
        });
    }

    /***
     * 获取 或者创建一个VIEW
     *
     * @param i
     * @return
     */
    private ViewGroup getOrCreateChild(int i) {
        ViewGroup v = mChildViews.get(i);
        if (v == null) {

            v = (ViewGroup) mAdapter.getView(i, getCached(), this);
            addAndMeasureChild(i, v);
            onChildSetup(i, v);
            onScaleChild(v, mScale);
        }

        return v;
    }

    /**
     * 缓存 获取一个VIEW
     */
    private View getCached() {
        if (mViewCache.size() == 0)
            return null;
        else
            return mViewCache.removeFirst();
    }

    /***
     * 添加(mChildViews)并且 测量  这个VIEW
     *
     * @param i
     * @param v
     */
    private void addAndMeasureChild(int i, View v) {
        LayoutParams params = v.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }
        addViewInLayout(v, 0, params, true);
        mChildViews.append(i, (ViewGroup) v); // Record the view against it's adapter index
        measureView(v);
    }

    /**
     * 返回当前在adapter中显示的View 图层
     */
    public ViewGroup getCurrentItem() {

        if (mChildViews != null && mChildViews.size() > 0) {
            ViewGroup v = mChildViews.get(mCurrent);
//            System.out.println("v.getChildCount() == " + v.getChildCount());

            if (v.getChildCount() == 2) {
                return (ViewGroup) v.getChildAt(v.getChildCount()-1);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public int getCurrentPosition(){
        return mCurrent;
    }
}
