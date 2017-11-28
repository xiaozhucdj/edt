package com.yougy.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yougy.common.manager.YougyApplicationManager;

import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesUtil {

    private SharedPreferences sp;

    private static SharedPreferencesUtil spUtil;

    private SharedPreferencesUtil(Context context) {
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SharedPreferencesUtil getSpUtil() {
        if (spUtil == null) {
            spUtil = new SharedPreferencesUtil(YougyApplicationManager.getInstance());
        }
        return spUtil;
    }

    public void remove(String key) {
        if (sp != null) {
            sp.edit().remove(key).commit();
        }
    }

    public void putBoolean(String key, boolean value) {
        if (sp != null) {
            sp.edit().putBoolean(key, value).commit();
        }
    }

    public void putFloat(String key, float value) {
        if (sp != null) {
            sp.edit().putFloat(key, value).commit();
        }
    }

    public void putInt(String key, int value) {
        if (sp != null) {
            sp.edit().putInt(key, value).commit();
        }
    }

    public void putLong(String key, long value) {
        if (sp != null) {
            sp.edit().putLong(key, value).commit();
        }
    }

    public void putString(String key, String value) {
        if (sp != null) {
            sp.edit().putString(key, value).commit();

        }
    }

    /**
     * 保存List
     *
     * @param tag
     * @param datalist
     */
    public <T> void setDataList(String tag, List<T> datalist) {
        if (null == datalist || datalist.size() <= 0)
            return;

        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist);
        sp.edit().clear();
        sp.edit().putString(tag, strJson);
        sp.edit().commit();

    }

    /**
     * 获取List
     *
     * @param tag
     * @return
     */
    public <T> List<T> getDataList(String tag) {
        List<T> datalist = new ArrayList<T>();
        String strJson = sp.getString(tag, null);
        if (null == strJson) {
            return datalist;
        }
        Gson gson = new Gson();
        datalist = gson.fromJson(strJson, new TypeToken<List<T>>() {
        }.getType());
        return datalist;

    }


    public String getString(String key, String defValue) {
        String value = null;
        if (sp != null) {
            value = sp.getString(key, defValue);
        }
        return value;
    }

    public boolean getBoolean(String key, boolean defValue) {
        boolean value = false;
        if (sp != null) {
            value = sp.getBoolean(key, defValue);
        }
        return value;
    }

    public int getInt(String key, int defValue) {
        int value = 0;
        if (sp != null) {
            value = sp.getInt(key, defValue);
        }
        return value;
    }

    public long getLong(String key, long defValue) {
        long value = 0;
        if (sp != null) {
            value = sp.getLong(key, defValue);
        }
        return value;
    }

    public void clearAll() {
        if (sp != null) {
            sp.edit().clear().commit();
        }
    }
}