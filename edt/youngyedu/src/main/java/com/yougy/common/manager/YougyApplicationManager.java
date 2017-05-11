package com.yougy.common.manager;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.inkscreen.LeController;
import com.inkscreen.utils.NetworkManager;
import com.tencent.bugly.crashreport.CrashReport;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;
import com.yougy.common.global.Commons;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.DateUtils;
import com.yougy.common.utils.FileUtils;
import com.yougy.ui.activity.BuildConfig;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2016/8/24.
 */
public class YougyApplicationManager extends LitePalApplication {

    private final int REQUEST_TIME = 10 * 1000;
    public static final boolean DEBUG = true;

    private static Context instance;
    /**
     * 主线程Handler
     */
    public static Handler mMainThreadHandler;
    /**
     * 全局Context
     */
    public static YougyApplicationManager mInstance;
    /**
     * 主线程ID
     */
    private static int mMainThreadId = -1;
    /**
     * 主线程ID
     */
    private static Thread mMainThread;
    /**
     * 主线程Looper
     */
    private static Looper mMainLooper;
//    private static RefWatcher watcher;

    private static YougyApplicationManager mContext;

    @Override
    public void onCreate() {
        super.onCreate();
//       watcher = LeakCanary.install(this);
        LeController.init(this);
        mContext = this;
        instance = this;
        mInstance = this;
        NetworkManager.getInstance().shutdownHttpClient();
        mMainThreadHandler = new Handler();

        mMainThreadId = android.os.Process.myTid();
        mMainThread = Thread.currentThread();
        mMainLooper = getMainLooper();

        rxBus = new RxBus();

        //创建 课本 文件夹
        FileUtils.createDirs(FileUtils.getTextBookFilesDir());
        //创建 课本 图片 文件夹
        FileUtils.createDirs(FileUtils.getTextBookIconFilesDir());
        //创建试读PDF 文件夹
        FileUtils.createDirs(FileUtils.getProbationBookFilesDir());


        //处理异常
        String logFile = FileUtils.getLogFilesDir() + DateUtils.getCurrentTimeSimpleYearMonthDayString() + "/" + "Error_Log.txt";
        FileUtils.makeParentsDir(logFile);
        YoungyUncaughtExceptionHandler handler = new YoungyUncaughtExceptionHandler(logFile);
        Thread.setDefaultUncaughtExceptionHandler(handler);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(REQUEST_TIME, TimeUnit.MILLISECONDS)
                .readTimeout(REQUEST_TIME, TimeUnit.MILLISECONDS)
                .build();
        OkHttpUtils.initClient(client);

        NoHttp.initialize(this);

        Logger.setTag("leke");
        Logger.setDebug(false);// 开始NoHttp的调试模式, 这样就能看到请求过程和日志
        //设备ID
//       Commons.UUID  = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID); // UUID
        Commons.UUID  =  getLocalMac();
//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().build());
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().build());


        Context context = getApplicationContext();
        // 获取当前包名
        String packageName = context.getPackageName();
        // 获取当前进程名
        String processName = getProcessName(android.os.Process.myPid());
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        // 初始化Bugly
        CrashReport.initCrashReport(context, "68d9d03b4a", BuildConfig.DEBUG, strategy);

    }

    public static void closeDb(){
        LitePal.getDatabase().close();
    }
    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    public static YougyApplicationManager getApp() {
        return mContext;
    }

    private RxBus rxBus;

    public static RxBus getRxBus(Context context) {
        YougyApplicationManager application = (YougyApplicationManager) context.getApplicationContext();
        return application.rxBus;
    }

    public static boolean isWifiAvailable() {
        WifiManager manager = (WifiManager) instance.getSystemService(Context.WIFI_SERVICE);
        return manager.isWifiEnabled();
    }

//    public static RefWatcher getWatcher() {
//        return watcher;
//    }

    public static Context getInstance() {
        return instance;
    }

    public static YougyApplicationManager getApplication() {
        return mInstance;
    }

    public static Handler getMainThreadHandler() {
        return mMainThreadHandler;
    }


    public static int getMainThreadId() {
        return mMainThreadId;
    }

    public static Thread getMainThread() {
        return mMainThread;
    }

    public static Looper getMainThreadLooper() {
        return mMainLooper;
    }

    private  String  getLocalMac() {
        String mac=null;
        String str = "";
        try
        {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (; null != str;)
            {
                str = input.readLine();
                if (str != null)
                {
                    mac = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return mac;

    }
}
