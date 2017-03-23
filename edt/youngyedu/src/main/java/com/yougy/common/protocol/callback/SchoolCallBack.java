package com.yougy.common.protocol.callback;

import android.content.Context;

import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.init.bean.SchoolInfo;

import okhttp3.Response;

/**
 * Created by jiangliang on 2016/12/13.
 */

public class SchoolCallBack extends BaseCallBack<SchoolInfo> {
    public SchoolCallBack(Context context) {
        super(context);
    }
    @Override
    public SchoolInfo parseNetworkResponse(Response response, int id) throws Exception {
        return GsonUtil.fromJson(response.body().string(), SchoolInfo.class);
    }

    @Override
    public void onClick() {
        ProtocolManager.querySchoolProtocol(SpUtil.getSelectAreaId(), "", ProtocolId.PROTOCOL_ID_QUERYSCHOOL, this);
    }

    @Override
    public void onResponse(SchoolInfo response, int id) {
//        if (response.getSchoolList() != null) {
            RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
            rxBus.send(response);
            LogUtils.e("SchoolCallBack","onResponse................");
//        }
    }
}