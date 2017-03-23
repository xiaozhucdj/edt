package com.yougy.common.protocol.callback;

import android.content.Context;

import com.yougy.common.global.Commons;
import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.FtpUtil;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.home.bean.StatusInfo;

import okhttp3.Response;

import static com.yougy.home.UploadService.DATABASE_NAME;

/**
 * Created by jiangliang on 2016/12/29.
 */

public class UnBindCallback extends BaseCallBack<StatusInfo> {

    public UnBindCallback(Context context) {
        super(context);
    }

    @Override
    public StatusInfo parseNetworkResponse(Response response, int id) throws Exception {
        if (response.isSuccessful()){
            String path = mWeakReference.get().getDatabasePath(DATABASE_NAME).getAbsolutePath();
            boolean uploadDb = FtpUtil.uploadFile(path, DATABASE_NAME);
            if (uploadDb) {
                SpUtil.changeContent(false);
            }
        }
        return GsonUtil.fromJson(response.body().string(),StatusInfo.class);
    }

    @Override
    public void onResponse(StatusInfo response, int id) {
        LogUtils.e("AccountSetActivity", "response is : " + response.getCode());
        if (response.getCode() == ProtocolId.RET_SUCCESS) {
            RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
            rxBus.send(response);
        }
    }

    @Override
    public void onClick() {
        super.onClick();
        ProtocolManager.deviceUnBindProtocol(Commons.UUID, ProtocolId.PROTOCOL_ID_UNBIND_DEVICE, this);
    }
}
