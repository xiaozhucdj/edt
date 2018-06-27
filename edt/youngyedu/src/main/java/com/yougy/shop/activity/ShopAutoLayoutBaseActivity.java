package com.yougy.shop.activity;

import android.os.Bundle;
import android.view.WindowManager;

import com.yougy.common.activity.AutoLayoutBaseActivity;
import com.yougy.common.manager.YoungyApplicationManager;
import com.yougy.common.utils.LogUtils;

import butterknife.ButterKnife;
import rx.observables.ConnectableObservable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by FH on 2017/3/6.
 */

public abstract class ShopAutoLayoutBaseActivity extends AutoLayoutBaseActivity {
    protected CompositeSubscription subscription;
    protected ConnectableObservable<Object> tapEventEmitter;
    protected String tag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView();
        ButterKnife.bind(this);
        tag = this.getClass().getName();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    /**
     * 设置界面布局文件
     */
    protected abstract void setContentView();

    @Override
    protected void onResume() {
        LogUtils.e("FH" , "=== onResume");
        super.onResume();
        subscription = new CompositeSubscription();
        tapEventEmitter = YoungyApplicationManager.getRxBus(this).toObserverable().publish();
        handleEvent();
    }

    protected void handleEvent() {
        subscription.add(tapEventEmitter.connect());
    }

    @Override
    protected void onStop() {
        LogUtils.e("FH" , "=== onStop");
        super.onStop();
        if (subscription != null) {
            subscription.clear();
            subscription = null;
        }
        tapEventEmitter = null;
    }
}
