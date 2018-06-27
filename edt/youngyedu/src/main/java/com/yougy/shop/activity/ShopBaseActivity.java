package com.yougy.shop.activity;

import android.os.Bundle;

import com.yougy.common.activity.BaseActivity;
import com.yougy.common.manager.YoungyApplicationManager;

import rx.observables.ConnectableObservable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by jiangliang on 2017/2/8.
 */

public abstract class ShopBaseActivity extends BaseActivity {
    protected CompositeSubscription subscription;
    protected ConnectableObservable<Object> tapEventEmitter;
    protected String tag;
    //设置是否需要在Activity onStop之后仍然接收RxBus的事件.默认为不接收,
    private boolean needRecieveEventAfterOnStop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tag = this.getClass().getName();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (subscription == null){
            subscription = new CompositeSubscription();
            tapEventEmitter = YoungyApplicationManager.getRxBus(this).toObserverable().publish();
            handleEvent();
        }
    }

    protected void handleEvent() {
        subscription.add(tapEventEmitter.connect());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!needRecieveEventAfterOnStop){
            if (subscription != null) {
                subscription.clear();
                subscription = null;
            }
            tapEventEmitter = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null) {
            subscription.clear();
            subscription = null;
        }
        tapEventEmitter = null;
    }

    public boolean isNeedRecieveEventAfterOnStop() {
        return needRecieveEventAfterOnStop;
    }

    public ShopBaseActivity setNeedRecieveEventAfterOnStop(boolean needRecieveEventAfterOnStop) {
        this.needRecieveEventAfterOnStop = needRecieveEventAfterOnStop;
        return this;
    }


}
