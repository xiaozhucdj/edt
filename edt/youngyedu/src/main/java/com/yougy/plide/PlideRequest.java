package com.yougy.plide;

import android.widget.ImageView;

import com.yougy.plide.pipe.Ball;


/**
 * Created by Administrator on 2018/7/7.
 */

public abstract class PlideRequest extends Ball {
    protected String mUrl;
    protected ImageView mImageView;
    protected PlideRequest mPreRequest = null;

    protected PlideLoadListener mCustomListener;
    protected PlideLoadListener mInnerLogicListener;
    private PlideRequestProcessor mProcessor;

    public PlideRequestProcessor getProcessor() {
        return mProcessor;
    }

    public void setProcessor(PlideRequestProcessor processor) {
        this.mProcessor = processor;
    }

    protected PlideRequest(ImageView imageView , String mUrl) {
        super(true);
        this.mImageView = imageView;
        this.mUrl = mUrl;
    }

    protected ImageView getmImageView() {
        return mImageView;
    }


    protected void setInnerLogicListener(PlideLoadListener listener){
        this.mInnerLogicListener = listener;
    }

    protected void setCustomListener(PlideLoadListener listener){
        this.mCustomListener = listener;
    }

    protected void callListeners(PlideLoadListener.STATUS newStatus , String url , int toPageIndex , int totalPageCount , PlideLoadListener.ERROR_TYPE errorType , String errorMsg){
        if (mCustomListener != null){
            mCustomListener.onLoadStatusChanged(newStatus , url , toPageIndex , totalPageCount , errorType , errorMsg);
        }
        if (mInnerLogicListener != null){
            mInnerLogicListener.onLoadStatusChanged(newStatus , url , toPageIndex , totalPageCount , errorType , errorMsg);
        }
    }

}
