package com.yougy.plide;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.yougy.common.utils.LogUtils;


/**
 * Created by FH on 2018/7/7.
 */

public class PlideLoadRequestionBuilder {
    String mUrl;
    PlideLoadListener mListener = null;
    boolean mUseCache = true;

    public PlideLoadRequestionBuilder(Context context) {

    }

    public PlideLoadRequestionBuilder load(String url){
        mUrl = url;
        return this;
    }

    public PlideLoadRequestionBuilder setLoadListener(PlideLoadListener loadListener){
        mListener = loadListener;
        return this;
    }

    public PlideLoadRequestionBuilder setUseCache(boolean useCache) {
        this.mUseCache = useCache;
        return this;
    }


    public PlideController into(ImageView imageView , PlideLoadListener specificListener){
        if (TextUtils.isEmpty(mUrl)){
            LogUtils.e("FH-----Plide-----into 失败,url没有设置");
            throw new PlideRunTimeException("FH-----Plide-----into 失败,url没有设置");
        }
        if (imageView == null){
            throw new PlideRunTimeException("into(ImageView)方法错误,参数ImageView不能为null!");
        }

        PlideDownloadRequest downloadRequest = new PlideDownloadRequest(imageView , mUrl , mUseCache);
        PlideOpenDocumentRequest openDocumentRequest = new PlideOpenDocumentRequest(imageView , mUrl , downloadRequest);
        if (mListener != null){
            downloadRequest.setCustomListener(mListener);
            openDocumentRequest.setCustomListener(mListener);
        }
        if (specificListener != null){
            downloadRequest.setInnerLogicListener(new PlideLoadListener() {
                @Override
                public void onLoadStatusChanged(STATUS newStatus, String url, int toPageIndex , int totalPageCount , ERROR_TYPE errorType, String errorMsg) {
                    switch (newStatus){
                        case DOWNLOAD_SUCCESS:
                        case DOWNLOADING:
                        case ERROR:
                            specificListener.onLoadStatusChanged(newStatus , url , toPageIndex ,totalPageCount ,  errorType , errorMsg);
                            break;
                    }
                }
            });
            openDocumentRequest.setInnerLogicListener(new PlideLoadListener() {
                @Override
                public void onLoadStatusChanged(STATUS newStatus, String url, int toPageIndex, int totalPageCount, ERROR_TYPE errorType, String errorMsg) {
                    switch (newStatus){
                        case OPEN_DOCUMENT_ING:
                        case OPEN_DOCUMENT_SUCCESS:
                        case ERROR:
                            specificListener.onLoadStatusChanged(newStatus , url , toPageIndex , totalPageCount , errorType , errorMsg);
                            break;
                    }
                }
            });
        }
        Plide2.getInstance().sendNewRequest(downloadRequest);
        Plide2.getInstance().sendNewRequest(openDocumentRequest);
        return new PlideController(imageView , mUrl , mUseCache , mListener , openDocumentRequest);
    }


    public PlideController into(ImageView imageView , PlideLoadListener specificListener, boolean isAuto){
        if (TextUtils.isEmpty(mUrl)){
            LogUtils.e("FH-----Plide-----into 失败,url没有设置");
            throw new PlideRunTimeException("FH-----Plide-----into 失败,url没有设置");
        }
        if (imageView == null){
            throw new PlideRunTimeException("into(ImageView)方法错误,参数ImageView不能为null!");
        }

        PlideDownloadRequest downloadRequest = new PlideDownloadRequest(imageView , mUrl , mUseCache);
        PlideOpenDocumentRequest openDocumentRequest = new PlideOpenDocumentRequest(imageView , mUrl , downloadRequest, isAuto);
        if (mListener != null){
            downloadRequest.setCustomListener(mListener);
            openDocumentRequest.setCustomListener(mListener);
        }
        if (specificListener != null){
            downloadRequest.setInnerLogicListener(new PlideLoadListener() {
                @Override
                public void onLoadStatusChanged(STATUS newStatus, String url, int toPageIndex , int totalPageCount , ERROR_TYPE errorType, String errorMsg) {
                    switch (newStatus){
                        case DOWNLOAD_SUCCESS:
                        case DOWNLOADING:
                        case ERROR:
                            specificListener.onLoadStatusChanged(newStatus , url , toPageIndex ,totalPageCount ,  errorType , errorMsg);
                            break;
                    }
                }
            });
            openDocumentRequest.setInnerLogicListener(new PlideLoadListener() {
                @Override
                public void onLoadStatusChanged(STATUS newStatus, String url, int toPageIndex, int totalPageCount, ERROR_TYPE errorType, String errorMsg) {
                    switch (newStatus){
                        case OPEN_DOCUMENT_ING:
                        case OPEN_DOCUMENT_SUCCESS:
                        case ERROR:
                            specificListener.onLoadStatusChanged(newStatus , url , toPageIndex , totalPageCount , errorType , errorMsg);
                            break;
                    }
                }
            });
        }
        Plide2.getInstance().sendNewRequest(downloadRequest);
        Plide2.getInstance().sendNewRequest(openDocumentRequest);
        return new PlideController(imageView , mUrl , mUseCache , mListener , openDocumentRequest);
    }

    public PlideController into_sync(ImageView imageView) throws PlideException{
        if (TextUtils.isEmpty(mUrl)){
            LogUtils.e("FH-----Plide-----into 失败,url没有设置");
            throw new PlideRunTimeException("FH-----Plide-----into 失败,url没有设置");
        }
        if (imageView == null){
            throw new PlideRunTimeException("into(ImageView)方法错误,参数ImageView不能为null!");
        }

        PlideDownloadRequest downloadRequest = new PlideDownloadRequest(imageView , mUrl , mUseCache);
        PlideOpenDocumentRequest openDocumentRequest = new PlideOpenDocumentRequest(imageView , mUrl , downloadRequest);
        if (mListener != null){
            downloadRequest.setCustomListener(mListener);
            openDocumentRequest.setCustomListener(mListener);
        }
        //result内容 -1表示openDocument失败 , -2表示下载失败 , -3表示用户取消 ,0表示成功.
        int[] result = new int[]{-999};
        downloadRequest.setInnerLogicListener(new PlideLoadListener() {
            @Override
            public void onLoadStatusChanged(STATUS newStatus, String url, int toPageIndex, int totalPageCount , ERROR_TYPE errorType, String errorMsg) {
                switch (newStatus){
                    case ERROR:
                        synchronized (result){
                            if (errorType == ERROR_TYPE.USER_CANCLE){
                                result[0] = -3;//用户取消
                            }
                            else{
                                result[0] = -2;//下载失败
                            }
                            result.notify();
                        }
                        break;
                }
            }
        });
        openDocumentRequest.setInnerLogicListener(new PlideLoadListener() {
            @Override
            public void onLoadStatusChanged(STATUS newStatus, String url, int toPageIndex, int totalPageCount , ERROR_TYPE errorType, String errorMsg) {
                switch (newStatus){
                    case ERROR:
                        synchronized (result){
                            if (errorType == ERROR_TYPE.USER_CANCLE){
                                result[0] = -3;//用户取消
                            }
                            else {
                                result[0] = -1;//openDocument失败
                            }
                            result.notify();
                        }
                        break;
                    case OPEN_DOCUMENT_SUCCESS:
                        synchronized (result){
                            result[0] = 0;//成功
                            result.notify();
                        }
                        break;
                }
            }
        });
        Plide2.getInstance().sendNewRequest(downloadRequest);
        Plide2.getInstance().sendNewRequest(openDocumentRequest);
        synchronized (result){
            if (result[0] == -999){
                try {
                    result.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    throw new PlideException("into_sync失败:" + PlideLoadListener.ERROR_TYPE.USER_CANCLE , PlideLoadListener.ERROR_TYPE.USER_CANCLE);
                }
            }
            switch (result[0]){
                case 0://成功
                    return new PlideController(imageView , mUrl , mUseCache , mListener , openDocumentRequest);
                case -1://openDucument失败
                    throw new PlideException("into_sync失败:" + PlideLoadListener.ERROR_TYPE.OPEN_DOCUMENT_ERROR , PlideLoadListener.ERROR_TYPE.OPEN_DOCUMENT_ERROR);
                case -2://下载失败
                    throw new PlideException("into_sync失败:" + PlideLoadListener.ERROR_TYPE.DOWNLOAD_ERROR , PlideLoadListener.ERROR_TYPE.DOWNLOAD_ERROR);
                case -3://用户取消
                    throw new PlideException("into_sync失败:" + PlideLoadListener.ERROR_TYPE.USER_CANCLE , PlideLoadListener.ERROR_TYPE.USER_CANCLE);
                default:
                    throw new PlideException("into_sync失败:" + PlideLoadListener.ERROR_TYPE.UNKNOWN, PlideLoadListener.ERROR_TYPE.UNKNOWN);
            }
        }
    }
}
