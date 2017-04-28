package com.yougy.common.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.yougy.common.utils.AliyunUtil;

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

    public DownloadService(){
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        AliyunUtil.getInstance().download();
    }
}
