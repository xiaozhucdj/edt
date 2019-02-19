package com.yougy.common.utils;

import android.util.Log;

/**
 * Created by Administrator on 2016/11/15.
 */

public class LogUtils {
    //==========================================================================
    // 静态常亮
    //==========================================================================
    /**
     * 日志输出级别NONE
     */
    public static final int LEVEL_NONE = 0;
    /**
     * 日志输出级别V
     */
    public static final int LEVEL_VERBOSE = 1;
    /**
     * 日志输出级别D
     */
    public static final int LEVEL_DEBUG = 2;
    /**
     * 日志输出级别I
     */
    public static final int LEVEL_INFO = 3;
    /**
     * 日志输出级别W
     */
    public static final int LEVEL_WARN = 4;
    /**
     * 日志输出级别E
     */
    public static final int LEVEL_ERROR = 5;

    private static  boolean DEBUG = true;
    //==========================================================================
    // 全局变量
    //==========================================================================
    /**
     * 是否允许输出log
     */
    private static int mDebuggable = LEVEL_ERROR;
    /**
     * 日志输出时的TAG
     */
    private static String mTag = "leke";
    //==========================================================================
    // get、set方法
    //==========================================================================

    /**
     * 设置日志输出级别
     */
    public static void setDebuggable(int debug) {
        mDebuggable = debug;
    }

    //==========================================================================
    // 方法
    //==========================================================================


    public static void setOpenLog(boolean openLog){
        DEBUG = openLog ;
    }

    /**
     * 以级别为 v 的形式输出LOG
     *
     * @param msg 需要输出的msg
     */
    public static void v(String msg) {
        if (DEBUG && mDebuggable >= LEVEL_VERBOSE) {
            Log.v(mTag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (DEBUG && mDebuggable >= LEVEL_VERBOSE) {
            Log.v(tag, msg);
        }
    }


    /**
     * 以级别为 d 的形式输出LOG
     *
     * @param msg 需要输出的msg
     */
    public static void d(String msg) {
        if (DEBUG && mDebuggable >= LEVEL_DEBUG) {
            Log.d(mTag, msg);
        }
    }

    public static void d(String tag , String msg) {
        if (DEBUG && mDebuggable >= LEVEL_DEBUG) {
            Log.d(tag, msg);
        }
    }

    /**
     * 以级别为 i 的形式输出LOG
     *
     * @param msg 需要输出的msg
     */
    public static void i(String msg) {
        if (DEBUG && mDebuggable >= LEVEL_INFO) {
            Log.i(mTag, msg);
        }
    }

    public static void i(String tag , String msg) {
        if (DEBUG && mDebuggable >= LEVEL_INFO) {
            Log.i(tag , msg);
        }
    }

    /**
     * 以级别为 w 的形式输出LOG
     *
     * @param msg 需要输出的msg
     */
    public static void w(String msg) {
        if (DEBUG && mDebuggable >= LEVEL_WARN) {
            Log.w(mTag, msg);
        }
    }

    public static void w(String tag , String msg) {
        if (DEBUG && mDebuggable >= LEVEL_WARN) {
            Log.w(tag , msg);
        }
    }

    /**
     * 以级别为 e 的形式输出LOG
     *
     * @param msg 需要输出的msg
     */
    public static void e(String msg) {
        if (DEBUG && mDebuggable >= LEVEL_ERROR) {
            Log.e(mTag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG && mDebuggable >= LEVEL_ERROR) {
            Log.e(tag, msg);
        }
    }

    /**
     * 以级别为 w 的形式输出Throwable
     *
     * @param tr 异常
     */
    public static void w(Throwable tr) {
        if (mDebuggable >= LEVEL_WARN) {
            Log.w(mTag, "", tr);
        }
    }

    /**
     * 以级别为 w 的形式输出LOG信息和Throwable
     *
     * @param msg 需要输出的msg
     * @param tr  异常
     */
    public static void w(String msg, Throwable tr) {
        if (mDebuggable >= LEVEL_WARN && null != msg) {
            Log.w(mTag, msg, tr);
        }
    }

    /**
     * 以级别为 e 的形式输出Throwable
     */
    public static void e(Throwable tr) {
        if (mDebuggable >= LEVEL_ERROR) {
            Log.e(mTag, "", tr);
        }
    }

    /**
     * 以级别为 e 的形式输出LOG信息和Throwable
     *
     * @param msg 需要输出的msg
     * @param tr  异常
     */
    public static void e(String msg, Throwable tr) {
        if (mDebuggable >= LEVEL_ERROR && null != msg) {
            Log.e(mTag, msg, tr);
        }
    }
}
