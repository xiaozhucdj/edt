package com.yougy.plide;

import android.widget.ImageView;

/**
 * Created by FH on 2018/7/10.
 */

public class PlideQueryPageCountRequest extends PlideRequest {
    protected PlideQueryPageCountRequest(ImageView imageView , String mUrl , PlideOpenDocumentRequest preRequest) {
        super(imageView, mUrl);
        this.mPreRequest = preRequest;
    }

    @Override
    public void run() throws InterruptedException {
        int totalPageCount = getProcessor().getPresenter().getTotalPages();
        PlideRequestProcessor.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mInnerLogicListener != null){
                    mInnerLogicListener.onLoadStatusChanged(PlideLoadListener.STATUS.OPEN_DOCUMENT_ING , mUrl , -999 , totalPageCount , null , null);
                }
            }
        });

    }

    @Override
    public void onCancelled() {
        PlideRequestProcessor.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mInnerLogicListener != null){
                    mInnerLogicListener.onLoadStatusChanged(PlideLoadListener.STATUS.ERROR , mUrl , -999 , -999 , PlideLoadListener.ERROR_TYPE.USER_CANCLE  , "取消查询");
                }
            }
        });
    }
    @Override
    public String toString() {
        return "PlideQueryPageCountRequest@" + Integer.toHexString(hashCode());
    }
}
