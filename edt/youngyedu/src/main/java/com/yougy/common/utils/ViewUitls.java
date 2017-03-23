package com.yougy.common.utils;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.yougy.common.activity.BaseActivity;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Administrator on 2016/8/30.
 */
public class ViewUitls {

    /**
     * 检测一个Motion事件是否在指定View的区域内
     *
     * @param v  指定的View
     * @param ev Motion事件
     * @return 在区域内返回true，否则返回false
     */
    public static boolean isMotionEventInView(View v, MotionEvent ev) {
        if (v == null || ev == null) {
            return false;
        }
        int[] coord = new int[2];
        v.getLocationOnScreen(coord);
        final int absLeft = coord[0];
        final int absRight = coord[0] + v.getWidth();
        final int absTop = coord[1] - getStatusBarHeight();
        final int absBottom = coord[1] + v.getHeight();
        return (ev.getRawX() >= absLeft && ev.getRawX() < absRight && ev.getRawY() >= absTop && ev.getRawY() < absBottom);
    }

    /**
     * 将一个Motion事件的坐标转换为相对于某个View的坐标
     *
     * @param v        指定的View
     * @param srcEvent Motion事件
     * @param inView   如果Motion事件发生在指定的View内，该引用将被赋为true，反之false
     * @return 返回转换后的结果
     */
    @SuppressLint("Recycle")
    public static MotionEvent getRelativeMotionEventInView(View v, MotionEvent srcEvent, AtomicBoolean inView) {
        if (v == null || srcEvent == null) {
            return null;
        }
        int[] coord = new int[2];
        v.getLocationOnScreen(coord);
        final int absLeft = coord[0];
        final int absRight = coord[0] + v.getWidth();
        final int absTop = coord[1];
        final int absBottom = coord[1] + v.getHeight();
        if (inView != null) {
            inView.set(srcEvent.getRawX() >= absLeft && srcEvent.getRawX() < absRight && srcEvent.getRawY() >= absTop && srcEvent.getRawY() < absBottom);
        }
        MotionEvent relativeEvent = MotionEvent.obtain(srcEvent);


        float offsetX = srcEvent.getRawX() - srcEvent.getX();
        float offsetY = srcEvent.getRawY() - srcEvent.getY();

        relativeEvent.offsetLocation(-(absLeft - offsetX), -(absTop - offsetY));
        return relativeEvent;
    }


    public static int getStatusBarHeight() {
        BaseActivity activity = BaseActivity.getForegroundActivity();
        if (activity != null) {
            Rect frame = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            return frame.top;
        }
        return 0;
    }

    /**
     * 把自身从布局中移除
     */
    public static void removeSelfFromParent(View v) {
        if (v != null && v.getParent() != null && v.getParent() instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) v.getParent();
            group.removeView(v);
        }
    }

    /**
     * 创建一个点击事件
     */
    public static void createClickEvent(View v) {
        int w = v.getMeasuredWidth();
        int h = v.getMeasuredHeight();
        long downTime = System.currentTimeMillis();
        long upTime = downTime + 10;

        MotionEvent down = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, w / 2, h / 2, 0);
        MotionEvent up = MotionEvent.obtain(downTime, upTime, MotionEvent.ACTION_DOWN, w / 2, h / 2, 0);
        v.dispatchTouchEvent(down);
        v.dispatchTouchEvent(up);
    }

    /**
     * 请求View树重新布局，用于解决中层View有布局状态而导致上层View状态断裂
     */
    public static void requestLayoutParent(View view, boolean isAll) {
        ViewParent parent = view.getParent();
        while (parent != null && parent instanceof View) {
            if (!parent.isLayoutRequested()) {
                parent.requestLayout();
                if (!isAll) {
                    break;
                }
            }
            parent = parent.getParent();
        }
    }

    /**
     * 判断触点是否落在该View上
     */
    public static boolean isTouchInView(MotionEvent ev, View v) {
        int[] vLoc = new int[2];
        v.getLocationOnScreen(vLoc);
        float motionX = ev.getRawX();
        float motionY = ev.getRawY();
        return motionX >= vLoc[0] && motionX <= (vLoc[0] + v.getWidth()) && motionY >= vLoc[1] && motionY <= (vLoc[1] + v.getHeight());
    }

    /**
     * FindViewById的泛型封装，减少强转代码
     */
    public static <T extends View> T findViewById(View layout, int id) {
        return (T) layout.findViewById(id);
    }

}
