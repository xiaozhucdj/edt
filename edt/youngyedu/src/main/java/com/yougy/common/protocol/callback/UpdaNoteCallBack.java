package com.yougy.common.protocol.callback;

import android.content.Context;

import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.request.UpdateNotesRequest;
import com.yougy.common.protocol.response.UpdaNoteProtocol;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;

import okhttp3.Response;

/**
 * Created by jiangliang on 2016/12/22.
 */

public class UpdaNoteCallBack extends BaseCallBack<UpdaNoteProtocol> {
    private UpdateNotesRequest mRequest;

    public UpdaNoteCallBack(Context context, UpdateNotesRequest request) {
        super(context);
        mRequest = request;
    }

    @Override
    public UpdaNoteProtocol parseNetworkResponse(Response response, int id) throws Exception {
        String str = response.body().string();
        System.out.println("response json ...." + str);
        return GsonUtil.fromJson(str, UpdaNoteProtocol.class);
    }

    @Override
    public void onResponse(UpdaNoteProtocol response, int id) {
        if (response.getCode() == ProtocolId.RET_SUCCESS) {
            RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
            rxBus.send(response);
        }
    }

    @Override
    public void onClick() {
        super.onClick();
        ProtocolManager.updateNotesProtocol(mRequest
                , ProtocolId.PROTOCOL_ID_UPDATE_NOTES, this);
    }

}
