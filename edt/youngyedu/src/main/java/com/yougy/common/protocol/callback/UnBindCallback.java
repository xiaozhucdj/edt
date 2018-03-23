package com.yougy.common.protocol.callback;

import android.content.Context;
import android.content.Intent;

import com.yougy.common.global.Commons;
import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.request.NewUnBindDeviceReq;
import com.yougy.common.protocol.response.NewUnBindDeviceRep;
import com.yougy.common.rx.RxBus;
import com.yougy.common.service.UploadService;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtils;

import okhttp3.Response;

/**
 * Created by jiangliang on 2016/12/29.
 */

public class UnBindCallback extends BaseCallBack<NewUnBindDeviceRep> {

    public UnBindCallback(Context context) {
        super(context);
    }

    @Override
    public NewUnBindDeviceRep parseNetworkResponse(Response response, int id) throws Exception {
        String json = response.body().string();
        LogUtils.e(getClass().getName(), "unbind json is : " + json);
        if (response.isSuccessful()) {
//            String path = mWeakReference.get().getDatabasePath(DATABASE_NAME).getAbsolutePath();
//            boolean uploadDb = FtpUtil.uploadFile(path, DATABASE_NAME);
//            if (uploadDb) {
//                SpUtils.changeContent(false);
//            }
            Intent intent = new Intent(mWeakReference.get(), UploadService.class);
            mWeakReference.get().startService(intent);
        }
        return GsonUtil.fromJson(json, NewUnBindDeviceRep.class);
    }

    @Override
    public void onResponse(NewUnBindDeviceRep response, int id) {
        LogUtils.e("AccountSetActivity", "response is : " + response.getCode());
        if (response.getCode() == ProtocolId.RET_SUCCESS) {
            RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
            rxBus.send(response);
        }
    }


    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        NewUnBindDeviceReq unBindDeviceReq = new NewUnBindDeviceReq();
        unBindDeviceReq.setDeviceId(Commons.UUID);
        unBindDeviceReq.setUserId(SpUtils.getUserId());
        NewProtocolManager.unbindDevice(unBindDeviceReq, this);
    }
}
