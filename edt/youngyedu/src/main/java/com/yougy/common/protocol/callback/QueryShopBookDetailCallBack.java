package com.yougy.common.protocol.callback;

import android.content.Context;

import com.yougy.common.activity.BaseActivity;
import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.response.QueryShopBookDetailRep;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.SpUtil;

import okhttp3.Response;

/**
 * Created by FH on 2017/6/9.
 */

public class QueryShopBookDetailCallBack extends BaseCallBack<QueryShopBookDetailRep>{

    private int bookId;

    public QueryShopBookDetailCallBack(Context context , int bookId) {
        super(context);
        this.bookId = bookId;
    }

    public int getBookId() {
        return bookId;
    }

    public QueryShopBookDetailCallBack setBookId(int bookId) {
        this.bookId = bookId;
        return this;
    }

    @Override
    public QueryShopBookDetailRep parseNetworkResponse(Response response, int id) throws Exception {
        String str = response.body().string();
        return GsonUtil.fromJson(str,QueryShopBookDetailRep.class);
    }

    @Override
    public void onResponse(QueryShopBookDetailRep response, int id) {
        RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
        rxBus.send(response);
    }

    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        ProtocolManager.queryShopBookDetailByIdProtocol(SpUtil.getUserId(), bookId , ProtocolId.PROTOCOL_ID_QUERY_SHOP_BOOK_DETAIL , this);
    }

    @Override
    public void onUiCancelListener() {
        super.onUiCancelListener();
        ( (BaseActivity)mContext).finish();
    }
}
