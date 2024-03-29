package com.yougy.common.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yougy.common.activity.BaseActivity;
import com.yougy.common.manager.YoungyApplicationManager;
import com.yougy.view.Toaster;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by lipan on 2014/6/19.
 */
public class UIUtils {
    /**
     * 获取Context
     */
    public static Context getContext() {
        return YoungyApplicationManager.getApplication();
    }

    /**
     * 获取主线程id
     */
    public static int getMainThreadId() {
        return YoungyApplicationManager.getMainThreadId();
    }

    /**
     * 获取主线程
     */
    public static Thread getMainThread() {
        return YoungyApplicationManager.getMainThread();
    }

    /**
     * 判断是否运行在主线程
     */
    public static boolean isRunInMainThread() {
        return Thread.currentThread().getId() == getMainThreadId();
    }

    /**
     * 获取主线程的handler
     */
    public static Handler getMainThreadHandler() {
        return YoungyApplicationManager.getMainThreadHandler();
    }

    /**
     * 获取主线程的looper
     */
    public static Looper getMainThreadLooper() {
        return YoungyApplicationManager.getMainThreadLooper();
    }

    /**
     * 把任务post主线程中
     */
    public static boolean post(Runnable runnable) {
        boolean result = false;
        Handler handler = getMainThreadHandler();
        if (handler != null) {
            result = handler.post(runnable);
        }
        return result;
    }

    /**
     * 把任务延迟post到主线程中
     */
    public static boolean postDelayed(Runnable runnable, long delay) {
        boolean result = false;
        Handler handler = getMainThreadHandler();
        if (handler != null) {
            result = handler.postDelayed(runnable, delay);
        }
        return result;
    }

    /**
     * 把任务post到主线程的消息队列最前面
     */
    public static boolean postAtFrontOfQueue(Runnable runnable) {
        boolean result = false;
        Handler handler = getMainThreadHandler();
        if (handler != null) {
            result = handler.postAtFrontOfQueue(runnable);
        }
        return result;
    }

    /**
     * 把任务从主线程的消息队列中删除
     */
    public static void removeCallbacks(Runnable runnable) {
        Handler handler = getMainThreadHandler();
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }

    /**
     * 在主线程执行
     */
    public static void runInMainThread(Runnable runnable) {
        if (isRunInMainThread()) {
            runnable.run();
        } else {
            post(runnable);
        }
    }

    /**
     * 获取应用资源对象
     */
    public static Resources getResources() {
        if (getContext() != null) {
            return getContext().getResources();
        } else {
            return null;
        }
    }

    /**
     * dip转换px
     */
    public static int dip2px(int dip) {
        if (getResources() != null) {
            final float scale = getResources().getDisplayMetrics().density;
            return (int) (dip * scale + 0.5f);
        } else {
            return 0;
        }
    }

    /**
     * px转换dip
     */
    public static int px2dip(int px) {
        if (getResources() != null) {
            final float scale = getResources().getDisplayMetrics().density;
            return (int) (px / scale + 0.5f);
        } else {
            return 0;
        }
    }

    /**
     * sp转px
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取dimen值
     */
    public static int getDimens(int resId) {
        if (getResources() != null) {
            return getResources().getDimensionPixelSize(resId);
        } else {
            return 0;
        }
    }

    /**
     * 获取图片
     */
    public static Drawable getDrawable(int resId) {
        if (getResources() != null) {
            return getResources().getDrawable(resId);
        } else {
            return null;
        }
    }

    /**
     * 获取图片
     */
    public static Bitmap getBitmap(int resId) {
        if (getResources() != null) {
            return BitmapFactory.decodeResource(getResources(), resId);
        } else {
            return null;
        }
    }

    /**
     * 获取颜色
     */
    public static int getColor(int resId) {
        if (getResources() != null) {
            return getResources().getColor(resId);
        } else {
            return 0;
        }
    }

    /**
     * 获取颜色选择器
     */
    public static ColorStateList getColorStateList(int resId) {
        if (getResources() != null) {
            return getResources().getColorStateList(resId);
        } else {
            return null;
        }
    }

    /**
     * 获取文字
     */
    public static String getString(int resId) {
        if (getResources() != null) {
            return getResources().getString(resId);
        } else {
            return null;
        }
    }

    /**
     * 获取文字，并按照后面的参数进行格式化
     */
    public static String getString(int resId, Object... formatAgrs) {
        if (getResources() != null) {
            return getResources().getString(resId, formatAgrs);
        } else {
            return null;
        }
    }

    /**
     * 根据指定的layout索引，创建一个View
     *
     * @param resId 指定的layout索引
     * @return 新的View
     */
    public static View inflate(int resId) {
        Context context = UIUtils.getContext();
        if (context != null) {
            return LayoutInflater.from(context).inflate(resId, null);
        }
        return null;
    }

    /**
     * 对toast的简易封装。线程安全，可以在非UI线程调用。
     *
     * @param resId    Toast内容的资源id
     * @param duration Toast的持续时间
     */
    public static void showToastSafe(final int resId, final int duration) {
        if (Process.myTid() == getMainThreadId()) {
            // 调用在UI线程
            if (BaseActivity.getForegroundActivity() != null) {
                Toaster.showDefaultToast(BaseActivity.getForegroundActivity(), resId, duration);
            }
        } else {
            // 调用在非UI线程
            post(new Runnable() {
                @Override
                public void run() {
                    if (BaseActivity.getForegroundActivity() != null) {
                        Toaster.showDefaultToast(BaseActivity.getForegroundActivity(), resId, duration);
                    }
                }
            });
        }
    }





    /**
     * 对toast的简易封装。线程安全，可以在非UI线程调用。
     * @param resId    Toast内容的资源id
     */
    public static void showToastSafe(final int resId) {
        if (Process.myTid() == getMainThreadId()) {
            // 调用在UI线程
            if (BaseActivity.getForegroundActivity() != null) {
                Toaster.showDefaultToast(BaseActivity.getForegroundActivity(), resId, Toast.LENGTH_SHORT);
            }
        } else {
            // 调用在非UI线程
            post(new Runnable() {
                @Override
                public void run() {
                    if (BaseActivity.getForegroundActivity() != null) {
                        Toaster.showDefaultToast(BaseActivity.getForegroundActivity(), resId, Toast.LENGTH_SHORT);
                    }
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
    public static void showToastSafe(final CharSequence text, final int duration) {
        if (Process.myTid() == getMainThreadId()) {
            // 调用在UI线程
            if (BaseActivity.getForegroundActivity() != null) {
                Toaster.showDefaultToast(getContext(), text, duration);
            }
        } else {
            // 调用在非UI线程
            post(new Runnable() {
                @Override
                public void run() {
                    if (BaseActivity.getForegroundActivity() != null) {
                        Toaster.showDefaultToast(BaseActivity.getForegroundActivity(), text, duration);
                    }
                }
            });
        }
    }
    /**
     * 对toast的简易封装。线程安全，可以在非UI线程调用。
     *
     * @param text     Toast内容
     */
    public static void showToastSafe(final CharSequence text) {
        showToastSafe(text , Toast.LENGTH_SHORT);
    }

    public static int getScreenWidth() {
        BaseActivity activity = BaseActivity.getCurrentActivity();
        if (activity != null) {
            DisplayMetrics metric = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
            return metric.widthPixels;     // 屏幕宽度（像素）
        }
        return 0;
    }

    public static int getScreenHeight() {
        BaseActivity activity =BaseActivity.getCurrentActivity();
        if (activity != null) {
            DisplayMetrics metric = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
            return metric.heightPixels;     // 屏幕高度（像素）
        }
        return 0;
    }

    public static DisplayMetrics getDisplayMetrics() {
        BaseActivity activity = BaseActivity.getCurrentActivity();
        if (activity != null) {
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            return dm;
        }
        return null;
    }

    public static int getStatusBarHeight() {
        int statusHeight = 0;
        BaseActivity activity = BaseActivity.getForegroundActivity();
        if (activity == null) {
            return statusHeight;
        }

        Rect localRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = activity.getResources().getDimensionPixelSize(i5);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    /***
     * 测量 控件的  宽高 ，主要是在getMeasured==0使用 ，
     *  == 0的场景 在oncreat方法
     * @param view
     * @return
     */
    public static int [] getViewWidthAndHeight( View view ) {
        int [] result = new int[2] ;

        int w  = view.getMeasuredWidth() ;
        int h  = view.getMeasuredHeight() ;

        if (w == 0 || h == 0){
            view.measure(View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED),View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED));
            w  = view.getMeasuredWidth() ;
            h  = view.getMeasuredHeight() ;
        }

//        System.out.println("w =="+w);
//        System.out.println("h =="+h) ;
        result[0] = w;
        result[1] = h;
        return  result ;
    }

    /**
     * 递归的使用AutoUtils递归根View下所有的view
     * @param rootView
     */
    public static void recursiveAuto(View rootView){
        if (rootView instanceof ViewGroup){
            for (int i = 0 ; i < ((ViewGroup) rootView).getChildCount() ; i++){
                View childView = ((ViewGroup) rootView).getChildAt(i);
                recursiveAuto(childView);
            }
            AutoUtils.auto(rootView);
        }
        else {
            AutoUtils.auto(rootView);
        }
    }

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * Generate a value suitable for use in {@link View#setId(int)}.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    public static String getTopActivityName(Context context) {
        ActivityManager manager = ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE));
        ActivityManager.RunningTaskInfo runningTaskInfo = manager.getRunningTasks(1).get(0);
        String topActivityName = "";
        if (runningTaskInfo!=null) {
            topActivityName = runningTaskInfo.topActivity.getClassName();
        }
        return topActivityName;
    }

}
