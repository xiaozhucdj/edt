package com.yougy.common.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.AliyunUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;


/**
 * Created by jiangliang on 2017/4/24.
 */

public class DownloadService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DownloadService(String name) {
        super(name);
    }

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (NetUtils.isNetConnected()) {
            NetWorkManager.queryDownloadAliyunData().subscribe(data -> {
                AliyunUtil aliyunUtil = new AliyunUtil(data);
                aliyunUtil.download();
            }, throwable -> {
                LogUtils.e("zhangyc queryDownloadAliyunData SocketTimeoutException....");
                throwable.printStackTrace();
            });
        }
    }
}
