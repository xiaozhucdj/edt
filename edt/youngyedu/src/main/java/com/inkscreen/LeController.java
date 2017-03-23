package com.inkscreen;

import android.content.Context;

import com.inkscreen.utils.network.RequestManager;


/**
 * Created by xcz on 2016/11/24.
 */
public class LeController {

    private static boolean isAgreedPrompt;
    public static Context appContext;
    public static boolean isAgreedPrompt(){
        return isAgreedPrompt;
    }


    public static void init(Context context){
        appContext = context;
        isAgreedPrompt = false;
        RequestManager.getInstance().init(context);

    }
}
