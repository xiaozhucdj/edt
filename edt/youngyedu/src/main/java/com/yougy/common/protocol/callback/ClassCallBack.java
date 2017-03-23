package com.yougy.common.protocol.callback;

import android.content.Context;

import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.callback.BaseCallBack;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.init.bean.ClassInfo;
import com.yougy.init.manager.InitManager;

import okhttp3.Response;

/**
 * Created by jiangliang on 2016/12/14.
 */

public class ClassCallBack extends BaseCallBack<ClassInfo> {

    public ClassCallBack(Context context) {
        super(context);
    }

    @Override
    public ClassInfo parseNetworkResponse(Response response, int id) throws Exception {
        return GsonUtil.fromJson(response.body().string(), ClassInfo.class);
    }

    @Override
    public void onClick() {
        ProtocolManager.queryClassProtocol(InitManager.getInstance().getSchoolId(), "", ProtocolId.PROTOCOL_ID_QUERYCLASS, this);
    }

    @Override
    public void onResponse(ClassInfo response, int id) {
        if (response != null) {
            RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
            rxBus.send(response);
        }
    }
}
