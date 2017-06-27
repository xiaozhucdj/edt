package com.yougy.common.protocol.callback;

import android.content.Context;

import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.request.NewUpdateNoteReq;
import com.yougy.common.protocol.response.NewUpdateNoteRep;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;

import okhttp3.Response;

/**
 * Created by Administrator on 2017/5/22.
 * 更新笔记的回调函数
 */

public class NewUpdaNoteCallBack extends BaseCallBack<NewUpdateNoteRep> {

    private  NewUpdateNoteReq updateNoteReq ;
    public NewUpdaNoteCallBack(Context context , NewUpdateNoteReq req) {
        super(context);
        updateNoteReq = req ;
    }

    @Override
    public NewUpdateNoteRep parseNetworkResponse(Response response, int id) throws Exception {
        String str = response.body().string();
        System.out.println("response json ...." + str);
        return GsonUtil.fromJson(str, NewUpdateNoteRep.class);
    }

    @Override
    public void onResponse(NewUpdateNoteRep response, int id) {
            RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
            rxBus.send(response);
    }


    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        NewProtocolManager.updateNote(updateNoteReq ,this);
    }
}
