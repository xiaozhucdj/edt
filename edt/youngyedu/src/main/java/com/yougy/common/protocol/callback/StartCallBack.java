package com.yougy.common.protocol.callback;

import android.content.Context;

import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.callback.BaseCallBack;
import com.yougy.common.protocol.response.LogInProtocol;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.init.bean.UserInfo;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jiangliang on 2016/12/14.
 */

public class StartCallBack extends BaseCallBack<LogInProtocol> {

    @Override
    public void onAfter(int id) {
    }

    @Override
    public void onBefore(Request request, int id) {
    }

    public StartCallBack(Context context) {
        super(context);
    }

    @Override
    public LogInProtocol parseNetworkResponse(Response response, int id) throws Exception {
        String str = response.body().string();
        return GsonUtil.fromJson(str,LogInProtocol.class);
    }

    @Override
    public void onResponse(LogInProtocol response, int id) {
        if (response.getCode() == ProtocolId.RET_SUCCESS) {
            if (response.getUserList() != null && response.getUserList().get(0) != null) {
                UserInfo.User user = response.getUserList().get(0);
                RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
                rxBus.send(user);
            }
        }
    }
}
