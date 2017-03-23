package com.inkscreen.will.utils.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * Created by xcz on 2016/11/16.
 */
public class MultiGridView extends GridView{
    public boolean ignoreItemTouchEvent = false;

    public MultiGridView(Context context) {
        super(context);
    }

    public MultiGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MultiGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub

        return ignoreItemTouchEvent ? false : super.dispatchTouchEvent(ev);
//		return false;
    }
}
