package com.inkscreen.utils;

import java.util.Calendar;

/**
 * Created by xcz on 2017/1/5.
 */
public class MyTimeUtils {

    /**
     * 接口返回的时间与本地时间配对后展示规则如下： a) 服务器日期与本地日期相同，展示服务器时间：MM-DD HH：MM(月-日 时:分) b)
     * 服务器日期与本地日期相差超过一年展示： YY-MM-DD HH：MM(年-月-日 时:分)
     *
     * @param startTime
     * @return
     */

    public static String getHomeWorkStartTime(long startTime) {
        try {
            // Date nowDate = new Date();
            Calendar current = Calendar.getInstance();
            current.setTimeInMillis(System.currentTimeMillis());
            int nowYear = current.get(Calendar.YEAR);
            // SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd
            // HH:mm:ss");
            current.setTimeInMillis(startTime);
            int startYear = current.get(Calendar.YEAR);
            if (startYear != nowYear) {
                return TimeUtil.formatTimeForMinute(startTime);
            } else {
                return TimeUtil.formatToMD(startTime);
            }
        } catch (Exception e) {
            return TimeUtil.formatTimeForMinute(startTime);
        }
    }

    /**
     * 作业结束时间： 接口返回的时间与本地时间配对后展示规则如下： a) 服务器日期与本地日期相同，展示服务器时间：今天HH：MM(时:分) b)
     * 服务器日期早于本地日期一天，展示：明天HH：MM 超过一天展示：MM-DD HH:MM 超过一年展示： YY-MM-DD HH:MM
     * 服务器晚于本地时间展示： MM-DD HH：MM(月-日 时:分) 超过一年YY-MM-DD HH:MM
     *
     * @param startTime
     * @return
     */

    public static String getHomeWorkDeadLineTime(long deadline) {
        try {
            // SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
            Calendar current = Calendar.getInstance();
            current.setTimeInMillis(System.currentTimeMillis());
            int nowYear = current.get(Calendar.YEAR);
            Calendar dead = Calendar.getInstance();
            dead.setTimeInMillis(deadline);
            int deadlineYear = dead.get(Calendar.YEAR);
            int deadlineMonth = dead.get(Calendar.MONTH);
            int deadlineDay = dead.get(Calendar.DAY_OF_MONTH);
            if (deadlineYear != nowYear) {
                return TimeUtil.formatTimeForMinute(deadline) + " 截止";
            } else if (deadlineMonth != current.get(Calendar.MONTH)) {
                return TimeUtil.formatToMD(deadline) + " 截止";
            } else if (deadlineDay == current.get(Calendar.DAY_OF_MONTH)) {
                return "今天 " + TimeUtil.currentTimeByHour(deadline) + " 截止";
            } else if (deadline - System.currentTimeMillis() < 24 * 60 * 60 * 1000
                    && deadline - System.currentTimeMillis() >= 0) {
                return "明天 " + TimeUtil.currentTimeByHour(deadline) + " 截止";
            } else {
                return TimeUtil.formatToMD(deadline) + " 截止";
            }
        } catch (Exception e) {
            return TimeUtil.formatTimeForMinute(deadline);
        }
    }


}
