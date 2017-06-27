package com.yougy.common.protocol.callback;

import android.content.Context;

import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.request.NewQuerySchoolOrgReq;
import com.yougy.common.protocol.response.NewQuerySchoolOrgRep;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.init.manager.InitManager;

import okhttp3.Response;

/**
 * Created by jiangliang on 2016/12/14.
 */

public class ClassCallBack extends BaseCallBack<NewQuerySchoolOrgRep> {

    public ClassCallBack(Context context) {
        super(context);
    }

    @Override
    public NewQuerySchoolOrgRep parseNetworkResponse(Response response, int id) throws Exception {
        String json = response.body().string();
        LogUtils.e(getClass().getName(),"class json : " + json);
//        return GsonUtil.fromJson(json, ClassInfo.class);
        return GsonUtil.fromJson(json, NewQuerySchoolOrgRep.class);
    }

    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        NewQuerySchoolOrgReq schoolOrgReq = new NewQuerySchoolOrgReq();
        schoolOrgReq.setSchoolId(InitManager.getInstance().getSchoolId());
        NewProtocolManager.querySchoolOrg(schoolOrgReq,this);
    }

    @Override
    public void onResponse(NewQuerySchoolOrgRep response, int id) {
        if (response != null) {
            RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
            rxBus.send(response);
        }
    }
}
