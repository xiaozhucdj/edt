package com.yougy.plide;

import android.text.TextUtils;

import com.yougy.common.utils.FileUtils;
import com.yougy.plide.pipe.Ball;


/**
 * Created by FH on 2018/1/12.
 */

public abstract class Downloader {
    protected static String DOWNLOAD_DIR_PATH = FileUtils.getAppFilesDir() + "Plide/";

    protected abstract void forceDownload(String url , String saveFilePath , DownloadListener downloadListener , Ball ball) throws InterruptedException;
    protected abstract void download(String url, boolean mUseCache ,DownloadListener downloadListener , Ball ball) throws InterruptedException;
    protected static String getSavePath(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (url.startsWith("/")) { //TODO 可替换成正则
                return url;
            } else if (url.startsWith("http://")) { //TODO 可替换成正则
                String fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
                if (!TextUtils.isEmpty(fileName)) {
                    return DOWNLOAD_DIR_PATH + fileName;
                }
            }
        }
        return null;
    }
}
