package com.yougy.common.protocol.callback;

import android.content.Context;

import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.response.NewQuerySchoolRep;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtil;

import okhttp3.Response;

/**
 * Created by jiangliang on 2016/12/13.
 */

public class SchoolCallBack extends BaseCallBack<NewQuerySchoolRep> {
    public SchoolCallBack(Context context) {
        super(context);
    }
    @Override
    public NewQuerySchoolRep parseNetworkResponse(Response response, int id) throws Exception {
        String json = response.body().string();
        LogUtils.e(getClass().getName(),"school call back json : " + json);
        return GsonUtil.fromJson(json, NewQuerySchoolRep.class);
    }

    @Override
    public void onClick() {
        ProtocolManager.querySchoolProtocol(SpUtil.getSelectAreaId(), "", ProtocolId.PROTOCOL_ID_QUERYSCHOOL, this);
    }

    @Override
    public void onResponse(NewQuerySchoolRep response, int id) {
            RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
            rxBus.send(response);
            LogUtils.e("SchoolCallBack","onResponse................");
    }
}