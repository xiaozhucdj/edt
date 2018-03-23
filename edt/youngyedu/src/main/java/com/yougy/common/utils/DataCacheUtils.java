package com.yougy.common.utils;

import android.content.Context;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/31.
 */

public class DataCacheUtils {


    private static ACache aCache;
    private static String studentId;

    private static ACache init(Context context) {
        studentId = SpUtils.getAccountId()+"";
        if (aCache != null) {
            return aCache;
        }
        aCache = ACache.get(context);
        return aCache;
    }


    public static String getString(Context context, String key) {


        return init(context).getAsString(studentId +"-"+key);
    }

    public static void putString(Context context, String key, String value) {
        init(context).put(studentId +"-"+key, value);
    }


    public static void reomve(Context context, String key){
        init(context).remove(studentId +"-"+key);
    }


    public static Object getObject(Context context, String key) {
        return init(context).getAsObject(studentId +"-"+key);
    }

    public static void putObject(Context context, String key, Serializable value) {
        init(context).put(studentId +"-"+key,value);
    }


}
