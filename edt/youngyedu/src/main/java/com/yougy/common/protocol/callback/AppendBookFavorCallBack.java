package com.yougy.common.protocol.callback;

import android.content.Context;
import android.util.Log;

import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.request.AppendBookFavorRequest;
import com.yougy.common.protocol.response.AppendBookFavorRep;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;

import okhttp3.Response;

/**
 * Created by Administrator on 2017/2/14.
 * 添加收藏的回调函数
 */

public class AppendBookFavorCallBack extends BaseCallBack<AppendBookFavorRep> {
    private final int mProtocol;
    private final AppendBookFavorRequest mRequest;

    public AppendBookFavorCallBack(Context context, int protocol,  AppendBookFavorRequest request) {
        super(context);
        mProtocol = protocol;
        mRequest = request;
    }

    @Override
    public AppendBookFavorRep parseNetworkResponse(Response response, int id) throws Exception {
        String backJson = response.body().string();
        LogUtils.i("response json ...." + backJson);
        return GsonUtil.fromJson(backJson, AppendBookFavorRep.class);
    }

    @Override
    public void onResponse(AppendBookFavorRep response, int id) {
        Log.e("AppendBookFavorCallBack", "send AppendBookFavorRep event");
        RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
        rxBus.send(response);

    }

    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        ProtocolManager.appendBookFavorProtocol(mRequest, mProtocol, this);
    }
}
