package com.yougy.common.manager;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.github.anrwatchdog.ANRError;
import com.github.anrwatchdog.ANRWatchDog;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.tencent.bugly.crashreport.CrashReport;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;
import com.yougy.anwser.AnsweringActivity;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.eventbus.EventBusConstant;
import com.yougy.common.global.Commons;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.DateUtils;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.init.activity.LocalLockActivity;
import com.yougy.message.YXClient;
import com.yougy.message.attachment.AskQuestionAttachment;
import com.yougy.message.attachment.EndQuestionAttachment;
import com.yougy.message.attachment.OverallLockAttachment;
import com.yougy.message.attachment.OverallUnlockAttachment;
import com.yougy.message.attachment.RetryAskQuestionAttachment;
import com.yougy.order.LockerActivity;
import com.zhy.autolayout.config.AutoLayoutConifg;
import com.zhy.autolayout.utils.ScreenUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import okhttp3.OkHttpClient;

import static com.yougy.common.global.Commons.isRelase;
import static com.yougy.common.global.FileContonst.LOCK_SCREEN;
import static com.yougy.common.global.FileContonst.NO_LOCK_SCREEN;
import static com.yougy.init.activity.LocalLockActivity.NOT_GOTO_HOMEPAGE_ON_ENTER;

//import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by Administrator on 2016/8/24.
 */
public class YoungyApplicationManager extends LitePalApplication {

    private final int REQUEST_TIME =15* 1000;
    public static final boolean DEBUG = true;

    private static Context instance;
    /**
     * 主线程Handler
     */
    public static Handler mMainThreadHandler;
    /**
     * 全局Context
     */
    public static YoungyApplicationManager mInstance;
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

    private static YoungyApplicationManager mContext;

    ANRWatchDog anrWatchDog = new ANRWatchDog(9000);

    @Override
    public void onCreate() {
        super.onCreate();

        //由于云信除了运行在app主进程之外,还会另外开几个其他的进程用于保活和云信core代码的运行.
        //这导致application的onCreate会在每个进程运行时都被调用一次,导致多次调用,所以以下application的初始化工作区分进程进行.
        //云信的初始化需要在每个进程中都初始化,所以不区分进程.
        YXClient.initNimClient(this);

        //其他的正常初始化需要区分进程,只在主进程里初始化
        if (inMainProcess(this)) {
            //申请wakeLock,保证不进入睡眠
            android.os.PowerManager powerManager = (android.os.PowerManager) getSystemService(Context.POWER_SERVICE);
            android.os.PowerManager.WakeLock wakeLock = powerManager.newWakeLock(android.os.PowerManager.FULL_WAKE_LOCK, "leke");
            wakeLock.acquire();

            //       watcher = LeakCanary.install(this);
            mContext = this;
            instance = this;
            mInstance = this;
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

            if (isRelase) {
                //处理异常
                String logFile = FileUtils.getLogFilesDir() + DateUtils.getCurrentTimeSimpleYearMonthDayString() + "/" + "Error_Log.txt";
                FileUtils.makeParentsDir(logFile);
                YoungyUncaughtExceptionHandler handler = new YoungyUncaughtExceptionHandler(logFile);
                Thread.setDefaultUncaughtExceptionHandler(handler);
            }


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
//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().build());
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().build());

            Commons.UUID = SpUtils.getUUID();
            if (TextUtils.isEmpty(Commons.UUID)) {
                Commons.UUID = NetworkUtil.getMacAddress(this).replaceAll(":", "");
            }
            LogUtils.i("mac_application__" + Commons.UUID);
            Context context = getApplicationContext();
            // 获取当前包名
            String packageName = context.getPackageName();
            // 获取当前进程名
            String processName = getProcessName(android.os.Process.myPid());
            // 设置是否为上报进程
            CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
            strategy.setUploadProcess(processName == null || processName.equals(packageName));
            // 初始化Bugly
            CrashReport.initCrashReport(context, "9629dd7708", false, strategy);


            //AutoLayout初始化
            AutoLayoutConifg.getInstance().init(this);
            int[] screenSize = ScreenUtils.getScreenSize(context, false);
            int mScreenWidth = screenSize[0];
            int mScreenHeight = screenSize[1];
            LogUtils.e("FH", " screenWidth =" + mScreenWidth + " ,screenHeight = " + mScreenHeight);

            //注册屏幕开锁广播接收器,每次开锁的时候回跳到本地锁.
            //本广播只会在应用程序启动后注册,未启动应用时,不能检测到开屏广播
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.SCREEN_ON");
            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (BaseActivity.getForegroundActivity() != null
                            && !(BaseActivity.getForegroundActivity() instanceof LocalLockActivity)
                            && !TextUtils.isEmpty(SpUtils.getLocalLockPwd())) {
                        Intent newIntent = new Intent(context, LocalLockActivity.class);
                        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        newIntent.putExtra(NOT_GOTO_HOMEPAGE_ON_ENTER, true);
                        context.startActivity(newIntent);
                    }
                }
            }, filter);

            //初始化云信配置,注册全局性的处理器和解析器等
            YXClient.getInstance().initOption(this);
            //初始化命令消息监听器,在此处处理
            YXClient.getInstance().with(this).addOnNewCommandCustomMsgListener(new YXClient.OnMessageListener() {
                @Override
                public void onNewMessage(IMMessage message) {
                    if (message.getAttachment() instanceof AskQuestionAttachment) {
                        Intent newIntent = new Intent(getApplicationContext(), AnsweringActivity.class);
                        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        newIntent.putExtra("itemId", ((AskQuestionAttachment) message.getAttachment()).itemId + "");
                        newIntent.putExtra("from", ((AskQuestionAttachment) message.getAttachment()).from);
                        newIntent.putExtra("examId", ((AskQuestionAttachment) message.getAttachment()).examID);
                        startActivity(newIntent);

                    } else if (message.getAttachment() instanceof EndQuestionAttachment) {
                        rxBus.send(message);
                    } else if (message.getAttachment() instanceof OverallLockAttachment) {
                        //TODO 全局锁屏
                        String time = ((OverallLockAttachment) message.getAttachment()).time;
                        LogUtils.i("全局锁屏" + time);
                        if (time.equalsIgnoreCase(DateUtils.getCalendarString())) {
                            SpUtils.setOrder(LOCK_SCREEN + time);
                            Intent newIntent = new Intent(getApplicationContext(), LockerActivity.class);
                            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(newIntent);
                        }
                    } else if (message.getAttachment() instanceof OverallUnlockAttachment) {
                        String time = ((OverallUnlockAttachment) message.getAttachment()).time;
                        LogUtils.i("全局解锁" + time);
                        //TODO 全局解锁
                        if (time.equalsIgnoreCase(DateUtils.getCalendarString())) {
                            SpUtils.setOrder(NO_LOCK_SCREEN);
                            BaseEvent baseEvent = new BaseEvent(EventBusConstant.EVENT_CLEAR_ACTIIVTY_ORDER, "");
                            EventBus.getDefault().post(baseEvent);
                        }
                    } else if (message.getAttachment() instanceof RetryAskQuestionAttachment) {
                        if (AnsweringActivity.lastExamId != ((RetryAskQuestionAttachment) message.getAttachment()).examId) {
                            Intent newIntent = new Intent(getApplicationContext(), AnsweringActivity.class);
                            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            newIntent.putExtra("itemId", ((RetryAskQuestionAttachment) message.getAttachment()).itemId + "");
                            newIntent.putExtra("from", ((RetryAskQuestionAttachment) message.getAttachment()).userId + "");
                            newIntent.putExtra("examId", ((RetryAskQuestionAttachment) message.getAttachment()).examId);
                            startActivity(newIntent);
                        }
                    }
                }
            });

          /*  YXClient.getInstance().with(this).addOnNewMessageListener(new YXClient.OnMessageListener() {
                @Override
                public void onNewMessage(IMMessage message) {
                    LogUtils.e("onNewMessage : " + message.getContent());

                    //TODO:测试 锁屏
                    if (message.getContent().equals("order0")){
                        //解锁
                        SpUtils.setOrder("order0");
                        BaseEvent baseEvent = new BaseEvent(EventBusConstant.EVENT_CLEAR_ACTIIVTY_ORDER, "");
                        EventBus.getDefault().post(baseEvent);

                    }else if (message.getContent().equals("order1")){
                        //上锁
                        SpUtils.setOrder("order1");
                        Intent newIntent = new Intent(getApplicationContext(), LockerActivity.class);
                        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(newIntent);
                    }else if (message.getContent().equals("order3")){
                        //解锁学科

                    }else if (message.getContent().equals("order4")){
                        //上锁学科
                    }
                }
            });*/
        }
//        checkAnr();
        LogUtils.setOpenLog(!isRelase);
    }

    private void checkAnr() {
        anrWatchDog.setANRListener(new ANRWatchDog.ANRListener() {
            @Override
            public void onAppNotResponding(ANRError error) {
                LogUtils.e("ANR-Watchdog", "Detected Application Not Responding!");

                // Some tools like ACRA are serializing the exception, so we must make sure the exception serializes correctly
                try {
                    new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(error);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                LogUtils.e("ANR-Watchdog", "Error was successfully serialized");

                throw error;
            }
        });
        anrWatchDog.start();
    }

    /**
     * 判断App是否是在主进程中(云信的sdk会开其他的进程,例如保活进程,会使Application.create()多次调用,需要用这个方法判断是否是在主进程中)
     *
     * @param context
     * @return
     */
    public static boolean inMainProcess(Context context) {
        String packageName = context.getPackageName();
        String processName = getProcessName(context);
        return packageName.equals(processName);
    }

    /**
     * 获取当前进程名
     *
     * @param context
     * @return 进程名
     */
    public static final String getProcessName(Context context) {
        String processName = null;

        // ActivityManager
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));

        while (true) {
            for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
                if (info.pid == android.os.Process.myPid()) {
                    processName = info.processName;
                    break;
                }
            }

            // go home
            if (!TextUtils.isEmpty(processName)) {
                return processName;
            }

            // take a rest and again
            try {
                Thread.sleep(100L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void closeDb() {
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

    public static YoungyApplicationManager getApp() {
        return mContext;
    }

    private RxBus rxBus;

    public static RxBus getRxBus(Context context) {
        YoungyApplicationManager application = (YoungyApplicationManager) context.getApplicationContext();
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

    public static YoungyApplicationManager getApplication() {
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
