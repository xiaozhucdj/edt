package com.yougy.plide.pipe;

import android.os.Bundle;

/**
 * Created by FH on 2018/1/11.
 */

public class Ball {
    private boolean isCancled = false;
    private Bundle data;
    private long timeStamp;
    private boolean mCancleOthers;
    public Ball(boolean cancleOthers) {
        timeStamp = System.currentTimeMillis();
        mCancleOthers = cancleOthers;
    }

    public boolean isCancled() {
        return isCancled;
    }

    public Bundle getData() {
        return data;
    }

    public void cancle(){
        this.isCancled = true;
    }

    public void setData(Bundle data) {
        this.data = data;
    }

    public void inserCheckPoint() throws InterruptedException {
        if (isCancled){
            throw new InterruptedException("Ball---检查点被触发!这个ball已经被cancle!");
        }
    }

    public void run() throws InterruptedException {

    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public boolean needCancleOthers() {
        return mCancleOthers;
    }
}
