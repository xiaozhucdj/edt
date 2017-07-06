package com.yougy.common.utils;

import android.text.TextUtils;

/**
 * Created by Administrator on 2016/7/19.
 */
public class StringUtils {
    /** 判断字符串是否有值，如果为null或者是空字符串或者只有空格或者为"null"字符串，则返回true，否则则返回false */
    public static boolean isEmpty(String value) {
        if (value != null && !"".equalsIgnoreCase(value.trim()) && !"null".equalsIgnoreCase(value.trim())) {
            return false;
        } else {
            return true;
        }
    }

    /** 判断多个字符串是否相等，如果其中有一个为空字符串或者null，则返回false，只有全相等才返回true */
    public static boolean isEquals(String... agrs) {
        String last = null;
        for (int i = 0; i < agrs.length; i++) {
            String str = agrs[i];
            if (isEmpty(str)) {
                return false;
            }
            if (last != null && !str.equalsIgnoreCase(last)) {
                return false;
            }
            last = str;
        }
        return true;
    }

    /**
     * 按照给出的最大字符串长度裁剪字符串,如果超出最大长度,则裁剪后添加省略号,否则原样返回
     * @param str 要裁剪的字符串
     * @param maxWords 最大长度
     * @return 裁剪后的字符串
     */
    public static String cutString(String str , int maxWords){
        if (TextUtils.isEmpty(str)){
            return str;
        }
        if (str.length() > maxWords){
            return str.substring(0 , maxWords) + "...";
        }
        else {
            return str;
        }
    }
}
