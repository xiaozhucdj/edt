package com.yougy.common.protocol.callback;

import android.content.Context;

import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.response.NewLoginRep;
import com.yougy.common.protocol.response.NewQueryStudentRep;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jiangliang on 2016/12/14.
 */

public class StartCallBack extends BaseCallBack<NewLoginRep> {

    @Override
    public void onAfter(int id) {
    }

    @Override
    public void onBefore(Request request, int id) {
    }

    public StartCallBack(Context context) {
        super(context);
    }

    @Override
    public NewLoginRep parseNetworkResponse(Response response, int id) throws Exception {
        String str = response.body().string();
        LogUtils.e(getClass().getName(),"login json is : " + str);
        return GsonUtil.fromJson(str,NewLoginRep.class);
    }

    @Override
    public void onResponse(NewLoginRep response, int id) {
        if (response.getCode() == ProtocolId.RET_SUCCESS) {
            if (response.getCount()>0) {
                NewQueryStudentRep.User user = response.getData().get(0);
                RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
                rxBus.send(user);
            }
        }
    }
}
