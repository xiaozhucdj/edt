package com.yougy.common.protocol.callback;

import android.content.Context;
import android.util.Log;

import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.response.BookShelfProtocol;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.SpUtil;

import okhttp3.Response;

/**
 * Created by jiangliang on 2016/12/14.
 */

public class BookShelfCallBack extends BaseCallBack<BookShelfProtocol> {

    public BookShelfCallBack(Context context) {
        super(context);
    }

    @Override
    public BookShelfProtocol parseNetworkResponse(Response response, int id) throws Exception {
        String str = response.body().string();
        return GsonUtil.fromJson(str, BookShelfProtocol.class);
    }

    @Override
    public void onResponse(BookShelfProtocol response, int id) {

        if (response.getCode() == ProtocolId.RET_SUCCESS) {
            RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
            rxBus.send(response);
            Log.e("ShelfCallBack","send shelf event...");
        }
    }

    @Override
    public void onClick() {
        super.onClick();
        ProtocolManager.bookShelfProtocol(SpUtil.getAccountId(), -1, -1, "", ProtocolId.PROTOCOL_ID_BOOK_SHELF, this);
    }
}
