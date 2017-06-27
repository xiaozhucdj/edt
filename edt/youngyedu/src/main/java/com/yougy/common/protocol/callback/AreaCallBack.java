package com.yougy.common.protocol.callback;

import android.content.Context;

import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.request.NewQueryAreaReq;
import com.yougy.common.protocol.response.NewQueryAreaRep;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;

import okhttp3.Response;

/**
 * Created by jiangliang on 2016/12/13.
 */

public class AreaCallBack extends BaseCallBack<NewQueryAreaRep> {
    public AreaCallBack(Context context) {
        super(context);
    }

    @Override
    public NewQueryAreaRep parseNetworkResponse(Response response, int id) throws Exception {
        return GsonUtil.fromJson(response.body().string(), NewQueryAreaRep.class);
    }

    @Override
    public void onResponse(NewQueryAreaRep response, int id) {
        LogUtils.e("AreaCallBack","response is : " + response);
        RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
        rxBus.send(response);
    }


    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        NewProtocolManager.queryArea(new NewQueryAreaReq(), this);
    }
}