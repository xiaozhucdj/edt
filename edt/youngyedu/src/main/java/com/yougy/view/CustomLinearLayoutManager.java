package com.yougy.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

/**
 * 禁止RecycleView的滑动
 */
public class CustomLinearLayoutManager extends LinearLayoutManager {
    private boolean isScrollVerticalEnabled = true;
    private boolean isScrollHorizontalEnabled = true;

    public CustomLinearLayoutManager(Context context) {
        super(context);
    }

    public CustomLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public CustomLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setScrollVerticalEnabled(boolean flag) {
        this.isScrollVerticalEnabled = flag;
    }
    public void setScrollHorizontalEnabled(boolean flag) {
        this.isScrollHorizontalEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollVerticalEnabled && super.canScrollVertically();
    }

    @Override
    public boolean canScrollHorizontally() {
        return isScrollHorizontalEnabled && super.canScrollHorizontally();
    }
}