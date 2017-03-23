package com.yougy.common.protocol.callback;

import android.content.Context;
import android.util.Log;

import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.request.RemoveBookCartRequest;
import com.yougy.common.protocol.response.RemoveBookCartProtocol;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtil;

import okhttp3.Response;

/**
 * Created by Administrator on 2017/2/14.
 * 删除单个 商品
 */

public class RemoveBookCartCallBack extends BaseCallBack<RemoveBookCartProtocol> {
    private int mProtocol;
    private RemoveBookCartRequest mRequest ;
    public RemoveBookCartCallBack(Context context, int protocol, RemoveBookCartRequest request) {
        super(context);
        mProtocol = protocol;
        mRequest = request ;
    }

    @Override
    public RemoveBookCartProtocol parseNetworkResponse(Response response, int id) throws Exception {
        String backJson = response.body().string();
        LogUtils.i("response json ...." + backJson);
        return GsonUtil.fromJson(backJson, RemoveBookCartProtocol.class);
    }

    @Override
    public void onResponse(RemoveBookCartProtocol response, int id) {
        Log.e("RemoveBookCartProtocol", "send RemoveBookCartProtocol event");
        RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
        rxBus.send(response);
    }

    @Override
    public void onClick() {
        super.onClick();
        //PROTOCOL_ID_REMOVE_BOOK_CART
        ProtocolManager.removeBookCartProtocol(mRequest, mProtocol, this);
    }
}
