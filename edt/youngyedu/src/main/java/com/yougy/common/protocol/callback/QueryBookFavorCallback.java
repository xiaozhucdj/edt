package com.yougy.common.protocol.callback;

import android.content.Context;
import android.util.Log;

import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.response.QueryBookFavorProtocol;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtil;

import okhttp3.Response;

/**
 * Created by Administrator on 2017/2/14.
 * 查询收藏
 */

public class QueryBookFavorCallback extends BaseCallBack<QueryBookFavorProtocol> {
    private int mProtocol;

    public QueryBookFavorCallback(Context context, int protocol) {
        super(context);
        mProtocol = protocol;
    }

    @Override
    public QueryBookFavorProtocol parseNetworkResponse(Response response, int id) throws Exception {
        String backJson = response.body().string();
        LogUtils.i("response json ...." + backJson);
        return GsonUtil.fromJson(backJson, QueryBookFavorProtocol.class);
    }


    @Override
    public void onResponse(QueryBookFavorProtocol response, int id) {
        Log.e("QueryBookFavorProtocol", "send QueryBookFavorProtocol event");
        RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
        rxBus.send(response);
    }

    @Override
    public void onClick() {
        super.onClick();
        //PROTOCOL_ID_QUERY_BOOK_FAVOR
        ProtocolManager.queryBookFavorProtocol(Integer.parseInt(SpUtil.getAccountId()), mProtocol, this);
    }
}
