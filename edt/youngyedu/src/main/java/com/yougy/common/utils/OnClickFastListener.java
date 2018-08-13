package com.yougy.common.utils;

import android.content.DialogInterface;

/**
 * Dialog点击的时候判断屏蔽快速点击事件
 */
public abstract class OnClickFastListener implements DialogInterface.OnClickListener {

    private static long lastClickTime;

    private static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (OnClickFastListener.isFastDoubleClick()) {
            return;
        }

        onFastClick(dialogInterface,i);
    }

    public abstract void onFastClick(DialogInterface dialogInterface, int i);
}
