package com.yougy.common.protocol.callback;

import android.content.Context;

import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YoungyApplicationManager;
import com.yougy.common.protocol.request.AppendBookCartRequest;
import com.yougy.common.protocol.response.AppendBookCartRep;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;

import okhttp3.Response;

/**
 * Created by Administrator on 2017/2/13.
 * 添加购物车
 */

public class AppendBookCartCallBack extends BaseCallBack<AppendBookCartRep> {
    private final int mProtocol;
    private final AppendBookCartRequest mRequest;

    public AppendBookCartCallBack(Context context, int protocol, AppendBookCartRequest request) {
        super(context);
        mProtocol = protocol;
        mRequest = request;
    }

    @Override
    public AppendBookCartRep parseNetworkResponse(Response response, int id) throws Exception {
        String backJson = response.body().string();
        LogUtils.i("response json ...." + backJson);
        return GsonUtil.fromJson(backJson, AppendBookCartRep.class);

    }

    @Override
    public void onResponse(AppendBookCartRep response, int id) {
        LogUtils.e("AppendBookCartCallBack", "send AppendBookCartRep event");
        RxBus rxBus = YoungyApplicationManager.getRxBus(mWeakReference.get());
        rxBus.send(response);
    }

    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        ProtocolManager.appendBookCartProtocol(mRequest, mProtocol, this);
    }
}
