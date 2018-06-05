package com.yougy.common.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.yougy.common.bean.AliyunData;
import com.yougy.common.bean.Result;
import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.protocol.request.AliyunDataUploadReq;
import com.yougy.common.utils.AliyunUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.ResultUtils;

import okhttp3.Response;
import rx.functions.Action1;

/**
 * Created by jiangliang on 2017/4/24.
 */

public class UploadService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public UploadService(String name) {
        super(name);
    }

    public UploadService() {
        super("UploadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        NetWorkManager.queryUploadAliyunData().subscribe(data -> {
            AliyunUtil aliyunUtil = new AliyunUtil(data);
            aliyunUtil.asyncUpload();
        });
    }
}
