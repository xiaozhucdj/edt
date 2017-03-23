package com.yougy.common.protocol.callback;

import android.content.Context;
import android.util.Log;

import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.request.PromoteBookRequest;
import com.yougy.common.protocol.response.PromoteBookProtocol;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;

import okhttp3.Response;

/**
 * Created by Administrator on 2017/2/13.
 * 书城图书推荐
 */

public class PromoteBookCallBack extends BaseCallBack<PromoteBookProtocol> {

    private final int mProtocolId;
    private final PromoteBookRequest mRequset;

    public PromoteBookCallBack(Context context, int protocolId , PromoteBookRequest request) {
        super(context);
        mProtocolId = protocolId ;
        mRequset = request ;
    }

    @Override
    public PromoteBookProtocol parseNetworkResponse(Response response, int id) throws Exception {
        String backJson = response.body().string();
        LogUtils.i("response json ...." + backJson);
        return GsonUtil.fromJson(backJson, PromoteBookProtocol.class);
    }

    @Override
    public void onResponse(PromoteBookProtocol response, int id) {
        Log.e("PromoteBookCallBack", "send PromoteBookCallBack event");
        if (response.getCode() == ProtocolId.RET_SUCCESS){
            RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
            rxBus.send(response);
        }
    }

    @Override
    public void onClick() {
        super.onClick();
        ProtocolManager.promoteBookProtocol(mRequset , mProtocolId ,this);
    }
}
