package com.yougy.common.protocol.callback;

import android.content.Context;

import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.request.RemoveNotesRequest;
import com.yougy.common.protocol.response.RemoveNoteProtocol;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;

import okhttp3.Response;

/**
 * Created by jiangliang on 2016/12/22.
 */

public class DelteNoteCallBack extends BaseCallBack<RemoveNoteProtocol> {
    RemoveNotesRequest mRequest;
    public DelteNoteCallBack(Context context ,RemoveNotesRequest request) {
        super(context);
        mRequest =  request;
    }

    @Override
    public RemoveNoteProtocol parseNetworkResponse(Response response, int id) throws Exception {
        String str = response.body().string();
        System.out.println("response json ...." + str);
        return GsonUtil.fromJson(str, RemoveNoteProtocol.class);
    }

    @Override
    public void onResponse(RemoveNoteProtocol response, int id) {
        if (response.getCode() == ProtocolId.RET_SUCCESS) {
            RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
            rxBus.send(response);
        }
    }

    @Override
    public void onClick() {
        super.onClick();
        ProtocolManager.removeNotesProtocol(mRequest, ProtocolId.PROTOCOL_ID_REMOVE_NOTES, this);
    }
}
