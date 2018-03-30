package com.yougy.message;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;

/**
 * Created by FH on 2018/3/30.
 */

public class MyEdittext extends android.support.v7.widget.AppCompatEditText{
    public interface SoftInputListener{
        void onBack();
        void onFocusChanged(boolean focused);
    }

    SoftInputListener mSoftInputListener;
    public MyEdittext(Context context) {
        super(context);
    }

    public MyEdittext(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyEdittext(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (mSoftInputListener != null && keyCode == KeyEvent.KEYCODE_BACK){
            mSoftInputListener.onBack();
        }
        return super.onKeyPreIme(keyCode, event);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (mSoftInputListener != null){
            mSoftInputListener.onFocusChanged(focused);
        }
    }

    public void setSoftInputListener(SoftInputListener mSoftInputListener) {
        this.mSoftInputListener = mSoftInputListener;
    }

}
