package com.yougy.common.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yougy.home.bean.NoteInfo;
import com.yougy.init.bean.BookInfo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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

    public static  List<NoteInfo>fromNotes(String json)
    {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<NoteInfo>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static List<BookInfo> fromBooks(String json)
    {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<BookInfo>>() {}.getType();
        return gson.fromJson(json, type);
    }

}
