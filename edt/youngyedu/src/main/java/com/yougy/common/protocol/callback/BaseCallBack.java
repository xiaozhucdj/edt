package com.yougy.common.protocol.callback;

import android.content.Context;

import com.yougy.common.utils.LogUtils;
import com.yougy.ui.activity.R;
import com.yougy.view.dialog.LoadingProgressDialog;
import com.yougy.view.dialog.UiPromptDialog;
import com.zhy.http.okhttp.callback.Callback;

import java.lang.ref.WeakReference;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by jiangliang on 2016/10/25.
 */

public abstract class BaseCallBack<T> extends Callback<T> implements UiPromptDialog.Listener {
    private static final String TAG = "BaseCallback";
    private LoadingProgressDialog loadingDialog;
    protected WeakReference<Context> mWeakReference;
    protected UiPromptDialog mUiPromptDialog;
    protected Context mContext ;

    public BaseCallBack(Context context) {
        mContext = context ;
        mWeakReference = new WeakReference<>(context);

        loadingDialog = new LoadingProgressDialog(mWeakReference.get());
        mUiPromptDialog = new UiPromptDialog(mWeakReference.get());
        mUiPromptDialog.setListener(this);
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
        if (!mUiPromptDialog.isShowing()) {
            mUiPromptDialog.show();
            mUiPromptDialog.setDialogStyle(false);
            mUiPromptDialog.setCancel(R.string.cancel);
            mUiPromptDialog.setConfirm(R.string.retry);
            mUiPromptDialog.setTitle(R.string.text_connect_timeout);
            loadingDialog.dismiss();
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

    @Override
    public void onUiCancelListener() {
        dissMissUiPromptDialog();
    }

    @Override
    public void onUiDetermineListener() {
        dissMissUiPromptDialog();
    }

    @Override
    public void onUiCenterDetermineListener() {

    }

    private void dissMissUiPromptDialog( ) {
        if (mUiPromptDialog != null && mUiPromptDialog.isShowing()) {
            mUiPromptDialog.dismiss();
        }
    }
}
