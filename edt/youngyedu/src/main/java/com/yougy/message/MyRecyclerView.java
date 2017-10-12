package com.yougy.message;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by FH on 2017/3/29.
 */

public class MyRecyclerView extends RecyclerView {
    OnItemTouchListener mListener;
    public MyRecyclerView(Context context) {
        super(context);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 设置一个OnItemTouchListener,后面set的会覆盖前面的.
     * @param listener
     */
    public void setOnItemTouchListener(OnItemTouchListener listener){
        if (listener != null){
            if (mListener != null){
                removeOnItemTouchListener(mListener);
            }
            mListener = listener;
            super.addOnItemTouchListener(mListener);
        }
    }

    /**
     * 不要使用这个方法添加listener,使用{@link #setOnItemTouchListener(OnItemTouchListener)}代替
     * @param listener
     */
    @Deprecated
    @Override
    public void addOnItemTouchListener(OnItemTouchListener listener) {}
}
