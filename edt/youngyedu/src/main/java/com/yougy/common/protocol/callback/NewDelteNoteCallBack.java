package com.yougy.common.protocol.callback;

import android.content.Context;

import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.request.NewDeleteNoteReq;
import com.yougy.common.protocol.response.NewDeleteNoteRep;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;

import okhttp3.Response;

/**
 * Created by Administrator on 2017/5/22.
 */

public class NewDelteNoteCallBack   extends BaseCallBack<NewDeleteNoteRep>{

    private NewDeleteNoteReq deleteNoteRep ;
    public NewDelteNoteCallBack(Context context ,NewDeleteNoteReq rep) {
        super(context);
        deleteNoteRep = rep ;
    }

    @Override
    public NewDeleteNoteRep parseNetworkResponse(Response response, int id) throws Exception {
        String str = response.body().string();
        System.out.println("response json ...." + str);
        return GsonUtil.fromJson(str, NewDeleteNoteRep.class);
    }

    @Override
    public void onResponse(NewDeleteNoteRep response, int id) {
            RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
            rxBus.send(response);
        }

    @Override
    public void onClick() {
        super.onClick();
        NewProtocolManager.deleteNote(deleteNoteRep,this);
    }
}
