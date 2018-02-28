package com.yougy.plide;

/**
 * Created by FH on 2018/1/12.
 */

public interface DownloadListener{
    void onDownloadStart(String url, String savePath);
    void onDownloadProgressChanged(String url, String savePath, float progress);
    void onDownloadStop(String url, String savePath, int errorCode, String reason);
    void onDownloadFinished(String url, String savePath, boolean noNeedToDownload);
}
