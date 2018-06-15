package com.yougy.common.protocol.callback;

import android.content.Context;

import com.google.gson.Gson;
import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.manager.YoungyApplicationManager;
import com.yougy.common.protocol.request.NewQueryNoteReq;
import com.yougy.common.protocol.response.NewQueryNoteRep;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.DataCacheUtils;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.StringUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.bean.NoteInfo;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static com.yougy.common.utils.GsonUtil.fromNotes;

/**
 * Created by Administrator on 2017/5/19.
 */

public class NewNoteBookCallBack extends CacheInfoBack<NewQueryNoteRep> {
    private NewQueryNoteReq newQueryNoteReq;

    public NewNoteBookCallBack(Context context, NewQueryNoteReq req) {
        super(context);
        newQueryNoteReq = req;
    }

    @Override
    public NewQueryNoteRep parseNetworkResponse(Response response, int id) throws Exception {
        mJson = response.body().string();
        LogUtils.i("note json ===" + mJson);
        NewQueryNoteRep rep =   new Gson().fromJson(mJson, NewQueryNoteRep.class);
        if (rep.getCode() == NewProtocolManager.NewCodeResult.CODE_SUCCESS){
            //缓存笔记JSON
            if (rep.getData()!=null && rep.getData().size()>0){
                DataCacheUtils.putString(UIUtils.getContext(), id+"", GsonUtil.toJson(rep.getData()));
            }else{
                DataCacheUtils.putString(UIUtils.getContext(), id+"", "");
            }
        }

        //查询离线笔记 ,并且添加到对尾
        List<NoteInfo> infos =  getOnffLine() ;
        if (infos!=null && infos.size()>0){
            if (rep.getData() == null){
                rep.setData(infos) ;
            }else{
                rep.getData().addAll(infos) ;
            }
        }
        return rep ;
    }

    private List<NoteInfo> getOnffLine() {
        // 离线添加的笔记
        String offLineAddStr = DataCacheUtils.getString(UIUtils.getContext(), NewProtocolManager.OffLineId.OFF_LINE_ADD);
        List<NoteInfo> books = new ArrayList<>();
        if (!StringUtils.isEmpty(offLineAddStr)) {
            if (books != null) {
                books.addAll(fromNotes(offLineAddStr));
            }
        }
        return books;
    }
    @Override
    public void onResponse(NewQueryNoteRep response, int id) {
            RxBus rxBus = YoungyApplicationManager.getRxBus(mWeakReference.get());
            rxBus.send(response);
    }

    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
//        NewProtocolManager.queryNote(newQueryNoteReq ,this);
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        e.printStackTrace();
        LogUtils.i("onError ....请求获取 笔记失败  ");
        RxBus rxBus = YoungyApplicationManager.getRxBus(mWeakReference.get());
        rxBus.send(id + "");
    }
}
