package com.yougy.common.fragment;

import android.content.Context;

import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.callback.BaseCallBack;
import com.yougy.common.protocol.response.NewQueryStudentRep;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;

import okhttp3.Response;

/**
 * Created by jiangliang on 2016/12/14.
 */

public class UserCallBack extends BaseCallBack<NewQueryStudentRep> {

    public UserCallBack(Context context) {
        super(context);
    }

    @Override
    public NewQueryStudentRep parseNetworkResponse(Response response, int id) throws Exception {
        String result = response.body().string();
        LogUtils.e("UserCallBack","result is : " + result);
        return GsonUtil.fromJson(result, NewQueryStudentRep.class);
    }

    @Override
    public void onResponse(NewQueryStudentRep response, int id) {
        if (response != null) {
            RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
            rxBus.send(response);
        }
    }
}
