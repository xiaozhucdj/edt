package com.yougy.common.utils;

import com.google.gson.Gson;

/**
 * Created by jiangliang on 2016/11/15.
 */

public class GsonUtil {

    public static <T> T fromJson(String json, Class<T> classOfT) {
        LogUtils.e(classOfT.getName(),"json : " + json);
        return new Gson().fromJson(json, classOfT);
    }

    public static String toJson(Object obj){
        return new Gson().toJson(obj);
    }

}
