package com.yougy.update;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.File;

/**
 * Created by aaron on 16/5/4.
 * 文件下载管理类
 */
public class DownloadManager {


    public static String DOWN_APP_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/youngyedu/apk/";

    private static Object lockObj = new Object();

    private static ThinDownloadManager thinDownloadManager = null;
    // 通过get方法获取
    public static String apkPath = null;
    public static String downApk = "update.apk";
    public static int downloadId = -1;

    /**
     * 获取下载管理对象
     * @return
     */
    public static com.thin.downloadmanager.DownloadManager getInstance() {
        synchronized (lockObj) {
            if (thinDownloadManager == null) {
                thinDownloadManager = new ThinDownloadManager();
            }
        }
        return thinDownloadManager;
    }



    /**
     * 获取下载apk文件的url
     * @param downLoadUrl
     * @return
     */
    public static DownloadRequest getDownLoadRequest(Context mContext, String downLoadUrl, DownloadStatusListenerV1 downloadStatusListenerV1) {
        Uri downLoadUri = Uri.parse(downLoadUrl);
        Uri destionUri = Uri.parse(getApkPath());
        DownloadRequest downloadRequest = new DownloadRequest(downLoadUri)
                .setRetryPolicy(new DefaultRetryPolicy())
                .setDestinationURI(destionUri).setPriority(DownloadRequest.Priority.HIGH)
                .setDownloadContext(mContext)
                .setStatusListener(downloadStatusListenerV1);

        return downloadRequest;
    }


    /**
     * 获取apk下载路径
     * @return
     */
    public static String getApkPath() {
        if (TextUtils.isEmpty(apkPath)) {
            File file = new File(DOWN_APP_PATH);
            if (!file.exists()) {
                file.mkdirs();
            }
            apkPath = DOWN_APP_PATH + downApk;
        }

        return apkPath;
    }
}
