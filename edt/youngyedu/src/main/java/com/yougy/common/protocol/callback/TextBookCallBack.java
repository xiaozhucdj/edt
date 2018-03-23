package com.yougy.common.protocol.callback;

import android.content.Context;

import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.response.BookShelfProtocol;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtils;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by jiangliang on 2016/12/13.
 */

public class TextBookCallBack extends CacheInfoBack<BookShelfProtocol> {
    private int mTermIndex;
    private int mCategoryId;
    private int mProtocolId ;

    public TextBookCallBack(Context context ,int protocolId) {
        super(context);
        mProtocolId = protocolId;
    }

    @Override
    public BookShelfProtocol parseNetworkResponse(Response response, int id) throws Exception {
        mJson = response.body().string();
        LogUtils.i("response json ...." + mJson);
        // 打开缓存
        operateCacheInfo(id);
        return GsonUtil.fromJson(mJson, BookShelfProtocol.class);
    }

    @Override
    public void onResponse(BookShelfProtocol response, int id) {

        if (response.getCode() == ProtocolId.RET_SUCCESS) {
                RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
                rxBus.send(response);
        }
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        LogUtils.i("yuanye...请求服务器 加载出错 ---onError");
        /**
         *  当绑定数据的时候我们需要下载全部图书
         *  当全部下载图书出问题后不需要发消息，直接弹出错误框
         *  而当本学期下载图书失败的时候需要 使用缓存数据 所以这块的判断需要这么写
         */
        if (id != ProtocolId.PROTOCOL_ID_BOOK_SHELF){
            RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
            rxBus.send(id+"");
        }else{
            super.onError(call, e, id);
        }

    }

    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        ProtocolManager.bookShelfProtocol(SpUtils.getAccountId(), mTermIndex, mCategoryId, "", mProtocolId, this);
    }

    public  String getJSON(){
        return mJson;
    }

    public void  setTermIndex(int termIndex){
        mTermIndex = termIndex ;
    }

    public void  setCategoryId(int categoryId){
        mCategoryId = categoryId ;
    }


}