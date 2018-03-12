package com.yougy.rx_subscriber;

import android.content.Context;

import com.yougy.common.activity.BaseActivity;
import com.yougy.common.utils.LogUtils;
import com.yougy.ui.activity.R;
import com.yougy.view.dialog.LoadingProgressDialog;
import com.yougy.view.dialog.UiPromptDialog;

import java.lang.ref.WeakReference;

import rx.Subscriber;

/**
 * Created by jiangliang on 2017/5/19.
 */

public abstract class ShopSubscriber<T> extends Subscriber<T> implements UiPromptDialog.Listener {
    private LoadingProgressDialog loadingDialog;
    protected WeakReference<Context> mWeakReference;
    protected Context mContext  ;
    private UiPromptDialog mUiPromptDialog;
    public ShopSubscriber(Context context){
        mContext = context ;
        mWeakReference = new WeakReference<>(context);
        loadingDialog = new LoadingProgressDialog(mWeakReference.get());
        mUiPromptDialog = new UiPromptDialog(mWeakReference.get()) ;
        mUiPromptDialog.setListener(this);
    }

    @Override
    public void onStart() {
        LogUtils.e(getClass().getName(),"on start .......... ");
        showLoadingDialog();
    }

    @Override
    public void onCompleted() {
        LogUtils.e(getClass().getName(),"on completed ...... ");
        hideLoadingDialog();
    }

    @Override
    public void onError(Throwable e) {
        LogUtils.e(getClass().getName(),"on error ...... " + e.getMessage());
        if (!mUiPromptDialog.isShowing()) {
            mUiPromptDialog.show();
            mUiPromptDialog.setDialogStyle(false);
            mUiPromptDialog.setCancel(R.string.cancel);
            mUiPromptDialog.setConfirm(R.string.retry);
            mUiPromptDialog.setTitle(R.string.text_connect_timeout);
            loadingDialog.dismiss();
        }
    }
    public abstract void require();

    private void showLoadingDialog(){
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    private void hideLoadingDialog(){
        if (loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onUiCancelListener() {
        dissMissUiPromptDialog();
        ((BaseActivity)mContext).finish();
    }

    @Override
    public void onUiDetermineListener() {
        dissMissUiPromptDialog();
        if (isUnsubscribed()){
           unsubscribe();
        }
        require();
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
