package com.yougy.common.protocol.callback;

import android.content.Context;

import com.google.gson.Gson;
import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.request.NewQueryNoteReq;
import com.yougy.common.protocol.response.NewQueryNoteRep;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.LogUtils;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/5/19.
 */

public class NewNoteBookCallBack extends CacheInfoBack<NewQueryNoteRep> {
    private NewQueryNoteReq newQueryNoteReq;

    public NewNoteBookCallBack(Context context, NewQueryNoteReq req) {
        super(context);
        newQueryNoteReq = req;
    }

    @Override
    public NewQueryNoteRep parseNetworkResponse(Response response, int id) throws Exception {
        mJson = response.body().string();
        LogUtils.i("note json ===" + mJson);
        NewQueryNoteRep rep =   new Gson().fromJson(mJson, NewQueryNoteRep.class);
        if (rep.getCode() == NewProtocolManager.NewCodeResult.CODE_SUCCESS){
            operateCacheInfo(id);
        }
        return rep ;
    }

    @Override
    public void onResponse(NewQueryNoteRep response, int id) {
            RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
            rxBus.send(response);
    }

    @Override
    public void onClick() {
        super.onClick();
        NewProtocolManager.queryNote(newQueryNoteReq ,this);
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        e.printStackTrace();
        LogUtils.i("onError ....请求获取 笔记失败  ");
        RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
        rxBus.send(id + "");
    }
}
