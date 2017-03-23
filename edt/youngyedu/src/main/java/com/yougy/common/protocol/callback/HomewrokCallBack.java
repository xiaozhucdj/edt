package com.yougy.common.protocol.callback;

import android.content.Context;

import com.google.gson.Gson;
import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.response.QueryHomewrokProtocol;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtil;

import okhttp3.Response;

/**
 * 笔记请求
 * Created by jiangliang on 2016/12/15.
 */

public class HomewrokCallBack extends CacheInfoBack<QueryHomewrokProtocol> {

    private int mProtocolId ;

    public HomewrokCallBack(Context context, int protocolId) {
        super(context);
        mProtocolId = protocolId ;
    }

    @Override
    public QueryHomewrokProtocol parseNetworkResponse(Response response, int id) throws Exception {
        mJson = response.body().string();
        LogUtils.i("note json ===" + mJson);
        operateCacheInfo(id);
        return new Gson().fromJson(mJson, QueryHomewrokProtocol.class);
    }

    @Override
    public void onResponse(QueryHomewrokProtocol response, int id) {
        if (response.getCode() == ProtocolId.RET_SUCCESS) {
            if (response.getData() != null && response.getData().size() > 0
                    && response.getData().get(0) != null
                    && response.getData().get(0).getHomeworkList() != null && response.getData().get(0).getHomeworkList().size() > 0) {
                RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
                rxBus.send(response);
            }
        }
    }

    @Override
    public void onClick() {
        super.onClick();
        ProtocolManager.queryHomeWrokProtocol(Integer.parseInt(SpUtil.getAccountId()), mProtocolId, this);
    }

    public String getJson() {
        return mJson;
    }


}
