package com.yougy.common.protocol.callback;

import android.content.Context;

import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.request.NewInserAllNoteReq;
import com.yougy.common.protocol.response.NewInserAllNoteRep;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;

import okhttp3.Response;

/**
 * Created by Administrator on 2017/5/22.
 */

public class NewAppendNotesCallBack extends BaseCallBack<NewInserAllNoteRep> {

    private NewInserAllNoteReq newInserAllNoteReq ;
    public NewAppendNotesCallBack(Context context,NewInserAllNoteReq req) {
        super(context);
        newInserAllNoteReq = req ;
    }

    @Override
    public NewInserAllNoteRep parseNetworkResponse(Response response, int id) throws Exception {
        String json = response.body().string() ;
        LogUtils.i("respons add notes json =="+json);
        return GsonUtil.fromJson(json,NewInserAllNoteRep.class);
    }

    @Override
    public void onResponse(NewInserAllNoteRep response, int id) {
        RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
        rxBus.send(response);
    }

    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        NewProtocolManager.inserAllNote(newInserAllNoteReq, this);
    }
}
