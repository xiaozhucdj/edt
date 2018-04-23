package com.yougy.common.protocol.callback;

import android.content.Context;

import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.request.PromoteBookRequest;
import com.yougy.common.protocol.response.PromoteBookRep;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;

import okhttp3.Response;

/**
 * Created by FH on 2017/6/13.
 */

public class PromoteBookCallBack extends BaseCallBack<PromoteBookRep> {
    PromoteBookRequest request;
    public PromoteBookCallBack(Context context , PromoteBookRequest request) {
        super(context);
        this.request = request;
    }

    @Override
    public PromoteBookRep parseNetworkResponse(Response response, int id) throws Exception {
        String backJson = response.body().string();
        LogUtils.i("response json ...." + backJson);
        return GsonUtil.fromJson(backJson, PromoteBookRep.class);
    }

    @Override
    public void onResponse(PromoteBookRep response, int id) {
        LogUtils.e("PromoteBookCallBack", "send PromoteBookRep event");
        RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
        rxBus.send(response);
    }


    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        ProtocolManager.promoteBookProtocol(request, ProtocolId.PROTOCOL_ID_PROMOTE_BOOK,this);
    }
}
