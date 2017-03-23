package com.inkscreen.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.yougy.common.manager.YougyApplicationManager;


/**
 * Created by xcz on 2016/11/24.
 */
public class AndroidUtils {
    private SharedPreferences mSharedPreferences;
    private static final String PREFERENCE_NAME = "common";
    private static AndroidUtils instance = new AndroidUtils();

    public static AndroidUtils getInstance() {
        if (instance == null) {
            instance = new AndroidUtils();
        }
        return instance;
    }
    public void putPrefs(String key, String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getPrefs(String key, String defaultVal) {
        return mSharedPreferences.getString(key, defaultVal);
    }

    public AndroidUtils(){
        mSharedPreferences = YougyApplicationManager.getApp().getSharedPreferences(PREFERENCE_NAME,
                Context.MODE_PRIVATE);
    }



    public static boolean isNetworkAvailable(Context context){
        boolean flag = false;
        try{
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null)
                flag = cm.getActiveNetworkInfo().isAvailable();
        }catch (Exception e) {
            flag = false;
        }
        return flag;
//        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo.State wifi  = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
//        if(wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING){
//            return true;
//        }
//
//        return false;
    }


    public static void transparentStatusbar(Window window){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }else{
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }



}
