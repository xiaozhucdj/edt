package com.yougy.common.protocol.callback;

import android.content.Context;

import com.google.gson.Gson;
import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.response.QueryNoteProtocol;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtil;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 笔记请求
 * Created by jiangliang on 2016/12/15.
 */

public class NoteBookCallBack extends CacheInfoBack<QueryNoteProtocol> {

    private int mTermIndex;
    private int mProtocolId ;

    public NoteBookCallBack(Context context, int protocolId) {
        super(context);
        mProtocolId = protocolId ;
    }

    @Override
    public QueryNoteProtocol parseNetworkResponse(Response response, int id) throws Exception {
        mJson = response.body().string();
        LogUtils.i("note json ===" + mJson);
        operateCacheInfo(id);
        return new Gson().fromJson(mJson, QueryNoteProtocol.class);
    }

    @Override
    public void onResponse(QueryNoteProtocol response, int id) {
        if (response.getCode() == ProtocolId.RET_SUCCESS) {
            if (response.getData() != null && response.getData().size() > 0
                    && response.getData().get(0) != null
                    && response.getData().get(0).getNoteList() != null && response.getData().get(0).getNoteList().size() > 0) {
                RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
                rxBus.send(response);
            }
        }
    }

    @Override
    public void onClick() {
        super.onClick();
        ProtocolManager.queryNotesProtocol(SpUtil.getAccountId(), mTermIndex, 2, mProtocolId, this);
    }

    public String getJson() {
        return mJson;
    }

    public void setTermIndex(int termIndex) {
        mTermIndex = termIndex;
    }

    @Override
    public void onError(Call call, Exception e, int id) {
//        super.onError(call, e, id);
        LogUtils.i("yuanye...请求服务器 加载出错 ---onError");
        if (id != ProtocolId.PROTOCOL_ID_BOOK_SHELF){
            RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
            rxBus.send(id+"");
        }else{
            super.onError(call, e, id);
        }

    }
}
