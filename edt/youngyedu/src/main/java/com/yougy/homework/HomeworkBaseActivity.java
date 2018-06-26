package com.yougy.homework;

import android.os.Bundle;

import com.yougy.common.activity.BaseActivity;
import com.yougy.common.manager.YoungyApplicationManager;

import rx.observables.ConnectableObservable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by FH on 2017/10/19.
 */

public abstract class HomeworkBaseActivity extends BaseActivity {
    //设置是否需要在Activity onStop之后仍然接收RxBus的事件.默认为不接收,
    private boolean needRecieveEventAfterOnStop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tag = this.getClass().getName();
    }


    public boolean isNeedRecieveEventAfterOnStop() {
        return needRecieveEventAfterOnStop;
    }

    public HomeworkBaseActivity setNeedRecieveEventAfterOnStop(boolean needRecieveEventAfterOnStop) {
        this.needRecieveEventAfterOnStop = needRecieveEventAfterOnStop;
        return this;
    }


}
