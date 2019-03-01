package com.yougy.anwser;

/**
 * Created by FH on 2017/4/13.
 */

import com.yougy.common.utils.LogUtils;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 自己写的一个基于rx的定时任务执行器
 */
public class TimedTask {
    public enum TYPE {
        //循环执行
        CIRCULATION,
        //立即执行一次,然后循环执行
        IMMEDIATELY_AND_CIRCULATION,
        //执行一次
        FOR_ONCE
    }

    private TYPE type = TYPE.FOR_ONCE;
    private long timeMill = 1000;
    private Observable<Integer> observable;
    private boolean tictac = false;
    private Subscription subscription;

    public TimedTask(final TYPE type, final long timeMill) {
        this.type = type;
        this.timeMill = timeMill;
        observable = Observable
                .create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        if (type == TYPE.FOR_ONCE){
                            subscriber.onNext(1);
                        }
                        else if (type == TYPE.CIRCULATION || type == TYPE.IMMEDIATELY_AND_CIRCULATION){
                            tictac = true;
                            int i = 1;
                            if (type == TYPE.IMMEDIATELY_AND_CIRCULATION){
                                subscriber.onNext(i++);
                            }
                            while (tictac) {
                                try {
                                    Thread.sleep(timeMill);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                subscriber.onNext(i++);
                            }
                        }
                        if (subscription != null){
                            subscription.unsubscribe();
                            subscription = null;
                        }
                    }
                });
    }

    public TimedTask start(Action1<Integer> action){
        if (action != null){
            if (subscription == null){
                subscription = observable.subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(action, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                LogUtils.e("FH_TimedTask" , "TimedTask 订阅出错,错误打印:" + throwable.getMessage());
                                throwable.printStackTrace();
                            }
                        });
            }
        }
        return this;
    }

    public TimedTask stop(){
        tictac = false;
        return this;
    }

}
