package com.yougy.common.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.yougy.common.bean.AliyunData;
import com.yougy.common.bean.Result;
import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.protocol.request.AliyunDataReq;
import com.yougy.common.utils.AliyunUtil;
import com.yougy.common.utils.ResultUtils;

import okhttp3.Response;

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

    public UploadService(){
        super("UploadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Response response = NewProtocolManager.queryAliyunData(new AliyunDataReq());
        try {
            if (response.isSuccessful()) {
                String resultJson = response.body().string();
                Result<AliyunData> result = ResultUtils.fromJsonObject(resultJson, AliyunData.class);
                AliyunData data = result.getData();
                AliyunUtil aliyunUtil = new AliyunUtil(data.getAccessKeyId(), data.getAccessKeySecret(), data.getSecurityToken());
                aliyunUtil.upload();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
