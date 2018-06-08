package com.yougy.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Administrator on 2016/7/11.
 * <p>
 * 对话框
 */
public abstract class BaseDialog extends Dialog {

    public static float DEFAULT_SCALE = 0.9f;
//    private OnDismissListener mOutListener;
//    private OnDismissListener mInnerListener = new InnerDismissListener();

    public BaseDialog(Context context) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        init();
        Window window = getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//        super.setOnDismissListener(mInnerListener);
    }

    public BaseDialog(Context context, int themeResId) {
        super(context, themeResId);
        init();
        Window window = getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//        super.setOnDismissListener(mInnerListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initLayout();
    }

    protected float getWidthScale() {
        return DEFAULT_SCALE;
    }

    protected float getHeightScale() {
        return DEFAULT_SCALE;
    }

    protected abstract void init();

    protected abstract void initLayout();

    @Override
    public void show() {
        super.show();
        Window window = getWindow();
        Display display = window.getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        window.setLayout(width, height);
    }

//    @Override
//    public void setOnDismissListener(OnDismissListener listener) {
//        mOutListener = listener;
//    }

//    class InnerDismissListener implements OnDismissListener{
//        @Override
//        public void onDismiss(DialogInterface dialog) {
//            if (mOutListener != null) {
//                mOutListener.onDismiss(dialog);
//                Log.e("InnerDismiss","onDismiss...............");
//            }
//        }
//    }


    public BaseDialog setOnDismissListener_return(@Nullable OnDismissListener listener) {
        setOnDismissListener(listener);
        return this;
    }
}
