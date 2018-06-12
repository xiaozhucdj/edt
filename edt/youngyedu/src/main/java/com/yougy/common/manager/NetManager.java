package com.yougy.common.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.yougy.common.activity.BaseActivity;
import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.eventbus.EventBusConstant;
import com.yougy.common.utils.SpUtils;
import com.yougy.init.activity.LoginActivity;
import com.yougy.message.YXClient;

import de.greenrobot.event.EventBus;

import static android.net.wifi.WifiManager.WIFI_STATE_CHANGED_ACTION;

/**
 * Created by Administrator on 2017/4/26.
 */

public class NetManager {
    private static NetManager sInstance;
    private final NetReceiver mNetReceiver;

    private NetManager() {
        mNetReceiver = new NetReceiver();
    }

    public static synchronized NetManager getInstance() {
        if (sInstance == null) {
            sInstance = new NetManager();
        }
        return sInstance;
    }

    /**
     * 判断网络是否连接
     *
     * @param context
     * @return
     */
    public boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.getType() == 1) {
                return networkInfo.isAvailable();
            }
        }

        return false;
    }

    /***
     * 关闭和打开WIFI
     * @param context
     * @param enabled
     */
    public void changeWiFi(Context context, boolean enabled) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(enabled);
    }

    /**
     * 这里得到信号强度就靠wifiinfo.getRssi()；这个方法。得到的值是一个0到-100的区间值，是一个int型数据，其中0到-50表示信号最好，-50到-70表示信号偏差，小于-70表示最差，有可能连接不上或者掉线，一般Wifi已断则值为-200。
     * 获取当前连接的网络信息
     *
     * @param context
     * @return
     */
    public int getConnectionInfoRssi(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getConnectionInfo().getRssi();
    }

    /**
     * 注册广播O(∩_∩)O~~
     *
     * @param context
     */
    public void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(mNetReceiver, filter);
    }

    /**
     * 反注册广播
     */
    public void unregisterReceiver(Context context) {
        context.unregisterReceiver(mNetReceiver);
    }

    private final class NetReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION) || action.equalsIgnoreCase(WIFI_STATE_CHANGED_ACTION)) {
                boolean isConnected = NetManager.getInstance().isWifiConnected(context);
                if (isConnected && SpUtils.getUserId() > 0 ) {
                    YXClient.checkNetAndRefreshLogin(null, null);
                }
                NetManager.getInstance().changeWiFi(context, true);
                BaseEvent baseEvent = new BaseEvent(EventBusConstant.EVENT_WIIF, "");
                EventBus.getDefault().post(baseEvent);
            }
        }
    }
}
