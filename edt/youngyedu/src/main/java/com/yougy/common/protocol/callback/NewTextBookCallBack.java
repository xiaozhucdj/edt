package com.yougy.common.protocol.callback;

import android.content.Context;

import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.request.NewBookShelfReq;
import com.yougy.common.protocol.response.NewBookShelfRep;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.DataCacheUtils;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.UIUtils;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/5/18.
 *  获取图书列表
 */
public class NewTextBookCallBack extends CacheInfoBack<NewBookShelfRep> {
   private NewBookShelfReq shelfReq ;
    public NewTextBookCallBack(Context context ,NewBookShelfReq req ) {
        super(context);
        shelfReq = req ;
    }

    @Override
    public NewBookShelfRep parseNetworkResponse(Response response, int id) throws Exception {
        mJson = response.body().string();
        LogUtils.i("response json ...." + mJson);
        //打开缓存判断
        NewBookShelfRep protocol=  GsonUtil.fromJson(mJson, NewBookShelfRep.class);
       if (protocol!=null && protocol.getCode() == NewProtocolManager.NewCodeResult.CODE_SUCCESS){
            //operateCacheInfo(id);
           //文件方式 缓存 JSON

           if (protocol.getData()!=null && protocol.getData().size()>0){
               DataCacheUtils.putString(UIUtils.getContext(), id+"", GsonUtil.toJson(protocol.getData()));
           }else{
               DataCacheUtils.putString(UIUtils.getContext(), id+"", "");
           }
       }
        return protocol ;
    }

    @Override
    public void onResponse(NewBookShelfRep response, int id) {
            RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
            rxBus.send(response);
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        e.printStackTrace();
        LogUtils.i("获取图书列表 失败");
        if (id != NewProtocolManager.NewProtocolId.ID_BOOK_SHELF){
            RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
            rxBus.send(id+"");
        }else{
            super.onError(call, e, id);
        }
    }


    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        NewProtocolManager.bookShelf(shelfReq,this);
    }
}
