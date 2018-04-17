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
 *
 *   建立一自定义的Toast ,适配产品需求 显示1S中的对话框
 */
public class Toaster {

    public static void showDefaultToast(Context context, int text, int duration){
        Toast toast = Toast.makeText(context, text, duration);
        View view = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
        TextView txtMsg = (TextView) view.findViewById(R.id.customToast_innerLayout_txtMessage);
        txtMsg.setText(text);
        toast.setView(view);
        toast.setDuration(duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showDefaultToast(Context context, CharSequence text, int duration){
        Toast toast = Toast.makeText(context, text, duration);
        View view = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
        TextView txtMsg = (TextView) view.findViewById(R.id.customToast_innerLayout_txtMessage);
        txtMsg.setText(text);
        toast.setView(view);
        toast.setDuration(duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @SuppressLint("InflateParams")
    public static void showGravityToast(Context context, CharSequence text, int duration){
        Toast toast = Toast.makeText(context, text, duration);
        View view = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
        TextView txtMsg = (TextView) view.findViewById(R.id.customToast_innerLayout_txtMessage);
        txtMsg.setText(text);
        toast.setView(view);
        toast.setDuration(duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @SuppressLint("InflateParams")
    public static void showGravityToast(Context context, int text, int duration){
        Toast toast = Toast.makeText(context, text, duration);
        View view = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
        TextView txtMsg = (TextView) view.findViewById(R.id.customToast_innerLayout_txtMessage);
        txtMsg.setText(text);
        toast.setView(view);
        toast.setDuration(duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
