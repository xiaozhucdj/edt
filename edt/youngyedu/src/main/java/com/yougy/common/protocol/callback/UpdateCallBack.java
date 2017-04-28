package com.yougy.common.protocol.callback;

import android.content.Context;

import com.yougy.common.protocol.response.VersioinProtocol;
import com.yougy.common.utils.GsonUtil;

import okhttp3.Response;

/**
 * Created by jiangliang on 2016/12/14.
 */

public class UpdateCallBack extends BaseCallBack<VersioinProtocol> {

    public UpdateCallBack(Context context) {
        super(context);
    }

    @Override
    public VersioinProtocol parseNetworkResponse(Response response, int id) throws Exception {
        return GsonUtil.fromJson(response.body().string(), VersioinProtocol.class);
    }


    @Override
    public void onClick() {
//        ProtocolManager.queryClassProtocol(InitManager.getInstance().getSchoolId(), "", ProtocolId.PROTOCOL_ID_QUERYCLASS, this);
    }

    @Override
    public void onResponse(VersioinProtocol response, int id) {
    }
}
