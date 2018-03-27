package com.yougy.common.utils;

import android.view.View;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;

/**
 * Created by Administrator on 2018/3/20.
 * 全局刷新
 */

public class RefreshUtil {
    private static boolean flag = true;

    /**全局刷新*/
    public static void invalidate(View view) {
        if (flag) {
            EpdController.invalidate(view, UpdateMode.GC);
        }
    }
}