package com.yougy.common.protocol.callback;

import android.content.Context;

import com.yougy.common.protocol.response.NewGetAppVersionRep;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/5/16.
 */

public class NewUpdateCallBack extends BaseCallBack<NewGetAppVersionRep>{

    public NewUpdateCallBack(Context context) {
        super(context);
    }

    @Override
    public void onBefore(Request request, int id) {
    }

    @Override
    public void onAfter(int id) {
    }

    @Override
    public NewGetAppVersionRep parseNetworkResponse(Response response, int id) throws Exception {
        String str = response.body().string() ;
        LogUtils.i("str =="+str);
      return   GsonUtil.fromJson(str, NewGetAppVersionRep.class);
    }

    @Override
    public void onResponse(NewGetAppVersionRep response, int id) {

    }
}
