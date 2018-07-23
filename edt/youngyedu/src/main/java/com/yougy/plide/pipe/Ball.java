package com.yougy.plide.pipe;

import android.os.Bundle;

/**
 * Created by FH on 2018/1/11.
 */

public abstract class Ball {
    private boolean isCanceld = false;
    private Bundle data;
    private long timeStamp;
    private boolean mCancelOthers;

    public Ball(boolean cancleOthers) {
        timeStamp = System.currentTimeMillis();
        mCancelOthers = cancleOthers;
    }

    public boolean isCanceld() {
        return isCanceld;
    }

    public Bundle getData() {
        return data;
    }

    public void setData(Bundle data) {
        this.data = data;
    }

    public void cancel(){
        this.isCanceld = true;
    }

    public void inserCheckPoint() throws InterruptedException {
        if (isCanceld){
            throw new InterruptedException("Ball---检查点被触发!这个ball已经被cancel!");
        }
    }

    public abstract void run() throws InterruptedException;

    public abstract void onCancelled();

    public long getTimeStamp() {
        return timeStamp;
    }

    public boolean needCancelOthers() {
        return mCancelOthers;
    }
}
