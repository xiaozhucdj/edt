package com.yougy.common.protocol.callback;

import android.content.Context;

import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.request.AppendNotesRequest;
import com.yougy.common.protocol.response.AppendNotesProtocol;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;

import okhttp3.Response;

/**
 * Created by jiangliang on 2016/12/15.
 */

public class AppendNotesCallBack extends BaseCallBack<AppendNotesProtocol> {

    private final AppendNotesRequest mRequest;


    public AppendNotesCallBack(Context context, AppendNotesRequest request ) {
        super(context);
         mRequest = request ;
    }


    @Override
    public AppendNotesProtocol parseNetworkResponse(Response response, int id) throws Exception {
        String json = response.body().string() ;
        LogUtils.i("respons add notes json =="+json);
        return GsonUtil.fromJson(json,AppendNotesProtocol.class);
    }

    @Override
    public void onResponse(AppendNotesProtocol response, int id) {
        if (response.getCode() == ProtocolId.RET_SUCCESS) {
            if (response.getData() != null && response.getData().size() > 0
                    && response.getData().get(0) != null
                    && response.getData().get(0).getNoteList() != null && response.getData().get(0).getNoteList().size() > 0) {
                LogUtils.e("AppendCallBack","onResponse................");
                RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
                //返回笔记的id
                rxBus.send(response.getData().get(0).getNoteList().get(0).getNoteId());
            }
        }
    }

    @Override
    public void onClick() {
        super.onClick();
        ProtocolManager.appendNotesProtocol(mRequest, ProtocolId.PROTOCOL_ID_APPEND_NOTES, this);
    }
}
