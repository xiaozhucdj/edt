package com.yougy.plide;

import android.widget.ImageView;

import com.yougy.common.utils.LogUtils;


/**
 * Created by FH on 2018/7/7.
 */

public class PlideController {
    private String mUrl;
    private boolean mUseCache;
    private PlideLoadListener mCustomListener;
    private ImageView mImageView;
    private PlideOpenDocumentRequest mPreRequest;

    protected PlideController(ImageView imageView , String mUrl , boolean useCache , PlideLoadListener customListener , PlideOpenDocumentRequest preRequest) {
        this.mUrl = mUrl;
        this.mImageView = imageView;
        this.mUseCache = useCache;
        this.mCustomListener = customListener;
        this.mPreRequest = preRequest;
    }

    public void toPage(int pageIndex , PlideLoadListener specificLlistener){
        if (pageIndex < 0){
            LogUtils.e("FH-----Plide-----toPage 失败,pageIndex : " + pageIndex + " 不合法");
            throw new PlideRunTimeException("FH-----Plide-----toPage 失败,pageIndex : " + pageIndex + " 不合法");
        }
        PlideToPageRequest toPageRequest = new PlideToPageRequest(mImageView , pageIndex , mUrl , mPreRequest);
        toPageRequest.setCustomListener(mCustomListener);
        if (specificLlistener != null){
            toPageRequest.setInnerLogicListener(specificLlistener);
        }
        Plide2.getInstance().sendNewRequest(toPageRequest);
    }

    public void toPage_sync(int pageIndex) throws PlideException {
        if (pageIndex < 0){
            LogUtils.e("FH-----Plide-----toPage 失败,pageIndex : " + pageIndex + " 不合法");
            throw new PlideRunTimeException("FH-----Plide-----toPage 失败,pageIndex : " + pageIndex + " 不合法");
        }
        PlideToPageRequest toPageRequest = new PlideToPageRequest(mImageView , pageIndex , mUrl , mPreRequest);
        toPageRequest.setCustomListener(mCustomListener);
        int[] result = new int[]{-999};
        toPageRequest.setInnerLogicListener(new PlideLoadListener() {
            @Override
            public void onLoadStatusChanged(STATUS newStatus, String url, int toPageIndex, int totalPageCount , ERROR_TYPE errorType, String errorMsg) {
                switch (newStatus){
                    case ERROR:
                        synchronized (result){
                            switch (errorType){
                                case USER_CANCLE:
                                    result[0] = -2;//用户取消
                                    break;
                                case TO_PAGE_ERROR://toPage失败
                                    result[0] = -1;
                                    break;
                            }

                            result.notify();
                        }
                        break;
                    case TO_PAGE_SUCCESS:
                        synchronized (result){
                            result[0] = 0;//成功
                            result.notify();
                        }
                        break;
                }
            }
        });
        Plide2.getInstance().sendNewRequest(toPageRequest);
        synchronized (result){
            if (result[0] == -999){
                try {
                    result.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    throw new PlideException("toPage_sync 失败 " + PlideLoadListener.ERROR_TYPE.USER_CANCLE , PlideLoadListener.ERROR_TYPE.USER_CANCLE);
                }
            }
        }
        switch (result[0]){
            case 0://toPage成功
                return;
            case -1://toPage失败
                throw new PlideException("toPage_sync 失败 " + PlideLoadListener.ERROR_TYPE.TO_PAGE_ERROR , PlideLoadListener.ERROR_TYPE.TO_PAGE_ERROR);
            case -2://用户取消
                throw new PlideException("toPage_sync 失败 " + PlideLoadListener.ERROR_TYPE.USER_CANCLE , PlideLoadListener.ERROR_TYPE.USER_CANCLE);
        }
    }

    public int getPdfPageCount_sync(){
        Result<Integer> result = new Result<Integer>();
        result.setResultCode(-999);
        PlideQueryPageCountRequest queryPageCountRequest = new PlideQueryPageCountRequest(mImageView , mUrl , mPreRequest);
        queryPageCountRequest.setInnerLogicListener(new PlideLoadListener() {
            @Override
            public void onLoadStatusChanged(STATUS newStatus, String url, int toPageIndex, int totalPageCount, ERROR_TYPE errorType, String errorMsg) {
                if (newStatus == STATUS.OPEN_DOCUMENT_SUCCESS){
                    synchronized (result){
                        result.setResultCode(0);
                        result.setData(totalPageCount);
                        result.notify();
                    }
                }
                else if (newStatus == STATUS.ERROR){
                    synchronized (result){
                        result.setResultCode(-1);
                        result.setErrorMsg(errorMsg);
                        result.notify();
                    }
                }
            }
        });
        Plide2.getInstance().sendNewRequest(queryPageCountRequest);
        synchronized (result){
            if (result.getResultCode() == -999){
                try {
                    result.wait();
                    if (result.getResultCode() == 0){
                        return result.getData();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }
}
