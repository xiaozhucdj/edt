package com.yougy.plide;

import android.text.TextUtils;

import com.yougy.common.utils.FileUtils;
import com.yougy.plide.pipe.Ball;

import java.math.BigInteger;
import java.security.MessageDigest;


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
                String fileName = getMD5(url);
                String suffix = url.substring(url.lastIndexOf(".") , url.length());
                if (!TextUtils.isEmpty(fileName)) {
                    return DOWNLOAD_DIR_PATH + fileName + suffix;
                }
            }
        }
        return null;
    }

    /**
     * 对字符串md5加密(小写+字母)
     *
     * @param str 传入要加密的字符串
     * @return  MD5加密后的字符串
     */
    public static String getMD5(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
