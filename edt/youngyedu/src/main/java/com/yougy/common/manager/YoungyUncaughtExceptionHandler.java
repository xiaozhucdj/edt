package com.yougy.common.manager;

import android.content.Context;
import android.content.Intent;

import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.FormatUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.PackageUtils;
import com.yougy.common.utils.SystemUtils;
import com.yougy.home.activity.SplashActivity;

import java.io.File;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Administrator on 2016/9/26.
 * 捕获全局 异常
 */
public class YoungyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
//==========================================================================
    // 全局变量
    //==========================================================================

    private Context mContext;
    private static String mDefaultLogFileName = "Error_Log.txt";
    private static String mLogFileAbsPath = FileUtils.getAppFilesDir() + mDefaultLogFileName;

    //==========================================================================
    // 构造方法
    //==========================================================================
    public YoungyUncaughtExceptionHandler(String logFileAbsPath) {
        mLogFileAbsPath = logFileAbsPath;
    }

    public YoungyUncaughtExceptionHandler() {

    }

    //==========================================================================
    // 方法
    //==========================================================================

    /**
     * 如果注册了该handler，那么未捕获的异常都将由该方法处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        handleException(ex);
    }

    /**
     * 处理异常
     *
     * @param ex 异常
     */
    private void handleException(final Throwable ex) {
        if (ex == null) {
            return;
        }
        ex.printStackTrace();
        ThreadManager.getShortPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    RandomAccessFile file = new RandomAccessFile(mLogFileAbsPath, "rw");
                    LogUtils.e("ExceptionHandler", "file abs path : " + mLogFileAbsPath);
                    if (isExistsException(ex, file)) {
                        return;
                    }

                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    ex.printStackTrace(pw);
                    // 存储手机系统版本和运行的app版本，以便对不同版本的系统做不同的处理
                    StringBuilder builder = new StringBuilder();
                    builder.append(getTime());
                    builder.append(", ");
                    builder.append("versionName=");
                    builder.append(PackageUtils.getVersionName());
                    builder.append("versionCode=");
                    builder.append(PackageUtils.getVersionCode());
                    builder.append(", ");
                    builder.append("sdk=");
                    builder.append(SystemUtils.getOSVersionSDKINT());
                    builder.append("\n");

                    byte[] hashCode = ("#" + (sw.toString().hashCode()) + "\n").getBytes(); // 异常信息hashCode
                    byte[] header = builder.toString().getBytes(); // 头信息
                    byte[] errInfos = sw.toString().getBytes(); // 异常信息
                    byte[] skipLen = ("*" + (header.length + errInfos.length) + "\n").getBytes(); // 需要跳过的字节数,用于检索

                    long fileLen = file.length();
                    file.setLength(fileLen + hashCode.length + skipLen.length + header.length + errInfos.length);
                    file.seek(fileLen);

                    file.write(hashCode); // 第一行存储 hashCode
                    file.write(skipLen); // 第二行存储异常信息长度
                    file.write(header); // 存储头信息
                    file.write(errInfos); // 存储异常信息正文
                } catch (Exception e) {
//                    LogUtils.e(e);
                } finally {
                    Intent intent = new Intent(YougyApplicationManager.getContext(),SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    YougyApplicationManager.getContext().startActivity(intent);
                    android.os.Process.killProcess(android.os.Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这
                }
            }
        });
    }

    //

    /**
     * 检测文件是否已经存在需要存储的异常信息，避免重复存储
     */
    private boolean isExistsException(Throwable ex, RandomAccessFile file) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            int hashCode = sw.toString().hashCode();
            String value;
            while (null != (value = file.readLine())) {
                if (value.startsWith("*")) {
                    value = value.replace("*", "");
                    int len = Integer.parseInt(value);
                    file.skipBytes(len);
                    continue;
                }
                value = value.replace("#", "");
                if (Integer.parseInt(value) == hashCode) {
                    return true;
                }
            }
        } catch (Exception e) {
//            LogUtils.e(e);
        }
        return false;
    }

    private String getTime() {
        return FormatUtils.formatDate("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis());
    }

    /**
     * 获取已经存储的异常信息
     */
    public static String getExceptionLog(Context context) {
        StringBuilder builder = new StringBuilder();
        Set<String> set = new HashSet<String>();
        if (FileUtils.isSDcardExist()) {// SD卡上log
            File file = new File(mLogFileAbsPath);
            if (file.exists()) {
                try {
                    RandomAccessFile rf = new RandomAccessFile(file, "r");
                    String temp;
                    while (null != (temp = rf.readLine())) {
                        if (temp.startsWith("#") || temp.startsWith("*")) {
                            if (temp.startsWith("#")) {
                                set.add(temp.replace("#", ""));
                            }
                            continue;
                        }
                        builder.append(temp);
                        builder.append("\n");
                    }
                    rf.close();
                } catch (Exception e) {
//                    LogUtils.e(e);
                }
            }
        }
        File file = new File(mLogFileAbsPath);
        if (file.exists()) {
            try {
                RandomAccessFile rf = new RandomAccessFile(file, "r");
                String temp;
                boolean isSkip = false;
                while (null != (temp = rf.readLine())) {
                    if (temp.startsWith("*")) {
                        if (isSkip) {
                            isSkip = false;
                            rf.skipBytes(Integer.parseInt(temp.replace("*", "")));
                            continue;
                        }
                    }
                    if (set.contains(temp.replace("#", ""))) { // 过滤掉重复
                        isSkip = true;
                        continue;
                    }
                    if (!temp.startsWith("#") && !temp.startsWith("*")) {
                        builder.append(temp);
                        builder.append("\n");
                    }
                }
                rf.close();
            } catch (Exception e) {
//                LogUtils.e(e);
            }
        }
        return builder.toString();
    }

    /**
     * 删除异常文件
     */
    public static void deleteExceptionFile(Context context) {
        if (FileUtils.isSDcardExist()) {// SD卡上log
            FileUtils.deleteFile(mLogFileAbsPath);
        }
        FileUtils.deleteFile(mLogFileAbsPath);
    }


}

