package com.inkscreen;

import android.app.Application;

import com.inkscreen.utils.NetworkManager;


/**
 * Created by xcz on 2016/11/24.
 */
public class LekeApplication extends Application {
    private static LekeApplication mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        LeController.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        NetworkManager.getInstance().shutdownHttpClient();
       // LeController.end();
    }

    public static LekeApplication getApp() {
        return mContext;
    }
}
