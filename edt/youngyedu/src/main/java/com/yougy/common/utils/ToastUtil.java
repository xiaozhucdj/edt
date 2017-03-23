package com.yougy.common.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

import com.yougy.ui.activity.R;


/**
 * Created by jiangliang on 2016/7/14.
 */
public class ToastUtil {

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    public static void showCustomToast(Context context,int resId){
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.save_img_src);
        Toast toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
        //自定义Toast的显示位置
        toast.setGravity(Gravity.CENTER,0,0);
        //自定义Toast的视图
        toast.setView(imageView);
        toast.show();
    }
}
