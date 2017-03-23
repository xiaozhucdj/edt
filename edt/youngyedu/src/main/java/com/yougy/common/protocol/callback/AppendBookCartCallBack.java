package com.yougy.common.protocol.callback;

import android.content.Context;
import android.util.Log;

import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.request.AppendBookCartRequest;
import com.yougy.common.protocol.response.AppendBookCartProtocol;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtil;

import okhttp3.Response;

/**
 * Created by Administrator on 2017/2/13.
 * 添加购物车
 */

public class AppendBookCartCallBack extends BaseCallBack<AppendBookCartProtocol> {
    private final int mProtocol;
    private final AppendBookCartRequest mRequest;

    public AppendBookCartCallBack(Context context, int protocol, AppendBookCartRequest request) {
        super(context);
        mProtocol = protocol;
        mRequest = request;
    }

    @Override
    public AppendBookCartProtocol parseNetworkResponse(Response response, int id) throws Exception {
        String backJson = response.body().string();
        LogUtils.i("response json ...." + backJson);
        return GsonUtil.fromJson(backJson, AppendBookCartProtocol.class);

    }

    @Override
    public void onResponse(AppendBookCartProtocol response, int id) {
        Log.e("PromoteBookCallBack", "send AppendBookCartCallback event");
        RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
        rxBus.send(response);
    }

    @Override
    public void onClick() {
        super.onClick();
        ProtocolManager.appendBookCartProtocol(mRequest, mProtocol, this);
    }
}
