package com.yougy.anwser;

import android.os.Bundle;

import com.yougy.common.activity.BaseActivity;
import com.yougy.common.manager.YoungyApplicationManager;

import rx.observables.ConnectableObservable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by FH on 2017/2/8.
 */

public abstract class AnswerBaseActivity extends BaseActivity {
    protected CompositeSubscription subscription;
    protected ConnectableObservable<Object> tapEventEmitter;
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
        subscription = new CompositeSubscription();
        tapEventEmitter = YoungyApplicationManager.getRxBus(this).toObserverable().publish();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (needRecieveEventAfterOnStop){
            if (subscription != null) {
                subscription.clear();
                subscription = null;
            }
            tapEventEmitter = null;
        }
    }

    public boolean isNeedRecieveEventAfterOnStop() {
        return needRecieveEventAfterOnStop;
    }

    public AnswerBaseActivity setNeedRecieveEventAfterOnStop(boolean needRecieveEventAfterOnStop) {
        this.needRecieveEventAfterOnStop = needRecieveEventAfterOnStop;
        return this;
    }


}
