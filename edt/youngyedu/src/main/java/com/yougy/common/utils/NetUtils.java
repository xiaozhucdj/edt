package com.yougy.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Administrator on 2016/10/8.
 */
public class NetUtils {
    /** 获取网络连接管理者ConnectivityManager */
    private static ConnectivityManager getConnManager() {
        Context context = UIUtils.getContext();
        if (context == null) {
            return null;
        }
        Object service = context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != service) {
            return (ConnectivityManager) service;
        }
        return null;
    }

    /** 获取连接的网络信息 */
    private static NetworkInfo getActiveNetInfo() {
        ConnectivityManager mgr = getConnManager();
        return mgr == null ? null : mgr.getActiveNetworkInfo();
    }
    /** 判断是否有网络连接 */
    public static  boolean isNetConnected() {
        NetworkInfo netWorkInfo = getActiveNetInfo();
        return netWorkInfo != null && netWorkInfo.isAvailable();
    }


    /** 判断是否有可用的网络 */
    public boolean isNetAvailable() {
        ConnectivityManager mgr = getConnManager();
        NetworkInfo[] info = mgr.getAllNetworkInfo();
        if (info != null) {
            for (NetworkInfo anInfo : info) {
                if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

}
