package com.yougy.shop.callback;

import android.content.Context;

import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.callback.BaseCallBack;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.SpUtils;
import com.yougy.shop.bean.QueryBookInfo;

import okhttp3.Response;

/**
 * Created by jiangliang on 2017/2/18.
 */

public class QueryBookCallBack extends BaseCallBack<QueryBookInfo>{

    public QueryBookCallBack(Context context) {
        super(context);
    }

    private String bookName;

    public void setBookName(String bookName){
        this.bookName = bookName;
    }

    @Override
    public QueryBookInfo parseNetworkResponse(Response response, int id) throws Exception {
        String str = response.body().string();
        return GsonUtil.fromJson(str,QueryBookInfo.class);
    }

    @Override
    public void onResponse(QueryBookInfo response, int id) {
        RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
        rxBus.send(response);
    }


    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        ProtocolManager.queryBookProtocol(SpUtils.getUserId(),bookName,-1,0,0, ProtocolId.PROTOCOL_ID_QUERY_BOOK,this);
    }
}
