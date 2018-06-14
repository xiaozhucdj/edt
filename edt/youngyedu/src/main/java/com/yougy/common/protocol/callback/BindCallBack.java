package com.yougy.common.protocol.callback;

import android.content.Context;
import android.content.Intent;

import com.yougy.common.global.Commons;
import com.yougy.common.manager.YoungyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.request.NewBindDeviceReq;
import com.yougy.common.protocol.response.NewBindDeviceRep;
import com.yougy.common.rx.RxBus;
import com.yougy.common.service.DownloadService;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtils;

import java.io.File;

import okhttp3.Response;
import rx.Observable;
import rx.schedulers.Schedulers;

import static com.yougy.common.utils.AliyunUtil.DATABASE_NAME;


/**
 * Created by jiangliang on 2016/12/14.
 */

public class BindCallBack extends BaseCallBack<NewBindDeviceRep> {

    public BindCallBack(Context context) {
        super(context);
    }

    @Override
    public NewBindDeviceRep parseNetworkResponse(Response response, int id) throws Exception {
        String result = response.body().string();
        LogUtils.e(getClass().getName(),"bind device json is :" + result);
        return GsonUtil.fromJson(result, NewBindDeviceRep.class);
    }

    @Override
    public void onResponse(NewBindDeviceRep response, int id) {
        if (response != null&&response.getCode()== ProtocolId.RET_SUCCESS) {
            downloadDb();
            RxBus rxBus = YoungyApplicationManager.getRxBus(mWeakReference.get());
            rxBus.send(response);
        }
    }

    private void downloadDb(){
        LogUtils.e("FH" , "注意这儿:DATABASE_NAME=" + DATABASE_NAME);
        final File dbfile = mWeakReference.get().getDatabasePath(DATABASE_NAME);
        if (YoungyApplicationManager.isWifiAvailable() && !SpUtils.isInit()) {
            Observable.create((Observable.OnSubscribe<Boolean>) subscriber -> {
                File parent = dbfile.getParentFile();
                if (!parent.exists()){
                    parent.mkdir();
                }
                Intent intent = new Intent(mWeakReference.get(), DownloadService.class);
                mWeakReference.get().startService(intent);
            }).subscribeOn(Schedulers.io()).subscribe();
        }
    }

    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        String uuid = Commons.UUID;
        NewBindDeviceReq deviceReq = new NewBindDeviceReq();
        deviceReq.setDeviceId(uuid);
//        NewProtocolManager.bindDevice(deviceReq,this);
    }
}
