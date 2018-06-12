package com.yougy.common.protocol.callback;

import android.content.Context;

import com.yougy.common.activity.BaseActivity;
import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.response.QueryBookOrderListRep;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.shop.activity.OrderListActivity;

import okhttp3.Response;

/**
 * Created by FH on 2017/2/14.
 * 查询订单列表CallBack
 */

public class QueryOrderListCallBack extends BaseCallBack<QueryBookOrderListRep> {
    private int mProtocol;

    public QueryOrderListCallBack(Context context, int protocol) {
        super(context);
        mProtocol = protocol;
    }

    @Override
    public QueryBookOrderListRep parseNetworkResponse(Response response, int id) throws Exception {
        String backJson = response.body().string();
        LogUtils.i("response json ...." + backJson);
        return GsonUtil.fromJson(backJson, QueryBookOrderListRep.class);
    }

    @Override
    public void onResponse(QueryBookOrderListRep response, int id) {
        LogUtils.e("QueryOrderListCallBack", "send QueryBookOrderListRep event");
        RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
        rxBus.send(response);
    }


    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        ProtocolManager.queryBookOrderProtocol(String.valueOf(SpUtils.getAccountId()), null, mProtocol, this);
    }

    @Override
    public void onUiCancelListener() {
        super.onUiCancelListener();
        ((BaseActivity) mContext).finish();
    }
}
