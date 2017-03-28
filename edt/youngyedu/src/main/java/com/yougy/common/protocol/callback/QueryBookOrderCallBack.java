package com.yougy.common.protocol.callback;

import android.content.Context;
import android.util.Log;

import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.response.OrderBaseResponse;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtil;

import okhttp3.Response;

/**
 * Created by FH on 2017/2/17.
 * 获取订单列表的callBack
 */

public class QueryBookOrderCallBack extends BaseCallBack<OrderBaseResponse> {

    private int mProtocol;

    public QueryBookOrderCallBack(Context context, int protocol) {
        super(context);
        mProtocol =protocol ;
    }

    @Override
    public OrderBaseResponse parseNetworkResponse(Response response, int id) throws Exception {
        String backJson = response.body().string();
        LogUtils.i("response json ...." + backJson);
        return GsonUtil.fromJson(backJson, OrderBaseResponse.class);
    }

    @Override
    public void onResponse(OrderBaseResponse response, int id) {
        Log.e("QueryBookOrderCallBack", "send OrderBaseResponse event");
        RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
        rxBus.send(response);
    }

    @Override
    public void onClick() {
        super.onClick();
        ProtocolManager.fake_queryBookOrderProtocol("11111" , mProtocol, this);
    }
}
