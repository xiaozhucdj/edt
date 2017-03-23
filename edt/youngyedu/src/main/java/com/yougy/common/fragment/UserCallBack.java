package com.yougy.common.fragment;

import android.content.Context;

import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.callback.BaseCallBack;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.init.bean.UserInfo;

import okhttp3.Response;

/**
 * Created by jiangliang on 2016/12/14.
 */

public class UserCallBack extends BaseCallBack<UserInfo> {

    public UserCallBack(Context context) {
        super(context);
    }

    @Override
    public UserInfo parseNetworkResponse(Response response, int id) throws Exception {
        String result = response.body().string();
        LogUtils.e("UserCallBack","result is : " + result);
        return GsonUtil.fromJson(result, UserInfo.class);
    }

    @Override
    public void onResponse(UserInfo response, int id) {
        if (response != null) {
            RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
            rxBus.send(response);
        }
    }
}
