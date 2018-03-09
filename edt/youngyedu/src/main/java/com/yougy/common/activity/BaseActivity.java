package com.yougy.common.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.onyx.android.sdk.api.device.FrontLightController;
import com.yougy.common.dialog.BaseDialog;
import com.yougy.common.utils.SharedPreferencesUtil;
import com.yougy.common.utils.StringUtils;
import com.yougy.view.Toaster;
import com.yougy.view.dialog.LoadingProgressDialog;
import com.yougy.view.dialog.UiPromptDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/8/24.
 */


/**
 * Created by yuanye 2016/08/22
 */
public abstract class BaseActivity extends FragmentActivity implements UiPromptDialog.Listener {
    protected String tag = getClass().getName();
    /**
     * UI 线程ID
     */
    private int mMainThreadId;

    /**
     * handler
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            onHandleMessage(msg);
        }
    };

    private PowerManager.WakeLock mWakeLock;

    /**
     * 记录处于前台的Activity
     */
    private static BaseActivity mForegroundActivity = null;
    WeakReference<BaseActivity> weakReference;
    /**
     * 记录所有活动的Activity
     */
    private static final List<BaseActivity> mActivities = new LinkedList<>();

    private LoadingProgressDialog mProgressDilag;
    /**
     * 记录当前Activity中的所有开启的对话框
     */
    private List<BaseDialog> mDialogs = new LinkedList<>();
    /**
     * 需要在对话框结束之后执行的任务
     */
    private static List<Runnable> mToDoAfterDialogDismiss = new LinkedList<Runnable>();

    /**
     * 获取UI线程ID
     *
     * @return UI线程ID
     */
    public int getMainThreadId() {
        return mMainThreadId;
    }

    /**
     * 获取内置handler
     *
     * @return 内置handler
     */
    public Handler getHandler() {
        return mHandler;
    }


    public static final int USER_ID = 200161010;
//    private WifiStatusChangedReceiver receiver = new WifiStatusChangedReceiver();
//    private IntentFilter filter;


    // ==========================================================================
    // 方法
    // ==========================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //设置无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView();
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        mMainThreadId = Process.myTid();
        mActivities.add(this);
        ButterKnife.bind(this);
        init();
        initLayout();
        loadData();
    }

    /**
     * 设置界面布局文件
     */
    protected abstract void setContentView();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    @Override
    protected void onNewIntent(Intent intent) {
        mMainThreadId = Process.myTid();
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        mForegroundActivity = this;
        weakReference = new WeakReference<>(mForegroundActivity);
        //onResume  中启用
//        mWakeLock = ((PowerManager) getSystemService(POWER_SERVICE))
//                .newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
//                        | PowerManager.ON_AFTER_RELEASE, BaseActivity.this.getClass().getName());
//        mWakeLock.acquire();

        super.onResume();
    }

    @Override
    protected void onPause() {
        mForegroundActivity = null;
//        if (mWakeLock != null) {
//            mWakeLock.release();
//        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDialogs.clear();
        mActivities.remove(this);
    }

//	@Override
//	public void setContentView(View view) {
//		/* Activity的根View，包装一层，方便做统一的修改 */
//		FrameLayout mContentFrame = new FrameLayout(this);
//		mContentFrame.addView(view, ViewGroup.LayoutParams.MATCH_PARENT,
//				ViewGroup.LayoutParams.MATCH_PARENT);
//		super.setContentView(mContentFrame);
//	}
//
//	@Override
//	public void setContentView(int resId) {
//		View view = inflate(resId);
//		setContentView(view);
//	}

    /**
     * 根据指定的layout索引，创建一个View
     *
     * @param resId 指定的layout索引
     * @return 新的View
     */
    public View inflate(int resId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        return inflater.inflate(resId, null);
    }

    /**
     * 向内置handler的消息队列中增加一个任务。该任务会在将来的某一时刻在UI线程执行。
     *
     * @param r Delayed	 *            任务
     */
    public boolean post(Runnable r) {
        return mHandler.post(r);
    }

    /**
     * 向内置handler的消息队列中增加一个任务。该任务会在指定延时后的某一时刻在UI线程执行。
     *
     * @param r           任务
     * @param delayMillis 延时的毫秒数
     */
    public boolean postDelayed(Runnable r, long delayMillis) {
        return mHandler.postDelayed(r, delayMillis);
    }

    /**
     * 向内置handler发送消息。
     */
    public void sendMessage(int what) {
        mHandler.sendEmptyMessage(what);
    }

    /**
     * 向内置handler发送消息。
     */
    public void sendMessage(int what, Object obj) {
        mHandler.obtainMessage(what, obj).sendToTarget();
    }

    /**
     * 向内置handler发送消息。
     */
    public void sendMessage(int what, int arg1, int arg2) {
        mHandler.obtainMessage(what, arg1, arg2).sendToTarget();
    }

    /**
     * 向内置handler发送消息。
     */
    public void sendMessage(int what, int arg1, int arg2, Object obj) {
        mHandler.obtainMessage(what, arg1, arg2, obj).sendToTarget();
    }

    /**
     * 从队列中移除回调
     */
    public void removeCallback(Runnable r) {
        mHandler.removeCallbacks(r);
    }

    /**
     * 内置handler处理到某个消息时，该方法被回调。子类实现该方法以定义对消息的处理。
     */
    protected void onHandleMessage(Message msg) {
    }

    /**
     * 线程安全的刷新UI方法，如果没有特殊需求，请使用该方法，而不要使用refreshView
     */
    public void refreshViewSafe() {
        if (Process.myTid() == mMainThreadId) {
            // 调用在UI线程
            refreshView();
        } else {
            // 调用在非UI线程
            post(new Runnable() {
                @Override
                public void run() {
                    refreshView();
                }
            });
        }
    }

    /**
     * 对toast的简易封装。线程安全，可以在非UI线程调用。
     *
     * @param resId    Toast内容的资源id
     * @param duration Toast的持续时间
     */
    public void showToastSafe(final int resId, final int duration) {
        if (Process.myTid() == mMainThreadId) {
            // 调用在UI线程
            Toaster.showDefaultToast(BaseActivity.this, resId, duration);
        } else {
            // 调用在非UI线程
            post(new Runnable() {
                @Override
                public void run() {
                    Toaster.showDefaultToast(BaseActivity.this, resId, duration);
                }
            });
        }
    }

    /**
     * 对toast的简易封装。线程安全，可以在非UI线程调用。
     *
     * @param text     Toast内容
     * @param duration Toast的持续时间
     */
    public void showToastSafe(final CharSequence text, final int duration) {
        if (Process.myTid() == mMainThreadId) {
            // 调用在UI线程
            Toaster.showDefaultToast(BaseActivity.this, text, duration);
        } else {
            // 调用在非UI线程
            post(new Runnable() {
                @Override
                public void run() {
                    Toaster.showDefaultToast(BaseActivity.this, text, duration);
                }
            });
        }
    }


    public void showToastGravitySafe(final int resId, final int duration) {
        if (Process.myTid() == mMainThreadId) {
            // 调用在UI线程
            Toaster.showGravityToast(BaseActivity.this, resId, duration);
        } else {
            // 调用在非UI线程
            post(new Runnable() {
                @Override
                public void run() {
                    Toaster.showGravityToast(BaseActivity.this, resId, duration);
                }
            });
        }
    }

    /**
     * 对toast的简易封装。线程安全，可以在非UI线程调用。
     *
     * @param text     Toast内容
     * @param duration Toast的持续时间
     */
    public void showToastGravitySafe(final CharSequence text, final int duration) {
        if (Process.myTid() == mMainThreadId) {
            // 调用在UI线程
            Toaster.showGravityToast(BaseActivity.this, text, duration);
        } else {
            // 调用在非UI线程
            post(new Runnable() {
                @Override
                public void run() {
                    Toaster.showGravityToast(BaseActivity.this, text, duration);
                }
            });
        }
    }


    /**
     * 关闭所有Activity
     */
    public static void finishAll() {
        List<BaseActivity> copy;
        synchronized (mActivities) {
            copy = new ArrayList<BaseActivity>(mActivities);
        }
        for (BaseActivity activity : copy) {
            activity.finish();
        }
        copy.clear();

    }

    /**
     * 关闭所有Activity，除了参数传递的Activity
     */
    public static void finishAll(BaseActivity except) {
        List<BaseActivity> copy;
        synchronized (mActivities) {
            copy = new ArrayList<BaseActivity>(mActivities);
        }
        for (BaseActivity activity : copy) {
            if (activity != except)
                activity.finish();
        }
        copy.clear();
    }


    /**
     * 获取当前处于前台的activity
     */
    public static BaseActivity getForegroundActivity() {
        return mForegroundActivity;
    }

    /**
     * 获取当前处于栈顶的activity，无论其是否处于前台
     */
    public static BaseActivity getCurrentActivity() {
        List<BaseActivity> copy;
        synchronized (mActivities) {
            copy = new ArrayList<BaseActivity>(mActivities);
        }
        if (copy.size() > 0) {
            return copy.get(copy.size() - 1);
        }
        return null;
    }

    /**
     * 推出应用
     */
    public static void exitSengledApp() {
        finishAll();
        Process.killProcess(Process.myPid());
    }

    /**
     * dip转换px
     */
    public int dip2px(int dip) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    /**
     * pxz转换dip
     */
    public int px2dip(int px) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    public int getDimens(int resId) {
        return getResources().getDimensionPixelSize(resId);
    }


    /**
     * 读取String的资源文件返回String
     */
    public String getStrText(int resId) {
        return this.getString(resId);
    }

    public void loadIntent(Context packageContext, Class<?> cls) {
        Intent intent = new Intent(packageContext, cls);
        startActivity(intent);
    }

    public void loadIntent(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    /**
     * 启动目标Activity,比将任务栈中已有的此Activity的实例清除
     */
    public void loadIntentFlag(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    /**
     * 启动目标Activity,并携带传入的FLAG和额外数据.
     */
    public void loadIntentWithFlagAndExtras(Class<? extends Activity> cls, int flag, Bundle extras) {
        Intent intent = new Intent(BaseActivity.this, cls);
        intent.setFlags(flag);
        intent.putExtras(extras);
        startActivity(intent);
    }

    public void loadIntentWithExtras(Class<? extends Activity> cls, Bundle extras) {
        Intent intent = new Intent(BaseActivity.this, cls);
        intent.putExtras(extras);
        startActivity(intent);
    }

    public void loadIntentWithExtra(Class<? extends Activity> cls, String key, int value) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(key, value);
        startActivity(intent);
    }

    public void loadIntentWithSpecificFlag(Class<?> cls, int flag) {
        Intent intent = new Intent(this, cls);
        intent.setFlags(flag);
        startActivity(intent);
    }


    /**
     * 获取Intent数据以及初始化本地数据
     */
    protected abstract void init();

    /**
     * 初始化控件
     */
    protected abstract void initLayout();

    /**
     * 从服务器加载数据
     */
    protected abstract void loadData();

    /**
     * 线程安全的刷新 refreshViewSafe 调用
     */
    protected abstract void refreshView();

    /**
     * 时间 间隔
     */
    protected long PRESS_TWICE_TO_EXIT_SPACE_TIME = 2000;
    /**
     * 二次back键盘 退出APP
     */
    protected boolean mIsPressTwiceToExit = false;
    protected long mLastBackPressedTime = -1;

    /**
     * 需要退出APP的 界面使用该函数
     */
    protected void setPressTwiceToExit(boolean isPressTwiceToExit) {
        mIsPressTwiceToExit = isPressTwiceToExit;
    }

    @Override
    public void onBackPressed() {
        if (!mIsPressTwiceToExit) {
            super.onBackPressed();
            return;
        }

        long currTime = System.currentTimeMillis();
        if (currTime - mLastBackPressedTime > PRESS_TWICE_TO_EXIT_SPACE_TIME) {
            showToastSafe("两秒内再次按下退出", Toast.LENGTH_SHORT);
            mLastBackPressedTime = currTime;
        } else {
            super.onBackPressed();
            //恢复打开app前的背光情况。
            boolean isLightOn = SharedPreferencesUtil.getSpUtil().getBoolean("OLD_ISLIGHTON", true);
            if (isLightOn) {
                FrontLightController.turnOn(this);
            } else {
                FrontLightController.turnOff(this);
            }
            int nowBrightness = SharedPreferencesUtil.getSpUtil().getInt("OLD_BRIGHTNESS", 0);
            FrontLightController.setBrightness(this, nowBrightness);


            finishAll();
        }
    }


    ///////////////////////////加载对话框/////////////////////////////////

    public void showProgressDialog(String msg) {
        if (StringUtils.isEmpty(msg)) {
            msg = "网络加载中";
        }

        if (mProgressDilag == null) {
            mProgressDilag = new LoadingProgressDialog(BaseActivity.this);
        }
        if (!mProgressDilag.isShowing()) {
            showDialogSafe(mProgressDilag);
            mProgressDilag.setTitle(msg);
        }

    }

    public void showProgressDialog(int resId) {
        if (mProgressDilag == null) {
            mProgressDilag = new LoadingProgressDialog(BaseActivity.this);
        }
        if (!mProgressDilag.isShowing()) {
            showDialogSafe(mProgressDilag);
            mProgressDilag.setTitle(resId);
        }
    }

    /**
     * 对showDialog的简易封装。线程安全，可以在非UI线程调用。
     *
     * @param dialog Dialog实例
     */
    public void showDialogSafe(final BaseDialog dialog) {
        if (Process.myTid() == mMainThreadId) {
            // 调用在UI线程
            showYoungdDialog(dialog);
        } else {
            // 调用在非UI线程
            post(new Runnable() {
                @Override
                public void run() {
                    showYoungdDialog(dialog);
                }
            });
        }
    }

    private void showYoungdDialog(BaseDialog dialog) {
        if (!mDialogs.contains(dialog)) {
            dialog.show();
            mDialogs.add(dialog);
        }
    }

    public void dismissProgressDialog() {
        if (mProgressDilag != null && mProgressDilag.isShowing()) {
            dismissDialogSafe(mProgressDilag);
        }
    }


    /**
     * 对dismissDialog的简易封装。线程安全，可以在非UI线程调用。
     *
     * @param dialog Dialog实例
     */
    public void dismissDialogSafe(final BaseDialog dialog) {
        if (Process.myTid() == mMainThreadId) {
            // 调用在UI线程
            dismissYoungDialog(dialog);
        } else {
            // 调用在非UI线程
            post(new Runnable() {
                @Override
                public void run() {
                    dismissYoungDialog(dialog);
                }
            });
        }
    }

    private void dismissYoungDialog(BaseDialog dialog) {
        dialog.dismiss();
        mDialogs.remove(dialog);
        if (getAllShowingDialogCount() == 0) {
            runDialogDismissPendingRunnables();
        }
    }

    /**
     * 获取该Activity开启的对话框个数
     */
    public int getShowingDialogCount() {
        return mDialogs.size();
    }

    /**
     * 获取所有Activity开启的对话框个数
     */
    public static int getAllShowingDialogCount() {
        int total = 0;
        for (BaseActivity mActivity : mActivities) {
            total += mActivity.getShowingDialogCount();
        }
        return total;
    }

    /**
     * 判断是否有对话框开启，如果有，则在对话框关闭后执行，没有则立即执行
     *
     * @param r 该任务是在主线程中执行的，请勿做耗时操作
     */
    public void doAfterAllDialogDismissSafe(final Runnable r) {
        if (Process.myTid() == getMainThreadId()) {
            // 调用在UI线程
            doAfterAllDialogDismiss(r);
        } else {
            // 调用在非UI线程
            post(new Runnable() {
                @Override
                public void run() {
                    doAfterAllDialogDismiss(r);
                }
            });
        }
    }

    private void doAfterAllDialogDismiss(Runnable r) {
        if (getAllShowingDialogCount() > 0) {
            // 有Dialog或DialogActivity在显示，延迟显示
            mToDoAfterDialogDismiss.add(r);
        } else {
            r.run();
        }
    }

    /**
     * 执行所有需要在对话框关闭后执行的任务
     */
    public void runDialogDismissPendingRunnables() {
        for (Runnable r : mToDoAfterDialogDismiss) {
            r.run();
        }
    }


    protected UiPromptDialog mUiPromptDialog;

    protected UiPromptDialog getUiPromptDialog() {
        return mUiPromptDialog;
    }
    //---------------------------------CancelAndDetermine--------------------------------------------

    /**
     * @param titleId 标题
     */
    protected void showCancelAndDetermineDialog(int titleId) {
        if (mUiPromptDialog == null) {
            mUiPromptDialog = new UiPromptDialog(this);
            mUiPromptDialog.setListener(this);
        }
        mUiPromptDialog.show();
        mUiPromptDialog.setTag(0);
        mUiPromptDialog.setTitle(titleId);
        mUiPromptDialog.setDialogStyle(false);
    }

    /**
     * @param titleId     标题
     * @param cancleId    取消按钮
     * @param determineId 确定按钮
     */
    protected void showCancelAndDetermineDialog(int titleId, int cancleId, int determineId) {
        if (mUiPromptDialog == null) {
            mUiPromptDialog = new UiPromptDialog(this);
            mUiPromptDialog.setListener(this);
        }
        mUiPromptDialog.show();
        mUiPromptDialog.setTag(0);
        mUiPromptDialog.setTitle(titleId);
        mUiPromptDialog.setDialogStyle(false);
    }


    protected void showTagCancelAndDetermineDialog(int titleId, int tag) {
        if (mUiPromptDialog == null) {
            mUiPromptDialog = new UiPromptDialog(this);
            mUiPromptDialog.setListener(this);
        }
        mUiPromptDialog.show();
        mUiPromptDialog.setTag(tag);
        mUiPromptDialog.setTitle(titleId);
        mUiPromptDialog.setDialogStyle(false);
    }


    /**
     * @param titleId     标题
     * @param cancleId    取消按钮
     * @param determineId 确定按钮
     * @param tag         tag 处理分类
     */
    protected void showTagCancelAndDetermineDialog(int titleId, int cancleId, int determineId, int tag) {
        if (mUiPromptDialog == null) {
            mUiPromptDialog = new UiPromptDialog(this);
            mUiPromptDialog.setListener(this);
        }
        mUiPromptDialog.show();
        mUiPromptDialog.setTag(tag);
        mUiPromptDialog.setTitle(titleId);
        mUiPromptDialog.setCancel(cancleId);
        mUiPromptDialog.setConfirm(determineId);
        mUiPromptDialog.setDialogStyle(false);
    }


    //---------------------------------CenterDetermine--------------------------------------------

    /**
     * @param titleId 标题
     */
    protected void showCenterDetermineDialog(int titleId) {
        if (mUiPromptDialog == null) {
            mUiPromptDialog = new UiPromptDialog(this);
            mUiPromptDialog.setListener(this);
        }
        mUiPromptDialog.show();
        mUiPromptDialog.setTag(0);
        mUiPromptDialog.setTitle(titleId);
        mUiPromptDialog.setDialogStyle(true);
    }

    /**
     * @param titleId 标题
     */
    protected void showCenterDetermineDialog(int titleId, int confirmId) {
        if (mUiPromptDialog == null) {
            mUiPromptDialog = new UiPromptDialog(this);
            mUiPromptDialog.setListener(this);
        }
        mUiPromptDialog.show();
        mUiPromptDialog.setTag(0);
        mUiPromptDialog.setTitle(titleId);
        mUiPromptDialog.setCenterConfirm(confirmId);
        mUiPromptDialog.setDialogStyle(true);
    }

    /**
     * @param titleId 标题
     */
    protected void showTagCenterDetermineDialog(int titleId, int tag) {
        if (mUiPromptDialog == null) {
            mUiPromptDialog = new UiPromptDialog(this);
            mUiPromptDialog.setListener(this);
        }
        mUiPromptDialog.show();
        mUiPromptDialog.setTag(tag);
        mUiPromptDialog.setTitle(titleId);
        mUiPromptDialog.setDialogStyle(true);
    }

    /**
     * @param titleId 标题
     */
    protected void showTagCenterDetermineDialog(int titleId, int confirmId, int tag) {
        if (mUiPromptDialog == null) {
            mUiPromptDialog = new UiPromptDialog(this);
            mUiPromptDialog.setListener(this);
        }
        mUiPromptDialog.show();
        mUiPromptDialog.setTag(tag);
        mUiPromptDialog.setTitle(titleId);
        mUiPromptDialog.setCenterConfirm(confirmId);
        mUiPromptDialog.setDialogStyle(true);
    }

    protected BaseActivity getThisActivity() {
        return this;
    }


    //---------------------------------dissmiss--------------------------------------------

    protected void dissMissUiPromptDialog() {
        if (mUiPromptDialog != null && mUiPromptDialog.isShowing()) {
            mUiPromptDialog.dismiss();
        }
    }

    //---------------------------------listener--------------------------------------------
    @Override
    public void onUiCancelListener() {
        dissMissUiPromptDialog();
    }

    @Override
    public void onUiDetermineListener() {
        dissMissUiPromptDialog();
    }

    @Override
    public void onUiCenterDetermineListener() {
        dissMissUiPromptDialog();
    }

    public void jumpTonet() {
        Intent intent = new Intent("android.intent.action.WIFI_ENABLE");
        startActivity(intent);
    }
}