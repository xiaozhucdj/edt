package com.yougy.rx_subscriber;

import android.content.Context;

import com.yougy.common.utils.LogUtils;
import com.yougy.view.dialog.LoadingErrorDialog;
import com.yougy.view.dialog.LoadingProgressDialog;

import java.lang.ref.WeakReference;

import rx.Subscriber;

/**
 * Created by jiangliang on 2017/5/19.
 */

public abstract class ShopSubscriber<T> extends Subscriber<T> implements LoadingErrorDialog.OnScreenClickListenr {
    private LoadingProgressDialog loadingDialog;
    private LoadingErrorDialog loadErrorDialog;
    protected WeakReference<Context> mWeakReference;

    public ShopSubscriber(Context context){
        mWeakReference = new WeakReference<>(context);
        loadingDialog = new LoadingProgressDialog(mWeakReference.get());
        loadErrorDialog = new LoadingErrorDialog(mWeakReference.get());
        loadErrorDialog.setOnScreenClickListener(this);
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
        if (!loadErrorDialog.isShowing()) {
            loadErrorDialog.show();
            loadingDialog.dismiss();
        }
    }
    public abstract void require();
    @Override
    public void onClick() {
        if (isUnsubscribed()){
            unsubscribe();
        }
        require();
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
