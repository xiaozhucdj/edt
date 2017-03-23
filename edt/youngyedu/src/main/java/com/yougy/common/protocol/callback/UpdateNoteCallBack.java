package com.yougy.common.protocol.callback;

import android.content.Context;

import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.shop.bean.BaseData;

import okhttp3.Response;

/**
 * Created by jiangliang on 2016/12/20.
 */

public class UpdateNoteCallBack extends BaseCallBack<BaseData> {

    public UpdateNoteCallBack(Context context) {
        super(context);
    }

    @Override
    public BaseData parseNetworkResponse(Response response, int id) throws Exception {
        return GsonUtil.fromJson(response.body().string(), BaseData.class);
    }

    @Override
    public void onResponse(BaseData response, int id) {
        if (response.getCode() == 200) {
            RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
            rxBus.send(response);
        }
    }
}
