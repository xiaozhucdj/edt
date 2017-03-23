package com.yougy.common.fragment;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.utils.LogUtils;

import rx.observables.ConnectableObservable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by jiangliang on 2016/12/12.
 */

public abstract class BFragment extends Fragment {
    public boolean mHide;
    protected CompositeSubscription subscription;
    protected ConnectableObservable<Object> tapEventEmitter;
    protected Context context;
    private String tag;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        tag = getClass().getName();
        LogUtils.e(tag, "onAttach...........");
        subscription = new CompositeSubscription();
        tapEventEmitter = YougyApplicationManager.getRxBus(context).toObserverable().publish();
        handleEvent();
    }

    protected void handleEvent() {
        subscription.add(tapEventEmitter.connect());
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.e(tag, "onResume...........");
      /*  subscription = new CompositeSubscription();
        tapEventEmitter = YougyApplicationManager.getRxBus(context).toObserverable().publish();
        handleEvent();*/
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mHide = hidden;
        LogUtils.e(tag, "mhide is : " + hidden);
        if (subscription != null) {
            subscription.clear();
            subscription = null;
        }
        tapEventEmitter = null;
        if (!hidden) {
            subscription = new CompositeSubscription();
            tapEventEmitter = YougyApplicationManager.getRxBus(context).toObserverable().publish();
            handleEvent();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.e(tag, "onDestroyView............");
        if (subscription != null) {
            subscription.clear();
        }
        subscription = null;
        tapEventEmitter = null;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        LogUtils.e(tag, "setMenuVisibility...");
        if (getView() != null) {
            getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
        }
        mHide = menuVisible;
        if (menuVisible && context != null) {
            subscription = new CompositeSubscription();
            tapEventEmitter = YougyApplicationManager.getRxBus(context).toObserverable().publish();
            handleEvent();
        }else{
            if (subscription != null) {
                subscription.clear();
                subscription = null;
            }
            tapEventEmitter = null;
        }
    }
}
