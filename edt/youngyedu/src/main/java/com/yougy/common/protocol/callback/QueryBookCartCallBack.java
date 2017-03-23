package com.yougy.common.protocol.callback;

import android.content.Context;
import android.util.Log;

import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.callback.BaseCallBack;
import com.yougy.common.protocol.response.QueryBookCartProtocol;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtil;

import okhttp3.Response;

/**
 * Created by Administrator on 2017/2/14.
 * 查询购物车
 */

public class QueryBookCartCallBack   extends BaseCallBack<QueryBookCartProtocol> {

    private int mProtocol;

    public QueryBookCartCallBack(Context context,int protocol) {
        super(context);
        mProtocol =protocol ;
    }

    @Override
    public QueryBookCartProtocol parseNetworkResponse(Response response, int id) throws Exception {
        String backJson = response.body().string();
        LogUtils.i("response json ...." + backJson);
        return GsonUtil.fromJson(backJson, QueryBookCartProtocol.class);
    }

    @Override
    public void onResponse(QueryBookCartProtocol response, int id) {
        Log.e("QueryBookCartCallBack", "send QueryBookCartProtocol event");
        RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
        rxBus.send(response);
    }

    @Override
    public void onClick() {
        super.onClick();
        //PROTOCOL_ID_QUERY_BOOK_CART
        ProtocolManager.queryBookCartProtocol(Integer.parseInt(SpUtil.getAccountId()),  mProtocol, this);
    }
}
