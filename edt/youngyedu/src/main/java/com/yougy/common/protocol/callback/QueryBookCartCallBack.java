package com.yougy.common.protocol.callback;

import android.content.Context;
import android.util.Log;

import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.response.QueryBookCartRep;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.shop.activity.ShopCartActivity;

import okhttp3.Response;

/**
 * Created by Administrator on 2017/2/14.
 * 查询购物车
 */

public class QueryBookCartCallBack   extends BaseCallBack<QueryBookCartRep> {

    private int mProtocol;

    public QueryBookCartCallBack(Context context,int protocol) {
        super(context);
        mProtocol =protocol ;
    }

    @Override
    public QueryBookCartRep parseNetworkResponse(Response response, int id) throws Exception {
        String backJson = response.body().string();
        LogUtils.i("response json ...." + backJson);
        return GsonUtil.fromJson(backJson, QueryBookCartRep.class);
    }

    @Override
    public void onResponse(QueryBookCartRep response, int id) {
        Log.e("QueryBookCartCallBack", "send QueryBookCartRep event");
        RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
        rxBus.send(response);
    }


    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        ProtocolManager.queryBookCartProtocol(SpUtils.getAccountId(),  mProtocol, this);
    }

    @Override
    public void onUiCancelListener() {
        super.onUiCancelListener();
        ((ShopCartActivity)mContext).finish();
    }
}
