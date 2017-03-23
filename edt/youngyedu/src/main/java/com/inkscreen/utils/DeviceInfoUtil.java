package com.inkscreen.utils;


import android.content.Context;

/**
 * 获取设备相关信息
 * 
 * @author elanking
 *
 */
public class DeviceInfoUtil {
//	/** 移动联通上网代理 */
//	private static final String CM_PROXY = "10.0.0.172";
//	/** 电信上网代理 */
//	private static final String CT_PROXY = "10.0.0.200";
//
//
//
//
//	/**
//	 *
//	 * @Description:获取手机的串号
//	 */
//	public static String getPhoneIMEI(Context context) {
//		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//		String imei = tm.getDeviceId();
//		return imei == null ? "" : imei;
//	}
//
//
//
//	/**
//	 * 屏幕高度
//	 *
//	 * @param
//	 * @return
//	 */
//	public static int getWindowHeiht() {
//		WindowManager wm = (WindowManager) YougyApplicationManager.getApp().getSystemService(Context.WINDOW_SERVICE);
//
//		return wm.getDefaultDisplay().getHeight();
//	}
//
//	/**
//	 *
//	 * @Description:获取手机的型号
//	 */
//	public static String getPhoneType() {
//		return Build.MODEL;
//	}
//
//	/**
//	 *
//	 * @Description:获取手机操作系统的版本
//	 */
//	public static String getSystemVersion() {
//		return Build.VERSION.RELEASE;
//	}
//
//	/**
//	 *
//	 * @Description:获取手机当前应用版本号
//	 */
//	public static int getVerCode(Context context) {
//		int verName = 0;
//		try {
//			verName = context.getPackageManager().getPackageInfo("com.elanking.mobile.yoomath", 0).versionCode;
//		} catch (Exception e) {
//
//		}
//		return verName;
//	}
//
//	/**
//	 *
//	 * @Description:获取手机当前应用版本名
//	 */
//	public static String getVerName(Context context) {
//		String verName = "";
//		try {
//			verName = context.getPackageManager().getPackageInfo("com.elanking.mobile.yoomath", 0).versionName;
//		} catch (Exception e) {
//
//		}
//		return verName;
//	}
//
//
//
//	public static boolean isAppAlive(Context context, String packageName) {
//		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//		List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
//		for (RunningAppProcessInfo rapi : infos) {
//			if (rapi.processName.equals(packageName))
//				return true;
//		}
//		return false;
//	}
//
//	/**
//	 * 判断应用是否在栈顶，即前台运行
//	 *
//	 * @param context
//	 * @param packageName
//	 * @return
//	 */
//	public static boolean isTopActivity(Context context, String packageName) {
//		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
//		if (tasksInfo.size() > 0) {
//			// 应用程序位于堆栈的顶层
//			if (packageName.equals(tasksInfo.get(0).topActivity.getPackageName())) {
//				return true;
//			}
//		}
//		return false;
//	}




	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

//	/**
//	 * 判断权限是否被关闭
//	 *
//	 * @param context
//	 * @param permission
//	 * @return
//	 */
//	public static boolean checkWriteExternalPermission(Context context, String permission) {
//		// String permission = "android.permission.WRITE_EXTERNAL_STORAGE";
//		// //你要判断的权限名字
//		boolean result = true;
//
//		if (Build.VERSION.SDK_INT >= 23) {
//			result = PermissionChecker.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
//
//		}
//		return result;
//	}
}
