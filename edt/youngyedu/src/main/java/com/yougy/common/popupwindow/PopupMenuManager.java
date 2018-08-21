package com.yougy.common.popupwindow;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.yougy.ui.activity.R;


public class PopupMenuManager {

    /**
     * 初始化popupmenu菜单
     */
    public static void initPupopWindow(Activity mContext, View view, String mRight, String mError, String mTime, String mPercent) {
        // 显示popupwindow菜单
        View contentView = PopupUtil.showPopupWindowMenu(mContext, view, R.layout.check_homework_menu);


        TextView tvRight = contentView.findViewById(R.id.tv_homework_menu_right);
        TextView tvError = contentView.findViewById(R.id.tv_homework_menu_error);
        TextView tvTime = contentView.findViewById(R.id.tv_homework_menu_time);
        TextView tvPercent = contentView.findViewById(R.id.tv_homework_menu_percent);


        tvRight.setText(mRight);
        tvError.setText(mError);
        tvTime.setText(mTime);
        tvPercent.setText(mPercent);

    }


    /**
     * 关闭popupWindow
     */
    public static void dismiss() {
        PopupUtil.dismiss();
    }

    /**
     * popupWindow是否展示
     */
    public static boolean isShow() {
        return PopupUtil.isShow();
    }

}
