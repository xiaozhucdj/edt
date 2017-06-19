package com.yougy.common.protocol.callback;

import android.content.Context;
import android.util.Log;

import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.request.RemoveBookFavorRequest;
import com.yougy.common.protocol.response.RemoveBookFavorProtocol;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;

import okhttp3.Response;

/**
 * Created by Administrator on 2017/2/14.
 * 单个条目移除 移除收藏
 */

public class RemoveBookFavorCallBack extends BaseCallBack<RemoveBookFavorProtocol> {
    private final RemoveBookFavorRequest mRequest;
    private int mProtocol;

    public RemoveBookFavorCallBack(Context context, int protocol, RemoveBookFavorRequest request) {
        super(context);
        mRequest = request;
        mProtocol = protocol;
    }

    @Override
    public RemoveBookFavorProtocol parseNetworkResponse(Response response, int id) throws Exception {
        String backJson = response.body().string();
        LogUtils.i("response json ...." + backJson);
        return GsonUtil.fromJson(backJson, RemoveBookFavorProtocol.class);
    }


    @Override
    public void onResponse(RemoveBookFavorProtocol response, int id) {
        Log.e("RemoveBookFavorProtocol", "send RemoveBookFavorProtocol event");
        RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
        rxBus.send(response);
    }

    @Override
    public void onClick() {
        super.onClick();
        //PROTOCOL_ID_REMOVE_BOOK_FAVOR
        ProtocolManager.bookFavorRemoveProtocol(mRequest, mProtocol, this);
    }
}
