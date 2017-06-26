package com.yougy.common.protocol.callback;

import android.content.Context;
import android.util.Log;

import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.response.IsOrderPaySuccessRep;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.shop.activity.ConfirmOrderActivity;

import okhttp3.Response;

/**
 * Created by FH on 2017/2/17.
 * 获取订单列表的callBack
 */

public class IsOrderPaySuccessCallBack extends BaseCallBack<IsOrderPaySuccessRep> {
    private String orderId;
    private int orderOwner;
    private int mProtocol;

    public IsOrderPaySuccessCallBack(Context context, String orderId , int orderOwner , int protocol) {
        super(context);
        mProtocol =protocol ;
        this.orderId = orderId;
        this.orderOwner = orderOwner;
    }

    @Override
    public IsOrderPaySuccessRep parseNetworkResponse(Response response, int id) throws Exception {
        String backJson = response.body().string();
        LogUtils.i("response json ...." + backJson);
        return GsonUtil.fromJson(backJson, IsOrderPaySuccessRep.class);
    }

    @Override
    public void onResponse(IsOrderPaySuccessRep response, int id) {
        Log.e("IsOrderPaySuccessCallbk", "send IsOrderPaySuccessRep event");
        RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
        rxBus.send(response);
    }


    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        ProtocolManager.isOrderPaySuccessProtocol(orderId , orderOwner , mProtocol, this);
    }

    @Override
    public void onUiCancelListener() {
        super.onUiCancelListener();
        ((ConfirmOrderActivity) mContext).finish();
    }
}
