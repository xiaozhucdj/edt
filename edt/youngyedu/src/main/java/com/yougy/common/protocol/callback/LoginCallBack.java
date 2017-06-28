package com.yougy.common.protocol.callback;

/**
 * Created by FH on 2016/11/17.
 */

import android.content.Context;
import android.util.Log;

import com.yougy.common.global.Commons;
import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.response.NewLoginRep;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.view.dialog.UiPromptDialog;

import okhttp3.Call;
import okhttp3.Response;

/***
 * 登录接口回调
 */
public class LoginCallBack extends BaseCallBack<NewLoginRep> {
    public static class Error{
        public Exception e;
        public UiPromptDialog mUiPromptDialog;

        public Error(Exception e, UiPromptDialog mUiPromptDialog) {
            this.e = e;
            this.mUiPromptDialog = mUiPromptDialog;
        }
    }

    public LoginCallBack(Context context) {
        super(context);
    }

    @Override
    public NewLoginRep parseNetworkResponse(Response response, int id) throws Exception {
        String str = response.body().string();
        LogUtils.i("response json ...." + str);
        LogUtils.e("parseNetwork",Thread.currentThread().getName());
        return GsonUtil.fromJson(str, NewLoginRep.class);
    }

    @Override
    public void onResponse(NewLoginRep response, int id) {
        LogUtils.e("onResponse",Thread.currentThread().getName());
        RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
        rxBus.send(response);
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        Log.v("FH" , "..............登录失败 onError" + e.getMessage());
        e.printStackTrace();
        loadingDialog.dismiss();
        RxBus rxBus = YougyApplicationManager.getRxBus(mWeakReference.get());
        Error error = new Error(e , mUiPromptDialog);
        rxBus.send(error);
    }

    @Override
    public void onUiDetermineListener() {
        ProtocolManager.loginProtocol(Commons.UUID, ProtocolId.PROTOCOL_ID_LOGIN, this);
    }

    public interface OnJumpListener {
        void jumpActivity(Class clazz);
    }
}