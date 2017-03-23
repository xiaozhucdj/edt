package com.onyx.android.sdk.ui;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;

/**
 * Created by ming on 16/9/14.
 */
public class DisableScrollGridManager extends GridLayoutManager {
    private boolean canScroll = false;

    public DisableScrollGridManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public DisableScrollGridManager(Context context) {
        super(context, 1);
    }

    public DisableScrollGridManager(Context context, int orientation, boolean reverseLayout) {
        super(context, 1, orientation, reverseLayout);
    }

    public DisableScrollGridManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public void setScrollEnable(boolean enable) {
        this.canScroll = enable;
    }

    @Override
    public boolean canScrollVertically() {
        return canScroll;
    }

    @Override
    public boolean canScrollHorizontally() {
        return canScroll;
    }
}
