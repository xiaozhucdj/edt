package com.yougy.common.protocol.callback;

import android.content.Context;

import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.request.NewBookShelfReq;
import com.yougy.common.protocol.response.BookShelfProtocol;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/5/18.
 *  获取图书列表
 */
public class NewTextBookCallBack extends CacheInfoBack<BookShelfProtocol> {
   private NewBookShelfReq shelfReq ;
    public NewTextBookCallBack(Context context ,NewBookShelfReq req ) {
        super(context);
        shelfReq = req ;
    }

    @Override
    public BookShelfProtocol parseNetworkResponse(Response response, int id) throws Exception {
        mJson = response.body().string();
        LogUtils.i("response json ...." + mJson);
        //打开缓存判断
        BookShelfProtocol protocol=  GsonUtil.fromJson(mJson, BookShelfProtocol.class);
       if (protocol!=null && protocol.getCode() == NewProtocolManager.NewCodeResult.CODE_SUCCESS){
           operateCacheInfo(id);
       }
        return protocol ;
    }

    @Override
    public void onResponse(BookShelfProtocol response, int id) {
            RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
            rxBus.send(response);
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        LogUtils.i("获取图书列表 失败");
        if (id != NewProtocolManager.NewProtocolId.ID_BOOK_SHELF){
            RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
            rxBus.send(id+"");
        }else{
            super.onError(call, e, id);
        }
    }

    @Override
    public void onClick() {
        super.onClick();
        NewProtocolManager.bookShelf(shelfReq,this);
    }
}
