package com.yougy.common.protocol.callback;

import android.content.Context;
import android.provider.Settings;

import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.FtpUtil;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.init.bean.BindInfo;
import com.yougy.init.manager.InitManager;

import org.litepal.tablemanager.Connector;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import static com.yougy.home.UploadService.DATABASE_NAME;
import static com.yougy.home.UploadService.JOURNAL_NAME;

/**
 * Created by jiangliang on 2016/12/14.
 */

public class BindCallBack extends BaseCallBack<BindInfo> {

    public BindCallBack(Context context) {
        super(context);
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        super.onError(call, e, id);
        SpUtil.saveAccountId("-1");
        SpUtil.saveAccountName("");
        SpUtil.saveAccountNumber("");
    }

    @Override
    public BindInfo parseNetworkResponse(Response response, int id) throws Exception {
        String result = response.body().string();
        return GsonUtil.fromJson(result, BindInfo.class);
    }

    @Override
    public void onResponse(BindInfo response, int id) {
        if (response != null) {
            downloadDb();
            RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
            rxBus.send(response);
        }
    }

    private void downloadDb(){
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
                    boolean downloadDb = FtpUtil.downLoadFile(dbfile.getAbsolutePath(), DATABASE_NAME);
                    LogUtils.e("BindCallBack","download result is : " + downloadDb);
//                    boolean downloadJournal = FtpUtil.downLoadFile(journalFile.getAbsolutePath(), JOURNAL_NAME);
//                    boolean downResult = downloadDb & downloadJournal;
                    SpUtil.changeInitFlag(downloadDb);
                    if (!downloadDb){
                        Connector.getDatabase();
                    }
                }
            }).subscribeOn(Schedulers.io()).subscribe();
        }
    }

    @Override
    public void onClick() {
        String uuid = Settings.Secure.getString(mWeakReference.get().getContentResolver(), Settings.Secure.ANDROID_ID);
        ProtocolManager.deviceBindProtocol(InitManager.getInstance().getStudentId(), uuid, ProtocolId.PROTOCOL_ID_DEVICEBIND, this);
    }
}
