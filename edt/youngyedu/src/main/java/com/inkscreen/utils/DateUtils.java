package com.inkscreen.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by xcz on 2016/12/30.
 */
public class DateUtils {

    public static final String TODAY = "今天";
    public static final String YESTERDAY = "昨天";
    public static final String TOMORROW = "明天";
    public static final String BEFORE_YESTERDAY = "前天";
    public static final String AFTER_TOMORROW = "后天";


    public static String getDateDetail(String date){
        Calendar today = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");
        try {
            today.setTime(df.parse(df.format(new Date(System.currentTimeMillis()))));
            today.set(Calendar.HOUR, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            target.setTime(df.parse(date));
            target.set(Calendar.HOUR, 0);
            target.set(Calendar.MINUTE, 0);
            target.set(Calendar.SECOND, 0);
            } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
            }
        long intervalMilli = target.getTimeInMillis() - today.getTimeInMillis();
        int xcts = (int) (intervalMilli / (24 * 60 * 60 * 1000));
        return showDateDetail(xcts,target);
        }


    private static String showDateDetail(int xcts, Calendar target) {
        switch (xcts) {
            case 0:
                return TODAY;
            case 1:
                return TOMORROW;
            case 2:
                return AFTER_TOMORROW;
            case -1:
                return YESTERDAY;
            case -2:
                return BEFORE_YESTERDAY;
            default:
                return null;
        }
    }
}
