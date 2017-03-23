package com.yougy.common.protocol.callback;

import android.content.Context;
import android.util.Log;

import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.request.QueryQRStrRequest;
import com.yougy.common.protocol.response.QueryQRStrProtocol;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;

import okhttp3.Response;

/**
 * Created by FH on 2017/2/17.
 * 获取生成支付二维码的str的callBack
 */

public class QueryQRStrCallBack extends BaseCallBack<QueryQRStrProtocol> {

    private int mProtocol;
    private QueryQRStrRequest mRequest ;
    public QueryQRStrCallBack(Context context, int protocol , QueryQRStrRequest request) {
        super(context);
        mProtocol =protocol ;
        mRequest = request;
    }

    @Override
    public QueryQRStrProtocol parseNetworkResponse(Response response, int id) throws Exception {
        String backJson = response.body().string();
        LogUtils.i("response json ...." + backJson);
        return GsonUtil.fromJson(backJson, QueryQRStrProtocol.class);
    }

    @Override
    public void onResponse(QueryQRStrProtocol response, int id) {
        Log.e("QueryBookCartCallBack", "send QueryBookCartProtocol event");
        RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
        rxBus.send(response);
    }

    @Override
    public void onClick() {
        super.onClick();
        ProtocolManager.fake_qureyQRStrProtocol(mRequest , mProtocol, this);
    }
}
