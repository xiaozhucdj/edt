package com.yougy.common.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@SuppressLint("SimpleDateFormat")
public class DateUtils {

    public static String getCalendarString() {
        Date d = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(d);
    }

    public static String getCalendarString(long time) {
        Date d = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(d);
    }

    public static String getYesterdayCalendarString() {
        Date d = new Date(System.currentTimeMillis() - 86400000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(d);
    }

    public static String getTimeString() {
        Date d = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(d);
    }

    public static String getTimeHHMMString() {
        Date d = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(d);
    }

    public static String getTimeString(long time) {
        Date d = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(d);
    }

    public static String getCalendarAndTimeString() {
        Date d = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(d);
    }

    public static String getCalendarAndTimeString(long time) {
        Date d = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(d);
    }

    public static String converLongTimeToString(long time) {
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;

        long hour = (time) / hh;
        long minute = (time - hour * hh) / mi;
        long second = (time - hour * hh - minute * mi) / ss;

        String strHour = hour < 10 ? "0" + hour : "" + hour;
        String strMinute = minute < 10 ? "0" + minute : "" + minute;
        String strSecond = second < 10 ? "0" + second : "" + second;
        if (hour > 0) {
            return strHour + ":" + strMinute + ":" + strSecond;
        } else {
            return "00:" + strMinute + ":" + strSecond;
        }
    }

    /**
     * @param time 待转换时间
     * @return 转换后的时间单位：秒
     */
    public static long transformToTime(String time) {
        long allSeconds = 0;
        if (!TextUtils.isEmpty(time) && time.contains(":")) {
            String temp[] = time.split(":");
            int hours = Integer.valueOf(temp[0]);
            int minutes = Integer.valueOf(temp[1]);
            long seconds = Long.valueOf(temp[2]);
            allSeconds = hours * 60 * 60 + minutes * 60 + seconds;
        }
        return allSeconds;
    }


    /**
     * 字符串转换 date
     */
    @SuppressLint("SimpleDateFormat")
    public static Date getStringDateTime(String strDate) {
        String farmatPattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern(farmatPattern);
        try {
            return sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 字符串转换 date
     */
    @SuppressLint("SimpleDateFormat")
    public static Date getStringDate(String strDate) {
        String farmatPattern = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern(farmatPattern);
        try {
            return sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取字符串的年月日
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDateYTD(String strDate) {
        Date date = getStringDateTime(strDate);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }

    /**
     * 获取字符串的时间
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDateTime(String strDate) {
        // AM是指夜里12时到中午12时（用24小时制表示是指0：00-12：00），
        // PM是指中午12时到夜里12时（用24小时制表示是指12：00-24：00）

        Date date = getStringDateTime(strDate);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String time = formatter.format(date);
        String[] times = time.split(":");
        if (times.length == 3) {
            int hour = Integer.valueOf(times[0]);
            if (hour >= 0 && hour < 12) {
                if (hour == 0) {
                    return 12 + ":" + times[1] + ":" + times[2] + " " + "a.m.";
                } else {
                    return hour + ":" + times[1] + ":" + times[2] + " " + "a.m.";
                }
            }
            if (hour >= 12 && hour <= 23) {
                if (hour == 12) {
                    return hour + ":" + times[1] + ":" + times[2] + " " + "p.m.";
                } else {
                    return hour - 12 + ":" + times[1] + ":" + times[2] + " " + "p.m.";
                }
            }
        }
        return null;
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     * @throws ParseException
     */

    public static int daysBetween(Date smdate, Date bdate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            smdate = sdf.parse(sdf.format(smdate));
            bdate = sdf.parse(sdf.format(bdate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        // System.out.println("Integer.parseInt(String.valueOf(between_days))=="+Integer.parseInt(String.valueOf(between_days)));
        return Integer.parseInt(String.valueOf(between_days));
    }

    /***
     * 返回 周一到周日的英文缩写
     *
     * @param repeatMode
     * @return
     */
    public static String getWeek(String repeatMode) {
        if (TextUtils.isEmpty(repeatMode)) {
            return "repeatMode没有没结果";
        }
        String MODO_WITHOUT_REPETITION = "0";
        String MODO_EVERYDAY_REPETITION = "1234567";

        if (repeatMode.equals(MODO_WITHOUT_REPETITION)) {
            return "without repetition";
        }

        if (repeatMode.equals(MODO_EVERYDAY_REPETITION)) {
            return "everyday";
        }

        StringBuffer buffer = new StringBuffer();

        char ch[] = repeatMode.toCharArray();
        for (char week : ch) {
            switch (week) {
                case '1':
                    buffer.append("MON ");
                    break;
                case '2':
                    buffer.append("TUE ");
                    break;
                case '3':
                    buffer.append("WED ");
                    break;
                case '4':
                    buffer.append("THU ");
                    break;
                case '5':
                    buffer.append("FRI ");
                    break;
                case '6':
                    buffer.append("SAT ");
                    break;
                case '7':
                    buffer.append("SUN ");
                    break;
                default:
                    break;
            }
        }
        return buffer.toString();

    }

    public static String getCurrentTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        return createGmtOffsetString(true, true, tz.getRawOffset());
    }

    public static String getCurrentTimeZoneCity() {
        TimeZone tz = TimeZone.getDefault();
        String city = tz.getID();
        return city;
    }

    private static String createGmtOffsetString(boolean includeGmt, boolean includeMinuteSeparator, int offsetMillis) {
        int offsetMinutes = offsetMillis / 60000;
        char sign = '+';
        if (offsetMinutes < 0) {
            sign = '-';
            offsetMinutes = -offsetMinutes;
        }
        StringBuilder builder = new StringBuilder(9);
        if (includeGmt) {
            builder.append("GMT");
        }
        builder.append(sign);
        appendNumber(builder, 2, offsetMinutes / 60);
        if (includeMinuteSeparator) {
            builder.append(':');
        }
        appendNumber(builder, 2, offsetMinutes % 60);
        return builder.toString();
    }

    private static void appendNumber(StringBuilder builder, int count, int value) {
        String string = Integer.toString(value);
        for (int i = 0; i < count - string.length(); i++) {
            builder.append('0');
        }
        builder.append(string);
    }

    /***
     * 根据日期 字符串 返回英文的显示的日期
     *
     * @param sourceStr
     * @return
     */
    public static String getNewDate(String sourceStr) {

        String newDate = " ";
        String[] sourceStrArray = sourceStr.split("-");

        if (sourceStrArray.length == 3) {
            String newMonth = "";
            String year = sourceStrArray[0];
            String month = sourceStrArray[1];
            String day = sourceStrArray[2];

            switch (Integer.parseInt(month)) {
                case 1:
                    newMonth = "Jan";
                    break;

                case 2:
                    newMonth = "Feb";
                    break;
                case 3:
                    newMonth = "Mar";
                    break;
                case 4:
                    newMonth = "Apr";
                    break;
                case 5:
                    newMonth = "May";
                    break;
                case 6:
                    newMonth = "Jun";
                    break;
                case 7:
                    newMonth = "Jul";
                    break;
                case 8:
                    newMonth = "Aug";
                    break;
                case 9:
                    newMonth = "Sep";
                    break;

                case 10:
                    newMonth = "Oct";
                    break;
                case 11:
                    newMonth = "Nov";
                    break;

                case 12:
                    newMonth = "Dec";
                    break;
                default:
                    break;
            }
            newDate = newMonth + " " + day + "," + " " + year;
        } else {
            newDate = sourceStr;
        }
        return newDate;
    }

    /***
     * 根据日期 字符串 返回英文的显示的日期
     *
     * @param sourceStr
     * @return
     */
    public static String getNewDateWithoutYear(String sourceStr) {

        String newDate = " ";
        String[] sourceStrArray = sourceStr.split("-");

        if (sourceStrArray.length == 3) {
            String newMonth = "";
            String month = sourceStrArray[1];
            String day = sourceStrArray[2];

            switch (Integer.parseInt(month)) {
                case 1:
                    newMonth = "Jan";
                    break;

                case 2:
                    newMonth = "Feb";
                    break;
                case 3:
                    newMonth = "Mar";
                    break;
                case 4:
                    newMonth = "Apr";
                    break;
                case 5:
                    newMonth = "May";
                    break;
                case 6:
                    newMonth = "Jun";
                    break;
                case 7:
                    newMonth = "Jul";
                    break;
                case 8:
                    newMonth = "Aug";
                    break;
                case 9:
                    newMonth = "Sep";
                    break;

                case 10:
                    newMonth = "Oct";
                    break;
                case 11:
                    newMonth = "Nov";
                    break;

                case 12:
                    newMonth = "Dec";
                    break;
                default:
                    break;
            }
            newDate = newMonth + " " + day;
        } else {
            newDate = sourceStr;
        }
        return newDate;
    }

    /**
     * 24小时制的毫秒值转换时分秒 格式 00:00:00
     *
     * @param l 毫秒值
     * @return
     */
    public static String formatLongToTimeStr(Long l) {
        int hour = 0;
        int minute = 0;
        int second = 0;

        second = l.intValue() / 1000;

        if (second > 60) {
            minute = second / 60;
            second = second % 60;
        }
        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        return (getTwoLength(hour) + ":" + getTwoLength(minute) + ":" + getTwoLength(second));
    }

    // public static long formatTimeStrToLong(String timeStr){
    // int hour = 0;
    // int minute = 0;
    // int second = 0;
    //
    // try {
    // String[] split = timeStr.split(":");
    // hour = Integer.parseInt(split[0]);
    // minute = Integer.parseInt(split[1]);
    // second = Integer.parseInt(split[2]);
    // } catch (NumberFormatException e) {
    // e.printStackTrace();
    // return -1;
    // }
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    // }

    private static String getTwoLength(final int data) {
        if (data < 10) {
            return "0" + data;
        } else {
            return "" + data;
        }
    }

    public static String getCurrentTimeSimpleYearMonthDayString() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year + "_" + month + "_" + day;
    }


    /**
     * 根据现在的时间,把给定的时间戳转换成相对于现在时间的描述字符串
     * 规则:
     * 1.不是今年的时间一律显示成年-月-日[3个空格]时:分.
     * 2.如果给定时间是今年的,而且是今天或者昨天,则显示为"今天"或者"昨天"
     * 3.如果给定的时间是本周的,但是不是今天或昨天,则一律显示为周X[3个空格]时:分
     * 4.如果是今年的且不是本周的时间,显示为月-日[3个空格]时:分
     *
     * @param timeMillis 要转换的时间戳
     * @param simplified 是否简化显示,如果为true,则只显示日期,如果为false,则显示为"日期[3个空格]时:分"
     * @return 转换好的字符串
     */
    public static String convertTimeMillis2StrRelativeNow(long timeMillis, boolean simplified) {
        Calendar now = Calendar.getInstance();
        Date date = new Date(timeMillis);
        Calendar target = Calendar.getInstance();
        target.setTime(date);
        if (now.get(Calendar.YEAR) == target.get(Calendar.YEAR)) {
            int nowDayOfYear = now.get(Calendar.DAY_OF_YEAR);
            int targetDayOfYear = target.get(Calendar.DAY_OF_YEAR);
            if (nowDayOfYear == targetDayOfYear) {
                if (simplified) {
                    return "今天";
                } else {
                    SimpleDateFormat format = new SimpleDateFormat("今天   HH:mm");
                    return format.format(date);
                }
            }
            if (nowDayOfYear == targetDayOfYear + 1) {
                if (simplified) {
                    return "昨天";
                } else {
                    SimpleDateFormat format = new SimpleDateFormat("昨天   HH:mm");
                    return format.format(date);
                }
            }
            if (now.get(Calendar.WEEK_OF_YEAR) == target.get(Calendar.WEEK_OF_YEAR)) {
                String tempStr = "未知周几";
                switch (target.get(Calendar.DAY_OF_WEEK)) {
                    case Calendar.MONDAY:
                        tempStr = "周一";
                        break;
                    case Calendar.TUESDAY:
                        tempStr = "周二";
                        break;
                    case Calendar.WEDNESDAY:
                        tempStr = "周三";
                        break;
                    case Calendar.THURSDAY:
                        tempStr = "周四";
                        break;
                    case Calendar.FRIDAY:
                        tempStr = "周五";
                        break;
                    case Calendar.SATURDAY:
                        tempStr = "周六";
                        break;
                    case Calendar.SUNDAY:
                        tempStr = "周日";
                        break;
                }
                if (simplified) {
                    return tempStr;
                } else {
                    SimpleDateFormat format = new SimpleDateFormat(tempStr + "   HH:mm");
                    return format.format(date);
                }
            }
            if (simplified) {
                SimpleDateFormat format = new SimpleDateFormat("MM-dd");
                return format.format(date);
            } else {
                SimpleDateFormat format = new SimpleDateFormat("MM-dd   HH:mm");
                return format.format(date);
            }
        }
        if (simplified) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            return format.format(date);
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd   HH:mm");
            return format.format(date);
        }
    }

    /**
     * 转换时间戳至指定格式的时间字符串
     *
     * @param timeMillis 毫秒为单位的时间戳
     * @param pattern    输出的时间字符串的格式,如果为null,则按照yyyy-MM-dd HH:mm:ss的格式输出.
     * @return 输出的时间字符串
     */
    public static String convertTimeMillisToStr(long timeMillis, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern == null ? "yyyy-MM-dd HH:mm:ss" : pattern);
        return format.format(new Date(timeMillis));
    }

    /**
     * 转换时间字符串为时间戳
     *
     * @param timeStr 时间字符串,如2012-01-01 09:00:00
     * @param pattern 时间字符串的格式,需要与时间字符串的格式完全一致,如yyyy-MM-dd HH:mm:ss
     * @return 时间戳
     */
    public static long convertTimeStrToTimeStamp(String timeStr, String pattern) {
        if (TextUtils.isEmpty(timeStr) || TextUtils.isEmpty(pattern)) {
            return -1;
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            return format.parse(timeStr).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }


}
