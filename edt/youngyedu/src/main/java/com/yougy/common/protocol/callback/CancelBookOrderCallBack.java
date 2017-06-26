package com.yougy.common.protocol.callback;

import android.content.Context;
import android.util.Log;

import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.response.CancelBookOrderRep;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.shop.activity.ConfirmOrderActivity;

import okhttp3.Response;

/**
 * Created by Administrator on 2017/2/14.
 * 添加收藏的回调函数
 */

public class CancelBookOrderCallBack extends BaseCallBack<CancelBookOrderRep> {
    private final int mProtocol;
    private String orderId;
    private int orderOwner;

    public CancelBookOrderCallBack(Context context, int protocol , String orderId , int orderOwner) {
        super(context);
        mProtocol = protocol;
        this.orderId = orderId;
        this.orderOwner = orderOwner;
    }

    @Override
    public CancelBookOrderRep parseNetworkResponse(Response response, int id) throws Exception {
        String backJson = response.body().string();
        LogUtils.i("response json ...." + backJson);
        return GsonUtil.fromJson(backJson, CancelBookOrderRep.class);
    }

    @Override
    public void onResponse(CancelBookOrderRep response, int id) {
        Log.e("CancelBookOrderCallBack", "send CancelBookOrderRep event");
        RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
        rxBus.send(response);

    }


    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        ProtocolManager.cancelPayOrderProtocol(orderId, orderOwner , mProtocol, this);
    }

    @Override
    public void onUiCancelListener() {
        super.onUiCancelListener();
        ((ConfirmOrderActivity) mContext).finish();
    }
}
