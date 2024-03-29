package com.yougy.common.manager;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;

import com.github.anrwatchdog.ANRError;
import com.github.anrwatchdog.ANRWatchDog;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.onyx.android.sdk.common.request.WakeLockHolder;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.tencent.bugly.crashreport.CrashReport;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;
import com.yougy.anwser.AnswerCheckActivity;
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
import com.yougy.homework.WriteHomeWorkActivity;
import com.yougy.message.ListUtil;
import com.yougy.message.YXClient;
import com.yougy.message.attachment.AskQuestionAttachment;
import com.yougy.message.attachment.EndQuestionAttachment;
import com.yougy.message.attachment.HomeworkRemindAttachment;
import com.yougy.message.attachment.OverallLockAttachment;
import com.yougy.message.attachment.OverallUnlockAttachment;
import com.yougy.message.attachment.PullAnswerCheckAttachment;
import com.yougy.message.attachment.SeatWorkAttachment;
import com.yougy.message.attachment.TaskRemindAttachment;
import com.yougy.order.LockerActivity;
import com.yougy.ui.activity.BuildConfig;
import com.zhy.autolayout.config.AutoLayoutConifg;
import com.zhy.autolayout.utils.ScreenUtils;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;

import static com.yougy.common.global.FileContonst.LOCK_SCREEN;
import static com.yougy.common.global.FileContonst.NO_LOCK_SCREEN;


/**
 * Created by Administrator on 2016/8/24.
 */
public class YoungyApplicationManager extends LitePalApplication {

    public static String lastAnsMsg = "无消息";
    public static String start_net = "无消息";
    public static String end_net = "无消息";
    private final int REQUEST_TIME = 15 * 1000;
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
    public static boolean NEED_PROMOTION = true;
    public static boolean IN_CHATTING = false;
    ANRWatchDog anrWatchDog = new ANRWatchDog(9000);

    private long lastReceiverTime;
    private String lastExamId;//上次收到作业的时间，主要解决待机重启后，短时间内收到多条相同布置的作业的消息的过滤判断
    private WakeLockHolder mHolder;

    Runnable pullAnswerCheckRunnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(getApplicationContext(), AnswerCheckActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Commons.seetingUrl(this);

        //由于云信除了运行在app主进程之外,还会另外开几个其他的进程用于保活和云信core代码的运行.
        //这导致application的onCreate会在每个进程运行时都被调用一次,导致多次调用,所以以下application的初始化工作区分进程进行.
        //云信的初始化需要在每个进程中都初始化,所以不区分进程.
        YXClient.initNimClient(this);

        //其他的正常初始化需要区分进程,只在主进程里初始化
        if (inMainProcess(this)) {
            //申请wakeLock,保证不进入睡眠
//            android.os.PowerManager powerManager = (android.os.PowerManager) getSystemService(Context.POWER_SERVICE);
//            android.os.PowerManager.WakeLock wakeLock = powerManager.newWakeLock(android.os.PowerManager.FULL_WAKE_LOCK, "leke:myWakeLock");
//            wakeLock.acquire();

            //       watcher = LeakCanary.install(this);
            mContext = this;
            instance = this;
            mInstance = this;
            mMainThreadHandler = new Handler();

            mMainThreadId = android.os.Process.myTid();
            mMainThread = Thread.currentThread();
            mMainLooper = getMainLooper();
            rxBus = new RxBus();

            NetManager.getInstance().registerReceiver(this);
            PowerManager.getInstance().registerReceiver(this);

            //创建 课本 文件夹
            FileUtils.createDirs(FileUtils.getTextBookFilesDir());
            //创建 课本 图片 文件夹
            FileUtils.createDirs(FileUtils.getTextBookIconFilesDir());
            //创建试读PDF 文件夹
            FileUtils.createDirs(FileUtils.getProbationBookFilesDir());

            //创建 媒体文件夹
            FileUtils.createDirs(FileUtils.getMediaJsonPath());
            FileUtils.createDirs(FileUtils.getMediaMp3Path());

            if (!BuildConfig.DEBUG) {
                //处理异常
                String logFile = FileUtils.getLogFilesDir() + DateUtils.getCurrentTimeSimpleYearMonthDayString() + "/" + "Error_Log.txt";
                FileUtils.makeParentsDir(logFile);
                YoungyUncaughtExceptionHandler handler = new YoungyUncaughtExceptionHandler(logFile);
                Thread.setDefaultUncaughtExceptionHandler(handler);
            }


//            OkHttpClient client = new OkHttpClient.Builder()
//                    .connectTimeout(REQUEST_TIME, TimeUnit.MILLISECONDS)
//                    .readTimeout(REQUEST_TIME, TimeUnit.MILLISECONDS)
//                    .build();
//            OkHttpUtils.initClient(client);

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
            //需求变更:本地锁暂时取消
//            IntentFilter filter = new IntentFilter();
//            filter.addAction("android.intent.action.SCREEN_ON");
//            registerReceiver(new BroadcastReceiver() {
//                @Override
//                public void onReceive(Context context, Intent intent) {
//                    if (BaseActivity.getForegroundActivity() != null
//                            && !(BaseActivity.getForegroundActivity() instanceof LocalLockActivity)
//                            && !TextUtils.isEmpty(SpUtils.getLocalLockPwd())) {
//                        Intent newIntent = new Intent(context, LocalLockActivity.class);
//                        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        newIntent.putExtra(NOT_GOTO_HOMEPAGE_ON_ENTER, true);
//                        context.startActivity(newIntent);
//                    }
//                }
//            }, filter);

            //初始化云信配置,注册全局性的处理器和解析器等
            YXClient.getInstance().initOption(this);
            //初始化命令消息监听器,在此处处理
            YXClient.getInstance().with(this).addOnNewCommandCustomMsgListener(new YXClient.OnMessageListener() {
                @Override
                public void onNewMessage(IMMessage message) {
                    if (message.getAttachment() instanceof AskQuestionAttachment) {
                        lastAnsMsg = "服务器发送结果：" + "接收时间" + DateUtils.getTimeString() + "消息内容" + message.getAttachment().toString();
                        getMainThreadHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Calendar now = Calendar.getInstance();
                                Calendar messageTime = Calendar.getInstance();
                                messageTime.setTime(new Date(message.getTime()));
                                if ((messageTime.get(Calendar.YEAR) == now.get(Calendar.YEAR))
                                        && (messageTime.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR))) {
                                    int examId = ((AskQuestionAttachment) message.getAttachment()).examID;
                                    if (!ListUtil.conditionalContains(AnsweringActivity.handledExamIdList, new ListUtil.ConditionJudger<Integer>() {
                                        @Override
                                        public boolean isMatchCondition(Integer nodeInList) {
                                            return nodeInList.intValue() == examId;
                                        }
                                    })) {
                                        Intent newIntent = new Intent(getApplicationContext(), AnsweringActivity.class);
                                        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        newIntent.putExtra("itemId", ((AskQuestionAttachment) message.getAttachment()).itemId + "");
                                        newIntent.putExtra("from", ((AskQuestionAttachment) message.getAttachment()).from);
                                        newIntent.putExtra("examId", ((AskQuestionAttachment) message.getAttachment()).examID);
                                        startActivity(newIntent);
                                        AnsweringActivity.handledExamIdList.add(examId);
                                    }
                                }
                            }
                        }, 1000);
                    } else if (message.getAttachment() instanceof EndQuestionAttachment) {
                        if (!ListUtil.conditionalContains(AnsweringActivity.handledExamIdList, new ListUtil.ConditionJudger<Integer>() {
                            @Override
                            public boolean isMatchCondition(Integer nodeInList) {
                                return nodeInList.intValue() == ((EndQuestionAttachment) message.getAttachment()).examID;
                            }
                        })) {
                            AnsweringActivity.handledExamIdList.add(((EndQuestionAttachment) message.getAttachment()).examID);
                        }
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
                    } else if (message.getAttachment() instanceof SeatWorkAttachment) {
                        //判断是否在写作业界面
                        SeatWorkAttachment attachment = (SeatWorkAttachment) message.getAttachment();
                        if (BaseActivity.getForegroundActivity() instanceof WriteHomeWorkActivity) {
                            //当前在写作业界面  examId 判断
                            WriteHomeWorkActivity writeHomeWorkActivity = (WriteHomeWorkActivity) BaseActivity.getForegroundActivity();
                            if (writeHomeWorkActivity.getExam_id().equals(attachment.examId)) {
                                //当前显示在前端  作业仍然未提交
                                return;
                            }
                        } else {
                            if (System.currentTimeMillis() - lastReceiverTime < 50
                                    && attachment.examId != null && attachment.examId.equals(lastExamId)) {
                                lastExamId = attachment.examId;
                                lastReceiverTime = System.currentTimeMillis();
                                return;
                            }
                        }
                        lastExamId = attachment.examId;
                        lastReceiverTime = System.currentTimeMillis();
                        Intent intent = new Intent(getApplicationContext(), WriteHomeWorkActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("examId", attachment.examId);
                        intent.putExtra("examName", attachment.examName);
                        //传参是否定时作业
                        intent.putExtra("isTimerWork", attachment.isTimeWork);
                        intent.putExtra("lifeTime", attachment.lifeTime);
                        intent.putExtra("teacherID", attachment.teacherId);
                        intent.putExtra("isOnClass", true);
                        intent.putExtra("isStudentCheck", attachment.isStudentCheck);
                        startActivity(intent);
                    } else if (message.getAttachment() instanceof PullAnswerCheckAttachment) {
                        if (!(BaseActivity.getForegroundActivity() instanceof AnswerCheckActivity)) {
                            getMainThreadHandler().removeCallbacks(pullAnswerCheckRunnable);
                            //此处延迟3000可以避免多个pullAnswerCheck消息同时到来拉起多次
                            getMainThreadHandler().postDelayed(pullAnswerCheckRunnable, 3000);
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

            //
            YXClient.getInstance().with(this).addOnNewMessageListener(message -> {
                if (IN_CHATTING) {
                    return;
                }
                if (message.getAttachment() instanceof TaskRemindAttachment) {
                    TaskRemindAttachment attachment = (TaskRemindAttachment) message.getAttachment();
                    if (!SpUtils.isHomeworkOrTaskfinished("task_" + attachment.taskId) && !taskReminds.contains(attachment)) {
                        taskReminds.add(attachment);
                    }
                    BaseEvent event = new BaseEvent(EventBusConstant.EVENT_REMIND);
                    EventBus.getDefault().post(event);
                } else if (message.getAttachment() instanceof HomeworkRemindAttachment) {
                    HomeworkRemindAttachment attachment = (HomeworkRemindAttachment) message.getAttachment();
                    if (!SpUtils.isHomeworkOrTaskfinished("homework_" + attachment.examId) && !homeworkReminds.contains(attachment)) {
                        homeworkReminds.add(attachment);
                    }
                    BaseEvent event = new BaseEvent(EventBusConstant.EVENT_REMIND);
                    EventBus.getDefault().post(event);
                }
            });

            changeSystemConfigIntegerValue();
            mHolder = new WakeLockHolder();
            mHolder.acquireWakeLock(mContext, "onyx-framework");

            registerScreenReceiver();

            LogUtils.setOpenLog(BuildConfig.DEBUG);
        }
    }

    public static final List<TaskRemindAttachment> taskReminds = new ArrayList<>();

    public static TaskRemindAttachment getTaskRemind() {
        if (taskReminds.size() == 0) {
            return null;
        }
        LogUtils.e("JiangLiang", "taskReminds' size is : " + taskReminds.size());
        return taskReminds.get(0);
    }

    public static void removeTaskRemind(TaskRemindAttachment attachment) {
        taskReminds.remove(attachment);
        LogUtils.e("JiangLiang", "after remove taskReminds'size is : " + taskReminds.size());
    }

    public static void removeTaskRemind(int taskId) {
        for (TaskRemindAttachment attachment : taskReminds) {
            if (taskId == attachment.taskId) {
                taskReminds.remove(attachment);
                break;
            }
        }
    }

    public static final List<HomeworkRemindAttachment> homeworkReminds = new ArrayList<>();

    public static HomeworkRemindAttachment getHomeworkRemind() {
        if (homeworkReminds.size() == 0) {
            return null;
        }
        LogUtils.e("FH_YoungyApplicationManager", "homeworkReminds' size is : " + homeworkReminds.size());
        return homeworkReminds.get(0);
    }

    public static void removeHomeworkRemind(HomeworkRemindAttachment attachment) {
        homeworkReminds.remove(attachment);
        LogUtils.e("FH_YoungyApplicationManager", "after remove homeworkReminds'size is : " + homeworkReminds.size());
    }

    public static void removeHomeworkRemind(String examId) {
        for (HomeworkRemindAttachment attachment : homeworkReminds) {
            if (examId != null && examId.equals(attachment.examId)) {
                homeworkReminds.remove(attachment);
                break;
            }
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        unRegisterScreenReceiver();
        NetManager.getInstance().unregisterReceiver(this);
        PowerManager.getInstance().unregisterReceiver(this);
        if (mHolder != null) {
            mHolder.releaseWakeLock();
        }
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

    /**
     * 应用是否在前台
     *
     * @return
     */
    public boolean isForegroundApp() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runnings = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo running : runnings) {
            if (running.processName.equals(getPackageName())) {
                if (running.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                        || running.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                    return true;
                }
            }
        }
        return false;
    }


    public static boolean changeSystemConfigIntegerValue() {
        try {
            return Settings.System.putInt(mContext.getContentResolver(), "close_wifi_delay", -1);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private ScreenBroadcastReceiver mScreenReceiver;

    /**
     * screen状态广播接收者
     */
    private class ScreenBroadcastReceiver extends BroadcastReceiver {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            LogUtils.w("ScreenBroadcastReceiver", action);
            if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
                mHolder.acquireWakeLock(YoungyApplicationManager.this, "onyx-framework");
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
                if (mHolder != null) {
                    mHolder.releaseWakeLock();
                }
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
            }
        }
    }

    private void registerScreenReceiver() {
        mScreenReceiver = new ScreenBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mScreenReceiver, filter);
    }

    private void unRegisterScreenReceiver() {
        if (mScreenReceiver != null) unregisterReceiver(mScreenReceiver);
    }

}
