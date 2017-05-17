package com.yougy.common.protocol.callback;

import android.content.Context;

import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.response.NewQueryAreaRep;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.init.bean.AreaInfo;

import okhttp3.Response;

/**
 * Created by jiangliang on 2016/12/13.
 */

public class AreaCallBack extends BaseCallBack<AreaInfo> {
    public AreaCallBack(Context context) {
        super(context);
    }

    @Override
    public AreaInfo parseNetworkResponse(Response response, int id) throws Exception {
        String result = response.body().string();
        LogUtils.e(getClass().getName(),"Area result is : " + result);
        NewQueryAreaRep
        return GsonUtil.fromJson(result, AreaInfo.class);
    }

    @Override
    public void onResponse(AreaInfo response, int id) {
        LogUtils.e("AreaCallBack","response is : " + response);
        AreaInfo.Area area = response.getArea();
        RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
        rxBus.send(area);
    }

    @Override
    public void onClick() {
//        ProtocolManager.queryAreaProtocol(-1, "", -1, -1, ProtocolId.PROTOCOL_ID_QUERYAREA, this);
    }
}