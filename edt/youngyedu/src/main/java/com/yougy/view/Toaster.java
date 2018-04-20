package com.yougy.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yougy.ui.activity.R;

/**
 * Created by Administrator on 2016/10/21.
 * <p>
 * 建立一自定义的Toast ,适配产品需求 显示1S中的对话框
 */
public class Toaster {
    private static Toast mToast = null;
    private static View mView;
    private static TextView mText;

    public static void showDefaultToast(Context context, int text, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context, text, duration);
            mView = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
            mText = (TextView) mView.findViewById(R.id.customToast_innerLayout_txtMessage);
            mToast.setView(mView);
        }

        mText.setText(text);
        mToast.setDuration(duration);
        mToast.setGravity(Gravity.TOP, 0, 0);
        mToast.show();
    }

    public static void showDefaultToast(Context context, CharSequence text, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context, text, duration);
            mView = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
            mText = (TextView) mView.findViewById(R.id.customToast_innerLayout_txtMessage);
            mToast.setView(mView);
        }

        mText.setText(text);
        mToast.setDuration(duration);
        mToast.setGravity(Gravity.TOP, 0, 0);
        mToast.show();
    }

    @SuppressLint("InflateParams")
    public static void showGravityToast(Context context, CharSequence text, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context, text, duration);
            mView = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
            mText = (TextView) mView.findViewById(R.id.customToast_innerLayout_txtMessage);
            mToast.setView(mView);
        }
        mText.setText(text);
        mToast.setDuration(duration);
        mToast.setGravity(Gravity.TOP, 0, 0);
        mToast.show();
    }

    @SuppressLint("InflateParams")
    public static void showGravityToast(Context context, int text, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context, text, duration);
            mView = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
            mText = (TextView) mView.findViewById(R.id.customToast_innerLayout_txtMessage);
            mToast.setView(mView);
        }
        mText.setText(text);
        mToast.setDuration(duration);
        mToast.setGravity(Gravity.TOP, 0, 0);
        mToast.show();
    }
}
