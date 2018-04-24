package com.yougy.common.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import com.yougy.common.utils.LogUtils;

/**
 * Created by jiangliang on 2017/4/17.
 */

public class WifiRecevier extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        LogUtils.e("FH" , "wifi状态改变 " + manager.getWifiState());
        if (manager.getWifiState() == 1){
            LogUtils.e("FH" , "检测到wifi关闭,重连!");
            manager.setWifiEnabled(true);
        }
    }
}
