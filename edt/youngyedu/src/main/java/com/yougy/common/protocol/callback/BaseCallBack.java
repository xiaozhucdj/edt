package com.yougy.common.protocol.callback;

import android.content.Context;

import com.yougy.common.utils.LogUtils;
import com.yougy.view.dialog.LoadingErrorDialog;
import com.yougy.view.dialog.LoadingProgressDialog;
import com.zhy.http.okhttp.callback.Callback;

import java.lang.ref.WeakReference;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by jiangliang on 2016/10/25.
 */

public abstract class BaseCallBack<T> extends Callback<T> implements LoadingErrorDialog.OnScreenClickListenr {
    private static final String TAG = "BaseCallback";
    private LoadingProgressDialog loadingDialog;
    private LoadingErrorDialog loadErrorDialog;
    protected WeakReference<Context> mWeakReference;

    public BaseCallBack(Context context) {

        mWeakReference = new WeakReference<>(context);

        loadingDialog = new LoadingProgressDialog(mWeakReference.get());
        loadErrorDialog = new LoadingErrorDialog(mWeakReference.get());
        loadErrorDialog.setOnScreenClickListener(this);
    }


    @Override
    public void onBefore(Request request, int id) {
        LogUtils.e(TAG, "onBefore......................");
        showLoadingDialog();
    }

    @Override
    public void onAfter(int id) {
        LogUtils.e(TAG, "onAfeter.........................");
        hideLoadingDialog();
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        e.printStackTrace();
        LogUtils.e(TAG,"onError..........................");
        if (!loadErrorDialog.isShowing()) {
            loadErrorDialog.show();
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onClick() {
        if (loadErrorDialog.isShowing()) {
            loadErrorDialog.dismiss();
        }
    }

    public void showLoadingDialog(){
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    public void hideLoadingDialog(){
        if (loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }

}
