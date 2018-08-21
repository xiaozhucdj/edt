package com.yougy.plide;

import android.widget.ImageView;

/**
 * Created by FH on 2018/7/10.
 */

public class PlideOpenDocumentRequest extends PlideRequest {
    protected PlideOpenDocumentRequest(ImageView imageView, String mUrl , PlideDownloadRequest preRequest) {
        super(imageView, mUrl);
        this.mPreRequest = preRequest;
        if (mPreRequest != null){
        }
    }

    @Override
    public void run() throws InterruptedException{
        PlideRequestProcessor.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callListeners(PlideLoadListener.STATUS.OPEN_DOCUMENT_ING , mUrl , -999 , -999 , null , null);
            }
        });
        PlideRequestProcessor.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mImageView.setImageBitmap(null);
            }
        });
        getProcessor().getPresenter().close(getProcessor());

//        if (closeLastResult.getResultCode() == -1){
//            getProcessor().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    callListeners(PlideLoadListener.STATUS.ERROR , mUrl , -999 , -999 , PlideLoadListener.ERROR_TYPE.OPEN_DOCUMENT_ERROR , closeLastResult.getErrorMsg());
//                }
//            });
//            throw new PlideRunTimeException("执行close失败");
//        }
        Result<Integer> result = getProcessor().getPresenter().openDocument(CommonDownloader.getSavePath(mUrl) , null);
        if (result.getResultCode() == 0){
            PlideRequestProcessor.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callListeners(PlideLoadListener.STATUS.OPEN_DOCUMENT_SUCCESS , mUrl , -999  , result.getData() , null , null);
                }
            });
        }
        else {
            PlideRequestProcessor.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callListeners(PlideLoadListener.STATUS.ERROR , mUrl , -999 , -999, PlideLoadListener.ERROR_TYPE.OPEN_DOCUMENT_ERROR , result.getErrorMsg());
                }
            });
            throw new PlideRunTimeException("执行openDocument失败");
        }
    }

    @Override
    public void onCancelled() {
        PlideRequestProcessor.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callListeners(PlideLoadListener.STATUS.ERROR , mUrl , -999 , -999 , PlideLoadListener.ERROR_TYPE.USER_CANCLE, "用户取消openDocument");
            }
        });
    }
    @Override
    public String toString() {
        return "PlideOpenDocumentRequest@" + Integer.toHexString(hashCode()) + " url=" + mUrl;
    }
}
