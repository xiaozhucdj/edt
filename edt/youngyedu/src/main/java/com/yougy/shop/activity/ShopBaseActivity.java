package com.yougy.shop.activity;

import android.os.Bundle;

import com.yougy.common.activity.BaseActivity;
import com.yougy.common.manager.YougyApplicationManager;

import butterknife.ButterKnife;
import rx.observables.ConnectableObservable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by jiangliang on 2017/2/8.
 */

public abstract class ShopBaseActivity extends BaseActivity {
    protected CompositeSubscription subscription;
    protected ConnectableObservable<Object> tapEventEmitter;
    protected String tag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView();
        ButterKnife.bind(this);
        tag = this.getClass().getName();
        super.onCreate(savedInstanceState);
    }

    /**
     * 设置界面布局文件
     */
    protected abstract void setContentView();

    @Override
    protected void onResume() {
        super.onResume();
        subscription = new CompositeSubscription();
        tapEventEmitter = YougyApplicationManager.getRxBus(this).toObserverable().publish();
        handleEvent();
    }

    protected void handleEvent() {
        subscription.add(tapEventEmitter.connect());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (subscription != null) {
            subscription.clear();
            subscription = null;
        }
        tapEventEmitter = null;
    }
}
