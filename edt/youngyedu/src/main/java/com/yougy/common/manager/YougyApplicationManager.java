package com.yougy.common.manager;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import com.inkscreen.LeController;
import com.inkscreen.utils.NetworkManager;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;
import com.yougy.common.global.Commons;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.DateUtils;
import com.yougy.common.utils.FileUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

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
        Commons.UUID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID); // UUID
//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().build());
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().build());

    }

    public static void closeDb(){
        LitePal.getDatabase().close();
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
}
