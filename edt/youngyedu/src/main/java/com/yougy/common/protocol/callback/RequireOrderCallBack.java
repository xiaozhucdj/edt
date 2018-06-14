package com.yougy.common.protocol.callback;

import android.content.Context;

import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YoungyApplicationManager;
import com.yougy.common.protocol.request.RequirePayOrderRequest;
import com.yougy.common.protocol.response.RequirePayOrderRep;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;

import okhttp3.Response;

/**
 * Created by FH on 2017/2/16.
 * 获取订单信息的回调函数
 */

public class RequireOrderCallBack extends BaseCallBack<RequirePayOrderRep> {
    private final int mProtocol;
    private final RequirePayOrderRequest mRequest;

    public RequireOrderCallBack(Context context, int protocol, RequirePayOrderRequest request) {
        super(context);
        mProtocol = protocol;
        mRequest = request;
    }

    @Override
    public RequirePayOrderRep parseNetworkResponse(Response response, int id) throws Exception {
        String backJson = response.body().string();
        LogUtils.i("response json ...." + backJson);
        return GsonUtil.fromJson(backJson, RequirePayOrderRep.class);
    }

    @Override
    public void onResponse(RequirePayOrderRep response, int id) {
        LogUtils.e("RequireOrderCallBack", "send RequirePayOrderRep event");
        RxBus rxBus = YoungyApplicationManager.getRxBus(mWeakReference.get());
        rxBus.send(response);
    }


    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        ProtocolManager.requirePayOrderProtocol(mRequest, mProtocol, this);
    }
}
