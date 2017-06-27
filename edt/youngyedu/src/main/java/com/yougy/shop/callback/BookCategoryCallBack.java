package com.yougy.shop.callback;

import android.content.Context;

import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.callback.BaseCallBack;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.shop.bean.CategoryInfo;

import okhttp3.Response;

/**
 * Created by jiangliang on 2017/2/18.
 */

public class BookCategoryCallBack extends BaseCallBack<CategoryInfo> {


    public BookCategoryCallBack(Context context) {
        super(context);
    }

    @Override
    public CategoryInfo parseNetworkResponse(Response response, int id) throws Exception {
        String str = response.body().string();
        LogUtils.e(getClass().getName(),str);
        return GsonUtil.fromJson(str,CategoryInfo.class);
    }

    @Override
    public void onResponse(CategoryInfo response, int id) {
        RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
        rxBus.send(response);
    }

    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        ProtocolManager.queryBookCategoryProtocol(SpUtil.getUserId(),-1, ProtocolId.PROTOCOL_ID_QUERY_BOOK_CATEGORY,this);
    }
}
