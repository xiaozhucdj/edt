package com.yougy.plide;

import android.widget.ImageView;

import com.yougy.common.utils.LogUtils;


/**
 * Created by FH on 2018/7/10.
 */

public class PlideDownloadRequest extends PlideRequest{
    private boolean mUseCache;

    public PlideDownloadRequest(ImageView imageView, String mUrl, boolean useCache) {
        super(imageView, mUrl);
        this.mUseCache = useCache;
    }

    @Override
    public void run() throws InterruptedException {
        Result result = new Result();
        result.setResultCode(-999);
        getProcessor().getDownloader().download(mUrl, mUseCache , new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String savePath) {
                getProcessor().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callListeners(PlideLoadListener.STATUS.DOWNLOADING , mUrl , -999 , -999 , null , null);
                    }
                });
            }

            @Override
            public void onDownloadProgressChanged(String url, String savePath, float progress) {

            }

            @Override
            public void onDownloadStop(String url, String savePath, int errorCode, String reason) {
                LogUtils.e("下载文件" + url + "失败,reason :" + reason);
                getProcessor().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (errorCode == -1){
                            synchronized (result){
                                result.setResultCode(-2);
                                result.notify();
                            }
                        }
                        else {
                            synchronized (result){
                                result.setResultCode(-1);
                                result.notify();
                            }
                        }
                    }
                });
            }

            @Override
            public void onDownloadFinished(String url, String savePath, boolean noNeedToDownload) {
                synchronized (result){
                    result.setResultCode(0);
                    result.notify();
                }
            }
        } , this);
        synchronized (result){
            if (result.getResultCode() == -999) {
                result.wait();
            }
            if (result.getResultCode() == -2){
                throw new InterruptedException("用户取消下载");
            }
            else if (result.getResultCode() == -1){
                callListeners(PlideLoadListener.STATUS.ERROR, mUrl , -999 , -999 , PlideLoadListener.ERROR_TYPE.DOWNLOAD_ERROR , result.getErrorMsg());
                throw new PlideRunTimeException("下载失败");
            }
            else {
                callListeners(PlideLoadListener.STATUS.DOWNLOAD_SUCCESS , mUrl , -999 , -999 , null , null);
            }
        }
    }

    @Override
    public void onCancelled() {
        getProcessor().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callListeners(PlideLoadListener.STATUS.ERROR, mUrl , -999 , -999 , PlideLoadListener.ERROR_TYPE.USER_CANCLE , "用户取消下载");
            }
        });
    }

    @Override
    public String toString() {
        return "PlideDownloadRequest@" + Integer.toHexString(hashCode()) + " url=" + mUrl;
    }
}
