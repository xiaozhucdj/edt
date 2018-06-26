package com.yougy.common.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/9/26.
 */
public class FormatUtils {

    private final static long MINUTE_MILLISECOND = 1000 * 60;
    private final static long HOUR_MILLISECOND = 1000 * 60 * 60;
    private final static long DAY_MILLISECOND = 1000 * 60 * 60 * 24;

    @SuppressLint("SimpleDateFormat")
    private final static SimpleDateFormat mDatePattern = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @SuppressLint("SimpleDateFormat")
    private final static SimpleDateFormat mDatePattern2 = new SimpleDateFormat("yyyy-MM-dd");

    //==========================================================================
    // 方法
    //==========================================================================

    /**
     * 获取格式化日期和时间
     *
     * @param datePattern 格式化字符串，例如"yyyy-MM-dd HH:mm:ss"
     * @return 格式化的日期时间
     */
    @SuppressLint("SimpleDateFormat")
    public static String formatDate(String datePattern, long time) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(datePattern);
            return format.format(time);
        } catch (Exception e) {
//            LogUtils.e(e);
        }
        return "";
    }

    /**
     * 如果日期格式为yyyy-MM-dd HH:mm:ss，将其拆分为date和time
     *
     * @return String[0]为date，String[1]为time
     */
    public static String[] splitDataAndTime(String dateStr) {
        String[] array = null;
        if (!StringUtils.isEmpty(dateStr)) {
            array = dateStr.split(" ");
        }
        return array;
    }

    /**
     * 将字符串转位日期类型，字符串需要满足"yyyy-MM-dd HH:mm:ss"这样的格式
     *
     * @param dateStr 日期字符串
     */
    public static Date string2Date(String dateStr) {
        Date date = null;
        if (!StringUtils.isEmpty(dateStr)) {
            try {
                date = mDatePattern.parse(dateStr);
            } catch (Exception e) {
//                LogUtils.e(e);
            }
        }
        return date;
    }


    /**
     * 判断给定字符串时间是否为今日
     *
     * @return boolean
     */
    public static boolean isToday(String dateStr) {
        boolean b = false;
        Date time = string2Date(dateStr);
        Date today = new Date();
        if (time != null) {
            String nowDate = mDatePattern2.format(today);
            String timeDate = mDatePattern2.format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }
        return b;
    }

    public static String formatTime (long time) {
        if (time < 0) {
            return "";
        }
        int second = (int) (time / 60);
        int minute = 0, hour = 0, day = 0;
        if (second > 59) {
            minute = second / 60;
            second = second % 60;
            if (minute > 59) {
                hour = minute / 60;
                minute = minute % 60;
                if (hour > 23) {
                    day = hour / 24;
                    hour = hour % 24;
                }
            }
        }
        String d = day > 0 ? (day + "天"): "";
        String h = hour > 0 ? (hour < 10 ? "0" + hour : String.valueOf(hour)) : "";
        String m = minute > 0 ? (minute < 10 ? "0" + minute : String.valueOf(minute)) : "";
        String s = second > 0 ? (second < 10 ? "0" + second : String.valueOf(second)) : "";
        return d + h + m + s;
    }

}

