package com.yougy.common.protocol.callback;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yougy.common.global.Commons;
import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.request.NewBindDeviceReq;
import com.yougy.common.protocol.response.NewBindDeviceRep;
import com.yougy.common.rx.RxBus;
import com.yougy.common.service.DownloadService;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtil;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import static com.yougy.common.utils.AliyunUtil.DATABASE_NAME;
import static com.yougy.common.utils.AliyunUtil.JOURNAL_NAME;


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
            RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
            rxBus.send(response);
        }
    }

    private void downloadDb(){
        Log.v("FH" , "注意这儿:DATABASE_NAME=" + DATABASE_NAME);
        final File dbfile = mWeakReference.get().getDatabasePath(DATABASE_NAME);
        final File journalFile = mWeakReference.get().getDatabasePath(JOURNAL_NAME);
        if (YougyApplicationManager.isWifiAvailable() && !SpUtil.isInit()) {
            Observable.create(new Observable.OnSubscribe<Boolean>() {

                @Override
                public void call(Subscriber<? super Boolean> subscriber) {
                    File parent = dbfile.getParentFile();
                    if (!parent.exists()){
                        parent.mkdir();
                    }
                    Intent intent = new Intent(mWeakReference.get(), DownloadService.class);
                    mWeakReference.get().startService(intent);
//                    boolean downloadDb = FtpUtil.downLoadFile(dbfile.getAbsolutePath(), DATABASE_NAME);
//                    LogUtils.e("BindCallBack","download result is : " + downloadDb);
//                    boolean downloadJournal = FtpUtil.downLoadFile(journalFile.getAbsolutePath(), JOURNAL_NAME);
//                    boolean downResult = downloadDb & downloadJournal;
//                    SpUtil.changeInitFlag(downloadDb);
//                    if (!downloadDb){
//                        Connector.getDatabase();
//                    }
                }
            }).subscribeOn(Schedulers.io()).subscribe();
        }
    }

    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        String uuid = Commons.UUID;
        NewBindDeviceReq deviceReq = new NewBindDeviceReq();
        deviceReq.setDeviceId(uuid);
        NewProtocolManager.bindDevice(deviceReq,this);
    }
}
