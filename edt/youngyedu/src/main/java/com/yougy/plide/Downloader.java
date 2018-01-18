package com.yougy.plide;


import com.yougy.plide.pipe.Ball;

/**
 * Created by FH on 2018/1/12.
 */

public interface Downloader {
    public void download(String url, String saveFilePath, DownloadListener downloadListener, Ball ball) throws InterruptedException;
}
