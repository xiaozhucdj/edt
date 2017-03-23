package com.yougy.shop.callback;

import android.content.Context;

import com.yougy.common.bean.Result;
import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.callback.BaseCallBack;
import com.yougy.common.protocol.request.PromoteBookRequest;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.ResultUtils;
import com.yougy.init.bean.BookInfo;

import java.util.List;

import okhttp3.Response;

/**
 * Created by jiangliang on 2017/2/18.
 */

public class PromoteBookCallBack extends BaseCallBack<Result<List<BookInfo>>> {

    public PromoteBookCallBack(Context context) {
        super(context);
    }

    @Override
    public Result<List<BookInfo>> parseNetworkResponse(Response response, int id) throws Exception {
        String str = response.body().string();
        LogUtils.e(getClass().getName(),str);
        return ResultUtils.fromJsonArray(str, BookInfo.class);
    }

    @Override
    public void onResponse(Result<List<BookInfo>> response, int id) {
        List<BookInfo> bookList = response.getData();
        RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
        rxBus.send(bookList);
    }

    @Override
    public void onClick() {
        PromoteBookRequest request = new PromoteBookRequest();
        ProtocolManager.promoteBookProtocol(request, ProtocolId.PROTOCOL_ID_PROMOTE_BOOK,this);
    }
}
