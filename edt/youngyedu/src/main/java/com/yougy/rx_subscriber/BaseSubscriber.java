package com.yougy.rx_subscriber;

import rx.Subscriber;

/**
 * Created by jiangliang on 2016/12/2.
 */

public abstract class BaseSubscriber<T> extends Subscriber<T> {
    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }
}
