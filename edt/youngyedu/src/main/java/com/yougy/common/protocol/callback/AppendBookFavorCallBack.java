package com.yougy.common.protocol.callback;

import android.content.Context;
import android.util.Log;

import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.request.AppendBookFavorRequest;
import com.yougy.common.protocol.response.AppendBookCartProtocol;
import com.yougy.common.protocol.response.AppendBookFavorProtocol;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtil;

import okhttp3.Response;

/**
 * Created by Administrator on 2017/2/14.
 * 添加收藏的回调函数
 */

public class AppendBookFavorCallBack extends BaseCallBack<AppendBookFavorProtocol> {
    private final int mProtocol;
    private final AppendBookFavorRequest mRequest;

    public AppendBookFavorCallBack(Context context, int protocol,  AppendBookFavorRequest request) {
        super(context);
        mProtocol = protocol;
        mRequest = request;
    }

    @Override
    public AppendBookFavorProtocol parseNetworkResponse(Response response, int id) throws Exception {
        String backJson = response.body().string();
        LogUtils.i("response json ...." + backJson);
        return GsonUtil.fromJson(backJson, AppendBookFavorProtocol.class);
    }

    @Override
    public void onResponse(AppendBookFavorProtocol response, int id) {
        Log.e("PromoteBookCallBack", "send AppendBookCartCallback event");
        RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
        rxBus.send(response);

    }

    @Override
    public void onClick() {
        super.onClick();
        ProtocolManager.bookFavorAppendProtocol(mRequest, mProtocol, this);
    }
}
