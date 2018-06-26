package com.yougy.task;

import android.view.View;

import com.frank.etude.pageBtnBar.PageBtnBar;
import com.frank.etude.pageBtnBar.PageBtnBarAdapter;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.manager.YoungyApplicationManager;
import com.yougy.common.utils.LogUtils;


import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by lenovo on 2018/6/19.
 */

public abstract class TaskBaseActivity extends BaseActivity {
    protected CompositeSubscription subscription;
    protected ConnectableObservable<Object> connectableObservable;
    protected boolean needRecieveEventAfterOnStop = false;

    @Override
    protected void init() {
        subscription = new CompositeSubscription();
        connectableObservable = YoungyApplicationManager.getRxBus(this).toObserverable().publish();
    }

    @Override
    protected void loadData() {
        if (showNoNetDialog()) {
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        handleYXMessage();
    }

    protected void handleYXMessage () {
        subscription.add(connectableObservable.connect());
        connectableObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        yxMsgObserverCall(o);
                    }
                });
    }

    protected abstract void yxMsgObserverCall (Object o);

    @Override
    protected void onStop() {
        super.onStop();
        removeYXMsgSubscription();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeYXMsgSubscription();
    }

    private void removeYXMsgSubscription() {
        if (needRecieveEventAfterOnStop){
            if (subscription != null) {
                subscription.clear();
                subscription = null;
            }
            connectableObservable = null;
        }
    }

//    /**
//     * 初始化翻页角标
//     */
//    private void initPages() {
//
////        //删除之前的按钮
////        //设置显示按钮
////        addBtnBarCounts(counts);
//        currentDatas.clear();
//        if (datas.size() > countPerPage) { // 大于1页
//            currentDatas.addAll(datas.subList(0, countPerPage));
//        } else {
//            LogUtils.i("initPages2.."); //小于1页
//            currentDatas.addAll(datas.subList(0, datas.size()));
//        }
//        taskAdapter.notifyDataSetChanged();
//    }


    public int getBtnBarCounts(int dataSize, int countPerPage) {
        int counts = 0;
        int quotient = dataSize / countPerPage;
        int remainder = dataSize % countPerPage;
        if (quotient == 0) {
            if (remainder == 0) {
                //没有数据
                counts = 0;
            } else {
                //不足16个item
                counts = 1;
            }
        }
        if (quotient != 0) {
            if (remainder == 0) {
                //没有数据
                counts = quotient; //.正好是16的倍数
            } else {
                //不足16个item
                counts = quotient + 1; // 不足16个 +1
            }
        }
        return counts;
    }

     /**
     * @param counts  下面的Btn  page
     */
     public void addBtnBarCounts(PageBtnBar pageBtnBar, int counts) {
         pageBtnBar.setPageBarAdapter(new PageBtnBarAdapter(getApplicationContext()) {
            @Override
            public int getPageBtnCount() {
                return counts;
            }

            @Override
            public void onPageBtnClick(View btn, int btnIndex, String textInBtn) {
                refreshAdapterData(btnIndex+1);
            }
        });
         pageBtnBar.setCurrentSelectPageIndex(0);
         pageBtnBar.refreshPageBar();
    }

    protected void refreshAdapterData(int pagerIndex) {
        LogUtils.d("zhangyc base pagerIndex: " + pagerIndex);
    }
 }
