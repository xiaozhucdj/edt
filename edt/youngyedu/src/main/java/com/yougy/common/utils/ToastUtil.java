package com.yougy.common.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yougy.ui.activity.R;


/**
 * Created by jiangliang on 2016/7/14.
 */
public class ToastUtil {
    private static Toast mToast = null;
    private static View mView;
    private static TextView mText;

    public static void showCustomToast(Context context, CharSequence message) {
        if (mToast == null) {
            mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            mView = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
            mText = (TextView) mView.findViewById(R.id.customToast_innerLayout_txtMessage);
            mToast.setView(mView);
        }

        mText.setText(message);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.TOP, 0, 0);
        mToast.show();
    }


    public static void showCustomToast(Context context, int resId){
        if (mToast == null) {
            mToast = Toast.makeText(context,  UIUtils.getResources().getText(resId), Toast.LENGTH_SHORT);
            mView = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
            mText = (TextView) mView.findViewById(R.id.customToast_innerLayout_txtMessage);
            mToast.setView(mView);
        }

        mText.setText( UIUtils.getResources().getText(resId));
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.TOP, 0, 0);
        mToast.show();
    }
}
