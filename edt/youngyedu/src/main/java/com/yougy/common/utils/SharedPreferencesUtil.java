package com.yougy.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yougy.common.manager.YoungyApplicationManager;

import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesUtil {

    private SharedPreferences sp;

    private static SharedPreferencesUtil spUtil;
    private static String studentId;

    private SharedPreferencesUtil(Context context) {
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SharedPreferencesUtil getSpUtil() {
        studentId = SpUtils.getAccountId() + "";
        if (spUtil == null) {
            spUtil = new SharedPreferencesUtil(YoungyApplicationManager.getInstance());
        }
        return spUtil;
    }

    public void remove(String key) {
        if (sp != null) {
            sp.edit().remove(studentId + "-" + key).commit();
        }
    }

    public void putBoolean(String key, boolean value) {
        if (sp != null) {
            sp.edit().putBoolean(studentId + "-" + key, value).commit();
        }
    }

    public void putFloat(String key, float value) {
        if (sp != null) {
            sp.edit().putFloat(studentId + "-" + key, value).commit();
        }
    }

    public void putInt(String key, int value) {
        if (sp != null) {
            sp.edit().putInt(studentId + "-" + key, value).commit();
        }
    }

    public void putLong(String key, long value) {
        if (sp != null) {
            sp.edit().putLong(studentId + "-" + key, value).commit();
        }
    }

    public void putString(String key, String value) {
        if (sp != null) {
            sp.edit().putString(studentId + "-" + key, value).commit();

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
        sp.edit().putString(studentId + "-" + tag, strJson).commit();

    }

    /**
     * 获取List
     *
     * @param tag
     * @return
     */
    public <T> List<T> getDataList(String tag) {
        List<T> datalist = new ArrayList<T>();
        String strJson = sp.getString(studentId + "-" + tag, null);
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
            value = sp.getString(studentId + "-" + key, defValue);
        }
        return value;
    }

    public boolean getBoolean(String key, boolean defValue) {
        boolean value = false;
        if (sp != null) {
            value = sp.getBoolean(studentId + "-" + key, defValue);
        }
        return value;
    }

    public int getInt(String key, int defValue) {
        int value = 0;
        if (sp != null) {
            value = sp.getInt(studentId + "-" + key, defValue);
        }
        return value;
    }

    public long getLong(String key, long defValue) {
        long value = 0;
        if (sp != null) {
            value = sp.getLong(studentId + "-" + key, defValue);
        }
        return value;
    }

    public void clearAll() {
        if (sp != null) {
            sp.edit().clear().commit();
        }
    }
}