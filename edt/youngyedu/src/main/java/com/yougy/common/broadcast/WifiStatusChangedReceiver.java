package com.yougy.common.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Created by jiangliang on 2016/10/12.
 */

public class WifiStatusChangedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (!manager.isWifiEnabled()){
            Log.e("WIFI","onReceiver.........");
            manager.setWifiEnabled(true);
        }
    }
}
